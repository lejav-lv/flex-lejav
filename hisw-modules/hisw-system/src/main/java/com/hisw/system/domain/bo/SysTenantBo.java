package com.hisw.system.domain.bo;

import com.hisw.system.domain.SysTenant;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 租户业务对象 sys_tenant
 *
 * @author lejav
 */

@Data
@AutoMapper(target = SysTenant.class, reverseConvertGenerate = false)
public class SysTenantBo implements Serializable {

    /**
     * 租户编号
     */
    private Long tenantId;

    /**
     * 联系人
     */
    @NotBlank(message = "联系人不能为空")
    private String contactUserName;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;

    /**
     * 企业名称
     */
    @NotBlank(message = "企业名称不能为空")
    private String companyName;

    /**
     * 用户名（创建系统用户）
     */
    private String username;

    /**
     * 密码（创建系统用户）
     */
    private String password;

    /**
     * 统一社会信用代码
     */
    private String licenseNumber;

    /**
     * 地址
     */
    private String address;

    /**
     * 域名
     */
    private String domain;

    /**
     * 企业简介
     */
    private String intro;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户套餐编号
     */
    @NotNull(message = "租户套餐不能为空")
    private Long packageId;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户数量（-1不限制）
     */
    private Long accountCount;

    /**
     * 租户状态（0正常 1停用）
     */
    private String status;

    /** 乐观锁 */
    private Integer version;
}
