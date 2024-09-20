package com.hisw.system.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 角色和菜单关联 sys_role_menu
 *
 * @author lejav
 */
@Data
@Table(value = "sys_role_menu")
public class SysRoleMenu {
    /** 角色ID */
    @Id(keyType = KeyType.None)
    private Long roleId;

    /** 菜单ID */
    @Id(keyType = KeyType.None)
    private Long menuId;
}
