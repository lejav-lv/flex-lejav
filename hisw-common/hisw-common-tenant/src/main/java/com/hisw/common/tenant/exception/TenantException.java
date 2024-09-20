package com.hisw.common.tenant.exception;

import com.hisw.common.core.exception.base.BaseException;



/**
 * 租户异常类
 *
 * @author lejav
 */
public class TenantException extends BaseException {

    private static final long serialVersionUID = 1L;

    public TenantException(String code, Object... args) {
        super("tenant", code, args, null);
    }
}
