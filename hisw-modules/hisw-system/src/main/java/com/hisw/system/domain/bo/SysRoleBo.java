package com.hisw.system.domain.bo;

import com.hisw.common.core.constant.UserConstants;
import com.hisw.common.orm.core.domain.BaseEntity;
import com.hisw.system.domain.SysRole;
import io.github.linpeilie.annotations.AutoMapper;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 角色信息业务对象 sys_role
 *
 * @author lejav
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysRole.class, reverseConvertGenerate = false)
public class SysRoleBo extends BaseEntity {

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @Size( max = 30, message = "角色名称长度不能超过{max}个字符")
    private String roleName;

    /**
     * 角色权限字符串
     */
    @NotBlank(message = "角色权限字符串不能为空")
    @Size( max = 100, message = "权限字符长度不能超过{max}个字符")
    private String roleKey;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    private Integer roleSort;

    /**
     * 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）
     */
    private String dataScope;

    /**
     * 菜单树选择项是否关联显示
     */
    private Boolean menuCheckStrictly;

    /**
     * 部门树选择项是否关联显示
     */
    private Boolean deptCheckStrictly;

    /**
     * 角色状态（0正常 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 菜单组
     */
    private Long[] menuIds;

    /**
     * 部门组（数据权限）
     */
    private Long[] deptIds;

    public SysRoleBo(Long roleId) {
        this.roleId = roleId;
    }

    public boolean isSuperAdmin() {
        return UserConstants.SUPER_ADMIN_ID.equals(this.roleId);
    }

}
