package com.hisw.system.service;

import com.hisw.common.orm.core.service.IBaseService;
import com.hisw.system.domain.SysUserRole;

/**
 * SysUserRole服务接口
 *
 * @author lejav
 */
public interface ISysUserRoleService extends IBaseService<SysUserRole> {

     /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    int countUserRoleByRoleId(Long roleId);

    /**
     * 取消授权用户:删除用户和角色关联信息
     *
     * @param userRole 用户和角色关联信息
     * @return 结果:true 删除成功，false 删除失败
     */
    boolean deleteUserRoleInfo(SysUserRole userRole);

    /**
     * 批量取消授权用户角色
     *
     * @param roleId 角色ID
     * @param userIds 需要删除的用户数据ID
     * @return 结果:true 删除成功，false 删除失败
     */
    boolean deleteUserRoleInfos(Long roleId, Long[] userIds);

    /**
     * 批量选择授权角色用户
     *
     * @param roleId 角色ID
     * @param userIds 需要新增的用户数据ID
     * @return 结果：true 保存成功，false 保存失败
     */
    boolean insertAuthUsers(Long roleId, Long[] userIds);

    /**
     * 新增用户角色
     *
     * @param userId  用户ID
     * @param roleIds 需要新增的角色数据ID
     */
    void insertUserRoles(Long userId, Long[] roleIds);

    /**
     * 通过用户ID删除用户和角色关联
     *
     * @param userId 用户ID
     */
    void deleteUserRoleByUserId(Long userId);

    /**
     * 批量删除用户和角色关联
     *
     * @param ids 需要删除的数据ID
     */
    void deleteUserRole(Long[] ids);
}
