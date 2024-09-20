package com.hisw.system.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 用户和角色关联 sys_user_role
 *
 * @author lejav
 */
@Data
@Table(value = "sys_user_role")
public class SysUserRole {
    /** 用户ID */
    @Id
    private Long userId;

    /** 角色ID */
    @Id
    private Long roleId;
}
