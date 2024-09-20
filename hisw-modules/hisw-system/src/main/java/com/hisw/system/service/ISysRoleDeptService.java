package com.hisw.system.service;

import com.hisw.common.orm.core.service.IBaseService;
import com.hisw.system.domain.SysRoleDept;

/**
 * SysRoleDept服务接口
 *
 * @author lejav
 */
public interface ISysRoleDeptService extends IBaseService<SysRoleDept> {

    /**
     * 通过角色ID删除角色和部门关联
     *
     * @param roleId 角色ID
     */
    void deleteRoleDeptByRoleId(Long roleId);

    /**
     * 批量删除角色部门关联信息
     *
     * @param ids 需要删除的数据ID
     */
    void deleteRoleDept(Long[] ids);

}
