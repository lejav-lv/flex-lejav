package com.hisw.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.ListUtil;
import com.hisw.common.core.core.domain.R;
import com.hisw.common.excel.utils.ExcelUtil;
import com.hisw.common.log.annotation.Log;
import com.hisw.common.log.enums.BusinessType;
import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.web.core.BaseController;
import com.hisw.system.domain.SysUserRole;
import com.hisw.system.domain.bo.SysDeptBo;
import com.hisw.system.domain.bo.SysRoleBo;
import com.hisw.system.domain.bo.SysUserBo;
import com.hisw.system.domain.vo.DeptTreeSelectVo;
import com.hisw.system.domain.vo.SysRoleVo;
import com.hisw.system.domain.vo.SysUserVo;
import com.hisw.system.service.ISysDeptService;
import com.hisw.system.service.ISysRoleService;
import com.hisw.system.service.ISysUserRoleService;
import com.hisw.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 角色信息
 *
 * @author lejav
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController {

    @Resource
    private ISysRoleService roleService;
    @Resource
    private ISysUserService userService;
    @Resource
    private ISysUserRoleService userRoleService;
    @Resource
    private ISysDeptService deptService;

    @SaCheckPermission("system:role:list")
    @GetMapping("/list")
    public TableDataInfo<SysRoleVo> list(SysRoleBo roleBo) {
        return roleService.selectPage(roleBo);
    }

    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:role:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRoleBo roleBo) {
        List<SysRoleVo> list = roleService.selectRoleList(roleBo);
        ExcelUtil.exportExcel(list, "角色数据", SysRoleVo.class, response);
    }

    /**
     * 根据角色编号获取详细信息
     */
    @SaCheckPermission("system:role:query")
    @GetMapping(value = "/{roleId}")
    public R<SysRoleVo> getInfo(@PathVariable Long roleId) {
        roleService.checkRoleDataScope(roleId);
        return R.ok(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @SaCheckPermission("system:role:add")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysRoleBo roleBo) {
        roleService.checkRoleAllowed(roleBo);
        if (!roleService.checkRoleNameUnique(roleBo)) {
            return R.fail("新增角色'" + roleBo.getRoleName() + "'失败，角色名称已存在");
        } else if (!roleService.checkRoleKeyUnique(roleBo)) {
            return R.fail("新增角色'" + roleBo.getRoleName() + "'失败，角色权限已存在");
        }
        boolean inserted = roleService.insertRole(roleBo);
        if (!inserted) {
            return R.fail("新增角色记录失败！");
        }
        return R.ok();
    }

    /**
     * 修改保存角色
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysRoleBo roleBo) {
        roleService.checkRoleAllowed(roleBo);
        roleService.checkRoleDataScope(roleBo.getRoleId());
        if (!roleService.checkRoleNameUnique(roleBo)) {
            return R.fail("修改角色'" + roleBo.getRoleName() + "'失败，角色名称已存在");
        } else if (!roleService.checkRoleKeyUnique(roleBo)) {
            return R.fail("修改角色'" + roleBo.getRoleName() + "'失败，角色权限已存在");
        }
        boolean updated = roleService.updateRole(roleBo);
        if (updated) {
            roleService.cleanOnlineUserByRole(roleBo.getRoleId());
            return R.ok();
        }
        return R.fail("修改角色'" + roleBo.getRoleName() + "'失败，请联系管理员");
    }

    /**
     * 修改保存数据权限
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public R<Void> dataScope(@RequestBody SysRoleBo roleBo) {
        roleService.checkRoleAllowed(roleBo);
        roleService.checkRoleDataScope(roleBo.getRoleId());
        boolean updated = roleService.authDataScope(roleBo);
        if (!updated) {
            return R.fail("修改角色'" + roleBo.getRoleName() + "'数据权限失败，请联系管理员");
        }

        return R.ok();
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysRoleBo roleBo) {
        roleService.checkRoleAllowed(roleBo);
        roleService.checkRoleDataScope(roleBo.getRoleId());
        boolean updated = roleService.updateRoleStatus(roleBo);
        if (!updated) {
            return R.fail("修改角色'" + roleBo.getRoleName() + "'的状态失败，请联系管理员");
        }
        return R.ok();
    }

    /**
     * 删除角色
     */
    @SaCheckPermission("system:role:remove")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public R<Void> remove(@PathVariable Long[] roleIds) {
        boolean deleted = roleService.deleteRoleByIds(roleIds);
        if (!deleted) {
            return R.fail("批量删除角色失败，请联系管理员");
        }
        return R.ok();
    }

    /**
     * 获取角色选择框列表
     *
     * @param roleIds 角色ID串
     */
    @SaCheckPermission("system:role:query")
    @GetMapping("/optionselect")
    public R<List<SysRoleVo>> optionselect(@RequestParam(required = false) Long[] roleIds) {
        return R.ok(roleService.selectRoleByIds(roleIds == null ? null : ListUtil.of(roleIds)));
    }

    /**
     * 查询已分配用户角色分页列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/authUser/allocatedList")
    public TableDataInfo<SysUserVo> allocatedList(SysUserBo userBo) {
        return userService.selectAllocatedPage(userBo);
    }

    /**
     * 查询未分配用户角色分页列表
     */
    @SaCheckPermission("system:role:list")
    @GetMapping("/authUser/unallocatedList")
    public TableDataInfo<SysUserVo> unallocatedList(SysUserBo userBo) {
        return userService.selectUnallocatedPage(userBo);
    }

    /**
     * 取消授权用户
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public R<Void> cancelAuthUser(@RequestBody SysUserRole userRole) {
        boolean deleted = userRoleService.deleteUserRoleInfo(userRole);
        if (!deleted) {
            return R.fail("取消授权用户角色失败，请联系管理员");
        }
        return R.ok();
    }

    /**
     * 批量取消授权用户
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public R<Void> cancelAuthUserAll(Long roleId, Long[] userIds) {
        boolean deleted = userRoleService.deleteUserRoleInfos(roleId, userIds);
        if (!deleted) {
            return R.fail("批量取消授权用户角色失败，请联系管理员");
        }
        return R.ok();
    }

    /**
     * 批量选择用户授权
     */
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public R<Void> selectAuthUserAll(Long roleId, Long[] userIds) {
        roleService.checkRoleDataScope(roleId);
        boolean inserted = userRoleService.insertAuthUsers(roleId, userIds);
        if (!inserted) {
            return R.fail("批量选择用户授权失败，请联系管理员");
        }
        return R.ok();
    }

    /**
     * 获取对应角色部门树列表
     */
    @SaCheckPermission("system:role:query")
    @GetMapping(value = "/deptTree/{roleId}")
    public R<DeptTreeSelectVo> deptTree(@PathVariable("roleId") Long roleId) {
        DeptTreeSelectVo selectVo = new DeptTreeSelectVo();
        selectVo.setCheckedKeys(deptService.selectDeptListByRoleId(roleId));
        selectVo.setDepts(deptService.selectDeptTreeList(new SysDeptBo()));
        return R.ok(selectVo);
    }
}
