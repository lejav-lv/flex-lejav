package com.hisw.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 租户套餐对象 sys_tenant_package
 *
 * @author lejav
 */
@Data
@Table("sys_tenant_package")
public class SysTenantPackage implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 租户套餐id
     */
    @Id
    private Long packageId;
    /**
     * 套餐名称
     */
    private String packageName;
    /**
     * 关联菜单id
     */
    private String menuIds;
    /**
     * 备注
     */
    private String remark;
    /**
     * 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）
     */
    private Boolean menuCheckStrictly;
    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /** 乐观锁 */
    @Column(version = true)
    private Integer version;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private Integer delFlag;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
