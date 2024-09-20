package com.hisw.common.oss.exception;



/**
 * OSS异常类
 *
 * @author lejav
 */
public class OssException extends RuntimeException {


    private static final long serialVersionUID = 1L;

    public OssException(String msg) {
        super(msg);
    }

}
