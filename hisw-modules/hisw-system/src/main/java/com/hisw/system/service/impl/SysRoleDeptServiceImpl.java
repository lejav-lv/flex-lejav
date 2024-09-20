package com.hisw.system.service.impl;

import com.hisw.common.orm.core.service.impl.BaseServiceImpl;
import com.hisw.system.domain.SysRoleDept;
import com.hisw.system.mapper.SysRoleDeptMapper;
import com.hisw.system.service.ISysRoleDeptService;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.hisw.system.domain.table.SysRoleDeptTableDef.SYS_ROLE_DEPT;

/**
 * SysRoleDept服务实现
 *
 * @author lejav
 */
@Service
public class SysRoleDeptServiceImpl extends BaseServiceImpl<SysRoleDeptMapper, SysRoleDept> implements ISysRoleDeptService {

    @Override
    public QueryWrapper query() {
        return super.query().from(SYS_ROLE_DEPT);
    }

    /**
     * 通过角色ID删除角色和部门关联
     *
     * @param roleId 角色ID
     */
    @Override
    public void deleteRoleDeptByRoleId(Long roleId) {
        QueryWrapper queryWrapper = query().where(SYS_ROLE_DEPT.ROLE_ID.eq(roleId));
        this.remove(queryWrapper);
    }

    /**
     * 批量删除角色部门关联信息
     *
     * @param ids 需要删除的数据ID
     */
    @Override
    public void deleteRoleDept(Long[] ids) {
        QueryWrapper queryWrapper = QueryWrapper.create().from(SYS_ROLE_DEPT).where(SYS_ROLE_DEPT.ROLE_ID.in(Arrays.asList(ids)));
        this.remove(queryWrapper);
    }

}
