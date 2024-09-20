package com.hisw.system.domain.bo;

import com.hisw.system.domain.SysTenantPackage;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import lombok.Data;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 租户套餐业务对象 sys_tenant_package
 *
 * @author lejav
 */

@Data
@AutoMapper(target = SysTenantPackage.class, reverseConvertGenerate = false)
public class SysTenantPackageBo implements Serializable {

    /**
     * 租户套餐id
     */
    private Long packageId;

    /**
     * 套餐名称
     */
    @NotBlank(message = "套餐名称不能为空")
    private String packageName;

    /**
     * 关联菜单id
     */
    @AutoMapping(target = "menuIds", expression = "java(com.hisw.common.core.utils.StringUtils.join(source.getMenuIds(), \",\"))")
    private Long[] menuIds;

    /**
     * 备注
     */
    private String remark;

    /**
     * 菜单树选择项是否关联显示
     */
    private Boolean menuCheckStrictly;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /** 乐观锁 */
    private Integer version;
}
