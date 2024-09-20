package com.hisw.common.core.enums;

import com.hisw.common.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备类型
 *
 * @author lejav
 */
@AllArgsConstructor
@Getter
public enum UserType {

    /**
     * pc端
     */
    SYS_USER("sys_user");

    private final String userType;

    public static UserType getUserType(String str) {
        for (UserType value : values()) {
            if (StringUtils.contains(str, value.getUserType())) {
                return value;
            }
        }
        throw new RuntimeException("用户类型未找到：" + str);
    }
}
