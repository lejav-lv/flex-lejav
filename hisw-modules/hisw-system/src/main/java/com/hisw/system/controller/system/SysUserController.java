package com.hisw.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.hisw.common.core.constant.UserConstants;
import com.hisw.common.core.core.domain.R;
import com.hisw.common.core.core.domain.model.LoginUser;
import com.hisw.common.core.utils.MapstructUtils;
import com.hisw.common.core.utils.StreamUtils;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.encrypt.annotation.ApiEncrypt;
import com.hisw.common.excel.core.ExcelResult;
import com.hisw.common.excel.utils.ExcelUtil;
import com.hisw.common.log.annotation.Log;
import com.hisw.common.log.enums.BusinessType;
import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.security.utils.LoginHelper;
import com.hisw.common.tenant.helper.TenantHelper;
import com.hisw.common.web.core.BaseController;
import com.hisw.system.domain.bo.SysDeptBo;
import com.hisw.system.domain.bo.SysPostBo;
import com.hisw.system.domain.bo.SysRoleBo;
import com.hisw.system.domain.bo.SysUserBo;
import com.hisw.system.domain.vo.*;
import com.hisw.system.listener.SysUserImportListener;
import com.hisw.system.service.*;
import com.mybatisflex.core.mask.MaskManager;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息
 *
 * @author lejav
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/user")
public class SysUserController extends BaseController {

    @Resource
    private ISysUserService userService;
    @Resource
    private ISysRoleService roleService;
    @Resource
    private ISysDeptService deptService;
    @Resource
    private ISysPostService postService;
    @Resource
    private ISysTenantService tenantService;

    /**
     * 获取用户列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/list")
    public TableDataInfo<SysUserVo> list(SysUserBo userBo) {
        return userService.selectPage(userBo);
    }

    /**
     * 导出用户列表
     */
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:user:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUserBo userBo) {
        List<SysUserVo> list = userService.selectUserList(userBo);
        List<SysUserExportVo> listVo = MapstructUtils.convert(list, SysUserExportVo.class);
        ExcelUtil.exportExcel(listVo, "用户数据", SysUserExportVo.class, response);
    }

    /**
     * 导入数据
     *
     * @param file          导入文件
     * @param updateSupport 是否更新已存在数据
     */
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @SaCheckPermission("system:user:import")
    @PostMapping("/importData")
    public R<Void> importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelResult<SysUserImportVo> result = ExcelUtil.importExcel(file.getInputStream(), SysUserImportVo.class, new SysUserImportListener(updateSupport));
        return R.ok(result.getAnalysis());
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "用户数据", SysUserImportVo.class, response);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getInfo")
    public R<UserInfoVo> getInfo() {
        UserInfoVo userInfoVo = new UserInfoVo();
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (LoginHelper.isSuperAdmin()) {
            // 超级管理员 如果重新加载用户信息需清除动态租户
            TenantHelper.clearDynamic();
        }
        SysUserVo user = userService.selectUserById(loginUser.getUserId());
        if (ObjectUtil.isNull(user)) {
            return R.fail("没有权限访问用户数据!");
        }
        userInfoVo.setUser(user);
        userInfoVo.setPermissions(loginUser.getMenuPermission());
        userInfoVo.setRoles(loginUser.getRolePermission());
        return R.ok(userInfoVo);
    }


    /**
     * 根据用户编号获取详细信息
     */
    @SaCheckPermission("system:user:query")
    @GetMapping(value = {"/", "/{userId}"})
    public R<SysUserInfoVo> getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        userService.checkUserDataScope(userId);
        SysUserInfoVo userInfoVo = new SysUserInfoVo();
        SysRoleBo sysRole = new SysRoleBo();
        sysRole.setStatus(UserConstants.ROLE_NORMAL);
        List<SysRoleVo> roles = roleService.selectRoleList(sysRole);
        SysPostBo sysPost = new SysPostBo();
        sysPost.setStatus(UserConstants.POST_NORMAL);
        userInfoVo.setRoles(LoginHelper.isSuperAdmin(userId) ? roles : StreamUtils.filter(roles, r -> !r.isSuperAdmin()));
        userInfoVo.setPosts(postService.selectPostList(sysPost));
        if (ObjectUtil.isNotNull(userId)) {
            //暂时取消脱敏处理
            SysUserVo sysUser;
            try {
                MaskManager.skipMask();
                sysUser = userService.selectUserById(userId);
            } finally {
                MaskManager.restoreMask();
            }
            userInfoVo.setUser(sysUser);
            userInfoVo.setRoleIds(StreamUtils.toList(sysUser.getRoles(), SysRoleVo::getRoleId));
            userInfoVo.setPostIds(postService.selectPostListByUserId(userId));
        }
        return R.ok(userInfoVo);
    }


    /**
     * 新增用户
     */
    @SaCheckPermission("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysUserBo userBo) {
        deptService.checkDeptDataScope(userBo.getDeptId());
        if (!userService.checkUserNameUnique(userBo)) {
            return R.fail("新增用户'" + userBo.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(userBo.getPhonenumber()) && !userService.checkPhoneUnique(userBo)) {
            return R.fail("新增用户'" + userBo.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(userBo.getEmail()) && !userService.checkEmailUnique(userBo)) {
            return R.fail("新增用户'" + userBo.getUserName() + "'失败，邮箱账号已存在");
        }
        if (!tenantService.checkAccountBalance(TenantHelper.getTenantId())) {
            return R.fail("当前租户下用户名额不足，请您联系管理员增加租户用户数量！");
        }
        userBo.setPassword(BCrypt.hashpw(userBo.getPassword()));
        boolean inserted = userService.insertUser(userBo);
        if (!inserted) {
            return R.fail("新增用户记录失败！");
        }
        return R.ok();
    }

    /**
     * 修改用户
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysUserBo userBo) {
        userService.checkUserAllowed(userBo.getUserId());
        userService.checkUserDataScope(userBo.getUserId());
        deptService.checkDeptDataScope(userBo.getDeptId());
        if (!userService.checkUserNameUnique(userBo)) {
            return R.fail("修改用户'" + userBo.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(userBo.getPhonenumber()) && !userService.checkPhoneUnique(userBo)) {
            return R.fail("修改用户'" + userBo.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(userBo.getEmail()) && !userService.checkEmailUnique(userBo)) {
            return R.fail("修改用户'" + userBo.getUserName() + "'失败，邮箱账号已存在");
        }
        boolean updated = userService.updateUser(userBo);
        if (!updated) {
            return R.fail("修改用户记录失败！");
        }
        return R.ok();
    }

    /**
     * 删除用户
     */
    @SaCheckPermission("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public R<Void> remove(@PathVariable Long[] userIds) {
        if (ArrayUtils.contains(userIds, LoginHelper.getUserId())) {
            return R.fail("当前用户不能删除");
        }
        boolean deleted = userService.deleteUserByIds(userIds);
        if (!deleted) {
            return R.fail("删除用户记录失败！");
        }
        return R.ok();
    }

    /**
     * 根据用户ID串批量获取用户基础信息
     *
     * @param userIds 用户ID串
     * @param deptId  部门ID
     */
    @SaCheckPermission("system:user:query")
    @GetMapping("/optionselect")
    public R<List<SysUserVo>> optionselect(@RequestParam(required = false) Long[] userIds, @RequestParam(required = false) Long deptId) {
        return R.ok(userService.selectUserByIds(userIds == null ? null : ListUtil.of(userIds), deptId));
    }

    /**
     * 重置密码
     */
    @ApiEncrypt
    @SaCheckPermission("system:user:resetPwd")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public R<Void> resetPwd(@RequestBody SysUserBo user) {
        userService.checkUserAllowed(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        boolean reseted = userService.resetPwd(user);
        if (!reseted) {
            return R.fail("重置密码失败！");
        }
        return R.ok();
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysUserBo user) {
        userService.checkUserAllowed(user.getUserId());
        userService.checkUserDataScope(user.getUserId());
        boolean updated = userService.updateUserStatus(user);
        if (!updated) {
            return R.fail("状态修改失败！");
        }
        return R.ok();
    }

    /**
     * 根据用户编号获取授权角色
     */
    @SaCheckPermission("system:user:query")
    @GetMapping("/authRole/{userId}")
    public R<SysUserInfoVo> authRole(@PathVariable("userId") Long userId) {
        SysUserVo user = userService.selectUserById(userId);
        List<SysRoleVo> roles = roleService.selectRolesByUserId(userId);
        SysUserInfoVo userInfoVo = new SysUserInfoVo();
        userInfoVo.setUser(user);
        //超级管理员角色只能id=1的admin用户才能拥有
        userInfoVo.setRoles(LoginHelper.isSuperAdmin(userId) ? roles : StreamUtils.filter(roles, r -> !r.isSuperAdmin()));
        return R.ok(userInfoVo);
    }

    /**
     * 用户授权角色
     */
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PutMapping("/authRole")
    public R<Void> insertAuthRole(Long userId, Long[] roleIds) {
        userService.checkUserDataScope(userId);
        userService.insertUserAuth(userId, roleIds);
        return R.ok();
    }

    /**
     * 获取部门树列表
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/deptTree")
    public R<List<Tree<Long>>> deptTree(SysDeptBo dept) {
        return R.ok(deptService.selectDeptTreeList(dept));
    }

    /**
     * 获取部门下的所有用户信息
     */
    @SaCheckPermission("system:user:list")
    @GetMapping("/list/dept/{deptId}")
    public R<List<SysUserVo>> listByDept(@PathVariable @NotNull Long deptId) {
        return R.ok(userService.selectUserListByDept(deptId));
    }


}
