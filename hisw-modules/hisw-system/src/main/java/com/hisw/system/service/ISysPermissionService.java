package com.hisw.system.service;

import java.util.Set;

/**
 * 用户权限处理
 *
 * @author lejav
 */
public interface ISysPermissionService {

    /**
     * 获取角色数据权限
     *
     * @param userId  用户id
     * @return 角色权限信息
     */
    Set<String> getRolePermission(Long userId);

    /**
     * 获取菜单数据权限
     *
     * @param userId  用户id
     * @return 菜单权限信息
     */
    Set<String> getMenuPermission(Long userId);

}
