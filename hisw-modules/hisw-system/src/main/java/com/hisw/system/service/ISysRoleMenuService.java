package com.hisw.system.service;

import com.hisw.common.orm.core.service.IBaseService;
import com.hisw.system.domain.SysRoleMenu;

/**
 * @author lejav
 */
public interface ISysRoleMenuService extends IBaseService<SysRoleMenu> {

     /**
     * 批量删除角色菜单关联信息
     *
     * @param ids 需要删除的数据ID
      */
    void deleteRoleMenu(Long[] ids);

    /**
     * 通过角色ID删除用户和菜单关联
     *
     * @param roleId 角色ID
     */
    void deleteRoleMenuByRoleId(Long roleId);

    /**
     * 查询菜单使用数量
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    int checkMenuExistRole(Long menuId);

}
