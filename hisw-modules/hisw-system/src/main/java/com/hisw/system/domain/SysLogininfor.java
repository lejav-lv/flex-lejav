package com.hisw.system.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统访问记录表 sys_logininfor
 *
 * @author lejav
 */
@Data
@Table(value = "sys_logininfor")
public class SysLogininfor implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @Id
    private Long infoId;

    /**
     * 租户编号
     */
    private Long tenantId;

    /** 用户账号 */
    private String userName;

    /**
     * 客户端
     */
    private String clientKey;

    /**
     * 设备类型
     */
    private String deviceType;

    /** 登录状态 0成功 1失败 */
    private String status;

    /** 登录IP地址 */
    private String ipaddr;

    /** 登录地点 */
    private String loginLocation;

    /** 浏览器类型 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 提示消息 */
    private String msg;

    /** 访问时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    /**
     * 请求参数
     */
    private Map<String, Object> params = new HashMap<>();
}
