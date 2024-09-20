package com.hisw.system.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.mybatisflex.annotation.RelationOneToOne;
import com.hisw.system.domain.SysOss;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;


import java.io.Serializable;
import java.util.Date;

/**
 * OSS对象存储视图对象 sys_oss
 *
 * @author lejav
 */
@Data
@AutoMapper(target = SysOss.class)
public class SysOssVo implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 对象存储主键
     */
    private Long ossId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原名
     */
    private String originalName;

    /**
     * 文件后缀名
     */
    private String fileSuffix;

    /**
     * URL地址
     */
    private String url;

    /** 乐观锁 */
    @ExcelProperty(value = "乐观锁版本号")
    private Integer version;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 上传人
     */
    private Long createBy;

    /**
     * 上传人名称
     */
    @RelationOneToOne(
        selfField = "createBy",
        targetTable = "sys_user",
        targetField = "userId",
        valueField = "userName"
    )
    private String createByName;

    /**
     * 服务商
     */
    private String service;


}
