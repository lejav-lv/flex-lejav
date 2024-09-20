package com.hisw.system.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 授权管理对象 sys_client
 *
 * @author lejav
 */
@Data
@Table(value = "sys_client")
public class SysClient  implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id
    private Long id;

    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 客户端key
     */
    private String clientKey;

    /**
     * 客户端秘钥
     */
    private String clientSecret;

    /**
     * 授权类型
     */
    private String grantType;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * token活跃超时时间
     */
    private Long activeTimeout;

    /**
     * token固定超时时间
     */
    private Long timeout;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /** 乐观锁 */
    @Column(version = true)
    private Integer version;

    /**
     * 删除标志（0就代表存在 1就代表删除）
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
