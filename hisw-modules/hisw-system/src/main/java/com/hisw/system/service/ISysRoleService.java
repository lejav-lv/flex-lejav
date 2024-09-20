package com.hisw.system.service;

import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.orm.core.service.IBaseService;
import com.hisw.system.domain.SysRole;
import com.hisw.system.domain.bo.SysRoleBo;
import com.hisw.system.domain.vo.SysRoleVo;

import java.util.List;
import java.util.Set;

/**
 * 角色业务层
 *
 * @author lejav
 */
public interface ISysRoleService extends IBaseService<SysRole> {
    /**
     * 分页查询角色列表
     *
     * @param roleBo 角色信息
     * @return 角色集合
     */
    TableDataInfo<SysRoleVo> selectPage(SysRoleBo roleBo);

    /**
     * 根据条件分页查询角色数据
     *
     * @param roleBo 角色信息
     * @return 角色数据集合信息
     */
    List<SysRoleVo> selectRoleList(SysRoleBo roleBo);

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRoleVo> selectRolesByUserId(Long userId);

    /**
     * 根据用户ID查询角色权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectRolePermissionByUserId(Long userId);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    List<SysRoleVo> selectRoleAll();

    /**
     * 通过角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    SysRoleVo selectRoleById(Long roleId);

    /**
     * 通过角色ID串查询角色
     *
     * @param roleIds 角色ID串
     * @return 角色列表信息
     */
    List<SysRoleVo> selectRoleByIds(List<Long> roleIds);

    /**
     * 校验角色名称是否唯一
     *
     * @param roleBo 角色信息
     * @return 结果
     */
    boolean checkRoleNameUnique(SysRoleBo roleBo);

    /**
     * 校验角色权限是否唯一
     *
     * @param roleBo 角色信息
     * @return 结果
     */
    boolean checkRoleKeyUnique(SysRoleBo roleBo);

    /**
     * 校验角色是否允许操作
     *
     * @param roleBo 角色信息
     */
    void checkRoleAllowed(SysRoleBo roleBo);

    /**
     * 校验角色是否有数据权限
     *
     * @param roleId 角色id
     */
    void checkRoleDataScope(Long roleId);

    /**
     * 新增保存角色信息
     *
     * @param roleBo 角色信息
     * @return 结果: true 保存成功，false 保存失败
     */
    boolean insertRole(SysRoleBo roleBo);

    /**
     * 新增角色菜单信息
     *
     * @param role 角色对象
     * @return true 保存成功，false 保存失败
     */
    boolean insertRoleMenu(SysRole role);

    /**
     * 修改保存角色信息
     *
     * @param roleBo 角色信息
     * @return true 操作成功，false 操作失败
     */
    boolean updateRole(SysRoleBo roleBo);

    /**
     * 修改角色状态
     *
     * @param roleBo 角色信息
     * @return 结果
     */
    boolean updateRoleStatus(SysRoleBo roleBo);

    /**
     * 修改数据权限信息
     *
     * @param roleBo 角色信息
     * @return 结果:true 成功，false 失败
     */
    boolean authDataScope(SysRoleBo roleBo);

    /**
     * 批量删除角色信息
     *
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    boolean deleteRoleByIds(Long[] roleIds);

    /**
     * 注销该角色的在线用户
     */
    void cleanOnlineUserByRole(Long roleId);

    /**
     * 根据用户ID查询角色
     *
     * @param userName 用户名
     * @return 角色列表
     */
    List<SysRoleVo> selectRolesByUserName(String userName);
}
