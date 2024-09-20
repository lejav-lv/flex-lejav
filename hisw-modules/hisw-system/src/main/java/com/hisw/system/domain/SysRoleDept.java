package com.hisw.system.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 角色和部门关联 sys_role_dept
 *
 * @author lejav
 */
@Data
@Table(value = "sys_role_dept")
public class SysRoleDept {
    /** 角色ID */
    @Id
    private Long roleId;

    /** 部门ID */
    @Id
    private Long deptId;
}
