package com.hisw.system.domain.bo;

import com.mybatisflex.annotation.ColumnMask;
import com.mybatisflex.core.mask.Masks;
import com.hisw.common.core.xss.Xss;
import com.hisw.common.orm.core.domain.BaseEntity;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 个人信息业务处理
 *
 * @author lejav
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SysUserProfileBo extends BaseEntity {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    @Xss(message = "用户昵称不能包含脚本字符")
    @Size( max = 30, message = "用户昵称长度不能超过{max}个字符")
    private String nickName;

    /**
     * 用户邮箱
     */
    @ColumnMask(Masks.EMAIL)
    @Email(message = "邮箱格式不正确")
    @Size( max = 50, message = "邮箱长度不能超过{max}个字符")
    private String email;

    /**
     * 手机号码
     */
    @ColumnMask(Masks.MOBILE)
    private String phonenumber;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private String gender;

}
