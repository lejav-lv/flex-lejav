package com.hisw.common.orm.core.domain;


import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mybatisflex.annotation.Column;
import lombok.Data;

/**
 * Entity基类
 *
 * @author lejav
 */

@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 租户编号
     */
    @Column(tenantId = true)
    private Long tenantId;

    /** 乐观锁 */
    @Column(version = true)
    private Integer version;

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

    /**
     * 请求参数
     */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Column(ignore = true)
    private Map<String, Object> params = new HashMap<>();

}
