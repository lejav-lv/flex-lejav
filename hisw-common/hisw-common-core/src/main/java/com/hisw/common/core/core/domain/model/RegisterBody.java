package com.hisw.common.core.core.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户注册对象
 *
 * @author lejav
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterBody extends LoginBody {
    private String userType;
}
