package com.hisw.system.domain;

import com.hisw.common.orm.core.domain.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * 角色表 sys_role
 *
 * @author lejav
 */
@Data
@NoArgsConstructor
@Table(value = "sys_role")
public class SysRole extends BaseEntity {
    /**
     * 角色ID
     */
    @Id
    private Long roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色权限
     */
    private String roleKey;

    /**
     * 角色排序
     */
    private Integer roleSort;

    /**
     * 数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限；5：仅本人数据权限）
     */
    private String dataScope;

    /**
     * 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）
     */
    private boolean menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示（0：父子不互相关联显示 1：父子互相关联显示 ）
     */
    private boolean deptCheckStrictly;

    /**
     * 角色状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private Integer delFlag;

    /**
     * 备注
     */
    private String remark;

    /**
     * 用户是否存在此角色标识 默认不存在
     */
    @Column(ignore = true)
    private boolean flag = false;

    /**
     * 菜单组
     */
    @Column(ignore = true)
    private Long[] menuIds;

    private List<SysMenu> menuList;

    /**
     * 部门组（数据权限）
     */
    @Column(ignore = true)
    private Long[] deptIds;

    private List<SysDept> deptList;

    private List<SysUser> userList;

    /**
     * 角色菜单权限
     */
    @Column(ignore = true)
    private Set<String> permissions;

    public SysRole(Long roleId) {
        this.roleId = roleId;
    }

    public boolean isAdmin() {
        return isAdmin(this.roleId);
    }

    public static boolean isAdmin(Long roleId) {
        return roleId != null && 1L == roleId;
    }

}
