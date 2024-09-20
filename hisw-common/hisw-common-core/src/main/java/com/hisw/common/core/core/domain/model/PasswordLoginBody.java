package com.hisw.common.core.core.domain.model;

import javax.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import static com.hisw.common.core.constant.UserConstants.*;

/**
 * 密码登录对象
 *
 * @author lejav
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PasswordLoginBody extends LoginBody {

    /**
     * 用户名
     */
    @NotBlank(message = "{user.username.not.blank}")
    @Length(min = USERNAME_MIN_LENGTH, max = USERNAME_MAX_LENGTH, message = "{user.username.length.valid}")
    private String username;

    /**
     * 用户密码
     */
    @NotBlank(message = "{user.password.not.blank}")
    @Length(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH, message = "{user.password.length.valid}")
    private String password;

}
