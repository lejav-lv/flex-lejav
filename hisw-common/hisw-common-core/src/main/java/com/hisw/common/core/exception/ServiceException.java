package com.hisw.common.core.exception;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 业务异常
 *
 * @author lejav
 */
@Data
@NoArgsConstructor
public final class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    public ServiceException(String message) {
        this.message = message;
    }

    public ServiceException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public ServiceException setMessage(String message) {
        this.message = message;
        return this;
    }

}
