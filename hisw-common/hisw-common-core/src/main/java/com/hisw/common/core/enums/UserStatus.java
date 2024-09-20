package com.hisw.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态
 *
 * @author lejav
 */
@AllArgsConstructor
@Getter
public enum UserStatus {
    DISABLE("1", "停用");

    private final String code;
    private final String info;

}
