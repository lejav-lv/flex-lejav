package com.hisw.system.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.hisw.common.core.constant.TenantConstants;
import com.hisw.common.core.constant.UserConstants;
import com.hisw.common.core.core.domain.dto.RoleDTO;
import com.hisw.common.core.core.domain.model.LoginUser;
import com.hisw.common.core.exception.ServiceException;
import com.hisw.common.core.utils.MapstructUtils;
import com.hisw.common.core.utils.SpringUtils;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.orm.core.page.PageQuery;
import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.orm.core.service.impl.BaseServiceImpl;
import com.hisw.common.security.utils.LoginHelper;
import com.hisw.system.domain.SysRole;
import com.hisw.system.domain.SysRoleDept;
import com.hisw.system.domain.SysRoleMenu;
import com.hisw.system.domain.bo.SysRoleBo;
import com.hisw.system.domain.vo.SysRoleVo;
import com.hisw.system.mapper.SysRoleMapper;
import com.hisw.system.service.*;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import static com.hisw.system.domain.table.SysDeptTableDef.SYS_DEPT;
import static com.hisw.system.domain.table.SysRoleTableDef.SYS_ROLE;
import static com.hisw.system.domain.table.SysUserRoleTableDef.SYS_USER_ROLE;
import static com.hisw.system.domain.table.SysUserTableDef.SYS_USER;

/**
 * 角色 业务层处理
 *
 * @author lejav
 */
@Service
public class SysRoleServiceImpl extends BaseServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Resource
    private ISysRoleMenuService roleMenuService;
    @Resource
    private ISysRoleDeptService roleDeptService;
    @Resource
    private ISysUserRoleService userRoleService;
    @Resource
    private ISysDataScopeService dataScopeService;


    @Override
    public QueryWrapper query() {
        return super.query().from(SYS_ROLE);
    }

    private QueryWrapper buildQueryWrapper(SysRoleBo roleBo) {
        return QueryWrapper.create()
            .select(QueryMethods.distinct(SYS_ROLE.ALL_COLUMNS))
            .from(SYS_ROLE.as("r"))
            .leftJoin(SYS_USER_ROLE).as("ur").on(SYS_USER_ROLE.ROLE_ID.eq(SYS_ROLE.ROLE_ID))
            .leftJoin(SYS_USER).as("u").on(SYS_USER.USER_ID.eq(SYS_USER_ROLE.USER_ID))
            .leftJoin(SYS_DEPT).as("d").on(SYS_DEPT.DEPT_ID.eq(SYS_USER.DEPT_ID))
            .and(SYS_ROLE.ROLE_ID.eq(roleBo.getRoleId()))
            .and(SYS_ROLE.ROLE_NAME.like(roleBo.getRoleName()))
            .and(SYS_ROLE.STATUS.eq(roleBo.getStatus()))
            .and(SYS_ROLE.ROLE_KEY.like(roleBo.getRoleKey()))
            .and(SYS_ROLE.CREATE_TIME.between(roleBo.getParams().get("beginTime"), roleBo.getParams().get("endTime")))
            .orderBy(SYS_ROLE.ROLE_SORT.asc());
    }

    /**
     * 根据条件分页查询角色数据
     *
     * @param roleBo 角色信息
     * @return 角色数据集合信息
     */
    @Override
    public List<SysRoleVo> selectRoleList(SysRoleBo roleBo) {
        QueryWrapper queryWrapper = dataScopeService.addCondition(buildQueryWrapper(roleBo));
        return this.listAs(queryWrapper, SysRoleVo.class);
    }

    /**
     * 分页查询角色列表
     *
     * @param roleBo 角色信息
     * @return 角色集合
     */
    @Override
    public TableDataInfo<SysRoleVo> selectPage(SysRoleBo roleBo) {
        QueryWrapper queryWrapper = dataScopeService.addCondition(buildQueryWrapper(roleBo));
        Page<SysRoleVo> page = this.pageAs(PageQuery.build(), queryWrapper, SysRoleVo.class);
        return TableDataInfo.build(page);
    }

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRoleVo> selectRolesByUserId(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(QueryMethods.distinct(SYS_ROLE.ALL_COLUMNS))
            .from(SYS_ROLE.as("r"))
            .leftJoin(SYS_USER_ROLE).as("ur").on(SYS_USER_ROLE.ROLE_ID.eq(SYS_ROLE.ROLE_ID))
            .leftJoin(SYS_USER).as("u").on(SYS_USER.USER_ID.eq(SYS_USER_ROLE.USER_ID))
            .leftJoin(SYS_DEPT).as("d").on(SYS_DEPT.DEPT_ID.eq(SYS_USER.DEPT_ID))
            .where(SYS_USER_ROLE.USER_ID.eq(userId));

        List<SysRoleVo> userRoles = this.listAs(queryWrapper, SysRoleVo.class);
        List<SysRoleVo> roles = selectRoleAll();
        for (SysRoleVo role : roles) {
            for (SysRoleVo userRole : userRoles) {
                if (role.getRoleId().longValue() == userRole.getRoleId().longValue()) {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByUserId(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(QueryMethods.distinct(SYS_ROLE.ALL_COLUMNS))
            .from(SYS_ROLE.as("r"))
            .leftJoin(SYS_USER_ROLE).as("ur").on(SYS_USER_ROLE.ROLE_ID.eq(SYS_ROLE.ROLE_ID))
            .leftJoin(SYS_USER).as("u").on(SYS_USER.USER_ID.eq(SYS_USER_ROLE.USER_ID))
            .leftJoin(SYS_DEPT).as("d").on(SYS_DEPT.DEPT_ID.eq(SYS_USER.DEPT_ID))
            .where(SYS_USER_ROLE.USER_ID.eq(userId));
        List<SysRoleVo> perms = this.listAs(queryWrapper, SysRoleVo.class);
        //List<SysRole> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRoleVo perm : perms) {
            if (StringUtils.isNotNull(perm)) {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Override
    public List<SysRoleVo> selectRoleAll() {
        return SpringUtils.getAopProxy(this).selectRoleList(new SysRoleBo());
    }

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    public SysRoleVo selectRoleById(Long roleId) {
        return this.getOneAs(query().where(SYS_ROLE.ROLE_ID.eq(roleId)), SysRoleVo.class);
    }

    /**
     * 通过角色ID串查询角色
     *
     * @param roleIds 角色ID串
     * @return 角色列表信息
     */
    @Override
    public List<SysRoleVo> selectRoleByIds(List<Long> roleIds) {
        QueryWrapper queryWrapper = query().where(SYS_ROLE.STATUS.eq(UserConstants.ROLE_NORMAL))
            .and(SYS_ROLE.ROLE_ID.in(roleIds));
        return this.listAs(queryWrapper, SysRoleVo.class);
    }

    /**
     * 校验角色名称是否唯一
     *
     * @param roleBo 角色信息
     * @return 结果
     */
    @Override
    public boolean checkRoleNameUnique(SysRoleBo roleBo) {
        Long roleId = ObjectUtil.isNull(roleBo.getRoleId()) ? -1L : roleBo.getRoleId();
        QueryWrapper queryWrapper = query().where(SYS_ROLE.ROLE_NAME.eq(roleBo.getRoleName()));
        SysRole info = this.getOne(queryWrapper);
        if (ObjectUtil.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色权限是否唯一
     *
     * @param roleBo 角色信息
     * @return 结果
     */
    @Override
    public boolean checkRoleKeyUnique(SysRoleBo roleBo) {
        Long roleId = ObjectUtil.isNull(roleBo.getRoleId()) ? -1L : roleBo.getRoleId();
        QueryWrapper queryWrapper = query().where(SYS_ROLE.ROLE_KEY.eq(roleBo.getRoleKey()));
        SysRole info = this.getOne(queryWrapper);
        if (ObjectUtil.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色是否允许操作
     *
     * @param roleBo 角色信息
     */
    @Override
    public void checkRoleAllowed(SysRoleBo roleBo) {
        if (ObjectUtil.isNotNull(roleBo.getRoleId()) && LoginHelper.isSuperAdmin(roleBo.getRoleId())) {
            throw new ServiceException("不允许操作超级管理员角色");
        }
        String[] keys = new String[]{TenantConstants.SUPER_ADMIN_ROLE_KEY, TenantConstants.TENANT_ADMIN_ROLE_KEY};
        // 新增:不允许使用 管理员标识符
        if (ObjectUtil.isNull(roleBo.getRoleId())
            && StringUtils.equalsAny(roleBo.getRoleKey(), keys)) {
            throw new ServiceException("不允许使用系统内置管理员角色标识符!");
        }
        // 修改:不允许修改 管理员标识符
        if (ObjectUtil.isNotNull(roleBo.getRoleId())) {
            SysRole sysRole = this.getById(roleBo.getRoleId());
            // 如果标识符不相等 判断为修改了管理员标识符
            if (!StringUtils.equals(sysRole.getRoleKey(), roleBo.getRoleKey())) {
                if (StringUtils.equalsAny(sysRole.getRoleKey(), keys)) {
                    throw new ServiceException("不允许修改系统内置管理员角色标识符!");
                } else if (StringUtils.equalsAny(roleBo.getRoleKey(), keys)) {
                    throw new ServiceException("不允许使用系统内置管理员角色标识符!");
                }
            }
        }
    }

    /**
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     */
    @Override
    public void checkRoleDataScope(Long roleId) {
        if (ObjectUtil.isNull(roleId)) {
            return;
        }
        if (LoginHelper.isSuperAdmin()) {
            return;
        }
        SysRoleBo role = new SysRoleBo();
        role.setRoleId(roleId);
        List<SysRoleVo> roles = SpringUtils.getAopProxy(this).selectRoleList(role);
        if (CollUtil.isEmpty(roles)) {
            throw new ServiceException("没有权限访问角色数据！");
        }
    }

    /**
     * 新增保存角色信息
     *
     * @param roleBo 角色信息
     * @return true 操作成功，false 操作失败
     */
    @Override
    @Transactional
    public boolean insertRole(SysRoleBo roleBo) {
        SysRole role = MapstructUtils.convert(roleBo, SysRole.class);
        role.setDelFlag(0);
        role.setDataScope("1");//默认1：全部数据权限
        // 新增角色信息
        boolean inserted = this.save(role);//使用全局配置的雪花算法主键生成器生成ID值
        if (inserted) {
            return insertRoleMenu(role);
        }
        return false;
    }

    /**
     * 修改保存角色信息
     *
     * @param roleBo 角色信息
     * @return true 操作成功，false 操作失败
     */
    @Override
    @Transactional
    public boolean updateRole(SysRoleBo roleBo) {
        SysRole role = MapstructUtils.convert(roleBo, SysRole.class);
        // 修改角色信息
        boolean updated = this.updateById(role);
        // 删除角色与菜单关联
        if (updated) {
            roleMenuService.deleteRoleMenuByRoleId(role.getRoleId());

            return insertRoleMenu(role);
        }
        return false;
    }

    /**
     * 修改角色状态
     *
     * @param roleBo 角色信息
     * @return boolean
     */
    @Override
    public boolean updateRoleStatus(SysRoleBo roleBo) {
        SysRole role = MapstructUtils.convert(roleBo, SysRole.class);

        Long roleId = role.getRoleId();
        String status = role.getStatus();
        if (UserConstants.ROLE_DISABLE.equals(status) && userRoleService.countUserRoleByRoleId(roleId) > 0) {
            throw new ServiceException("角色已分配，不能禁用!");
        }

        // 修改角色信息
        return this.updateById(role);
    }

    /**
     * 修改数据权限信息
     *
     * @param roleBo 角色信息
     * @return 结果:true 成功，false 失败
     */
    @Override
    @Transactional
    public boolean authDataScope(SysRoleBo roleBo) {
        // 修改角色信息
        SysRole role = MapstructUtils.convert(roleBo, SysRole.class);
        // 修改角色信息
        boolean updated = this.updateById(role);
        // 删除角色与部门关联
        roleDeptService.deleteRoleDeptByRoleId(role.getRoleId());
        // 新增角色和部门 信息（数据权限）
        insertRoleDept(role);

        return updated;
    }

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     * @return true 保存成功，false 保存失败
     */
    @Override
    @Transactional
    public boolean insertRoleMenu(SysRole role) {
        boolean inserted = true;
        // 新增用户与角色管理
        List<SysRoleMenu> list = new ArrayList<>();
        for (Long menuId : role.getMenuIds()) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (!list.isEmpty()) {
            inserted = roleMenuService.saveBatchWithPk(list, 100);//批量保存
        }
        return inserted;
    }

    /**
     * 新增角色部门信息(数据权限)
     *
     * @param role 角色对象
     */
    @Transactional
    public void insertRoleDept(SysRole role) {
        // 新增角色与部门（数据权限）管理
        List<SysRoleDept> list = new ArrayList<>();
        for (Long deptId : role.getDeptIds()) {
            SysRoleDept rd = new SysRoleDept();
            rd.setRoleId(role.getRoleId());
            rd.setDeptId(deptId);
            list.add(rd);
        }
        if (!list.isEmpty()) {
            roleDeptService.saveBatchWithPk(list, 100);//批量保存
        }
    }

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    @Override
    @Transactional
    public boolean deleteRoleByIds(Long[] roleIds) {
        for (Long roleId : roleIds) {
            checkRoleAllowed(new SysRoleBo(roleId));
            checkRoleDataScope(roleId);
            SysRoleVo role = selectRoleById(roleId);
            if (userRoleService.countUserRoleByRoleId(roleId) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除!", role.getRoleName()));
            }
        }
        // 删除角色与菜单关联
        roleMenuService.deleteRoleMenu(roleIds);
        // 删除角色与部门关联
        roleDeptService.deleteRoleDept(roleIds);

        //逻辑删除：update sys_role set del_flag = '1' where role_id in
        return UpdateChain.of(SysRole.class)
            .set(SysRole::getDelFlag, "1")
            .where(SysRole::getRoleId).in(Arrays.asList(roleIds))
            .update();
    }

    /**
     * 注销该角色的在线用户
     *
     * @param roleId 主键
     */
    @Override
    public void cleanOnlineUserByRole(Long roleId) {
        // 如果角色未绑定用户 直接返回
        int num = userRoleService.countUserRoleByRoleId(roleId);
        if (num == 0) {
            return;
        }
        List<String> keys = StpUtil.searchTokenValue("", 0, -1, false);
        if (CollUtil.isEmpty(keys)) {
            return;
        }
        // 角色关联的在线用户量过大会导致redis阻塞卡顿 谨慎操作
        keys.parallelStream().forEach(key -> {
            String token = StringUtils.substringAfterLast(key, ":");
            // 如果已经过期则跳过
            if (StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) < -1) {
                return;
            }
            LoginUser loginUser = LoginHelper.getLoginUser(token);
            for (RoleDTO roleDTO : loginUser.getRoles()) {
                if (ObjectUtil.isNotNull(roleDTO.getRoleId()) && roleDTO.getRoleId().equals(roleId)) {
                    try {
                        StpUtil.logoutByTokenValue(token);
                        break;
                    } catch (NotLoginException ignored) {
                    }
                }
            }
        });
    }

    /**
     * 根据用户名查询角色
     *
     * @param userName 用户名
     * @return 角色列表
     */
    @Override
    public List<SysRoleVo> selectRolesByUserName(String userName) {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(QueryMethods.distinct(SYS_ROLE.ALL_COLUMNS))
            .from(SYS_ROLE.as("r"))
            .leftJoin(SYS_USER_ROLE).as("ur").on(SYS_USER_ROLE.ROLE_ID.eq(SYS_ROLE.ROLE_ID))
            .leftJoin(SYS_USER).as("u").on(SYS_USER.USER_ID.eq(SYS_USER_ROLE.USER_ID))
            .leftJoin(SYS_DEPT).as("d").on(SYS_DEPT.DEPT_ID.eq(SYS_USER.DEPT_ID))
            .where(SYS_ROLE.DEL_FLAG.eq(0))
            .and(SYS_USER.USER_NAME.eq(userName));
        return this.listAs(queryWrapper, SysRoleVo.class);
    }
}
