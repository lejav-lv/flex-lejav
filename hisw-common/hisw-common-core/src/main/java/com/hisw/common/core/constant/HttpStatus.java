package com.hisw.common.core.constant;

/**
 * 返回状态码
 *
 * @author lejav
 */
public class HttpStatus {
    /**
     * 操作成功
     */
    public static final int SUCCESS = 200;

    /**
     * 访问受限，授权过期
     */
    public static final int FORBIDDEN = 403;

    /**
     * 系统内部错误
     */
    public static final int ERROR = 500;

    /**
     * 系统警告消息
     */
    public static final int WARN = 601;
}
