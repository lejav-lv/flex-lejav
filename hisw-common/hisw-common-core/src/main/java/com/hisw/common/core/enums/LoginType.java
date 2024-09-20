package com.hisw.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录类型
 *
 * @author lejav
 */
@Getter
@AllArgsConstructor
public enum LoginType {

    /**
     * 密码登录
     */
    PASSWORD("user.password.retry.limit.exceed", "user.password.retry.limit.count");

    /**
     * 登录重试超出限制提示
     */
    private final String retryLimitExceed;

    /**
     * 登录重试限制计数提示
     */
    private final String retryLimitCount;
}
