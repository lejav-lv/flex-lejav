package com.hisw.system.service;

import cn.dev33.satoken.secure.BCrypt;
import com.hisw.common.core.constant.Constants;
import com.hisw.common.core.constant.GlobalConstants;
import com.hisw.common.core.core.domain.model.RegisterBody;
import com.hisw.common.core.enums.UserType;
import com.hisw.common.core.exception.user.CaptchaException;
import com.hisw.common.core.exception.user.CaptchaExpireException;
import com.hisw.common.core.exception.user.UserException;
import com.hisw.common.core.utils.MessageUtils;
import com.hisw.common.core.utils.ServletUtils;
import com.hisw.common.core.utils.SpringUtils;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.log.event.LogininforEvent;
import com.hisw.common.redis.utils.RedisUtils;
import com.hisw.common.tenant.helper.TenantHelper;
import com.hisw.common.web.config.properties.CaptchaProperties;
import com.hisw.system.domain.bo.SysUserBo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 注册校验方法
 *
 * @author lejav
 */
@RequiredArgsConstructor
@Service
public class SysRegisterService {

    @Resource
    private ISysUserService userService;

    private final CaptchaProperties captchaProperties;

    /**
     * 注册
     */
    public void register(RegisterBody registerBody) {
        Long tenantId = registerBody.getTenantId();
        TenantHelper.dynamic(tenantId, () -> {
            String username = registerBody.getUsername();
            String password = registerBody.getPassword();
            // 校验用户类型是否存在
            String userType = UserType.getUserType(registerBody.getUserType()).getUserType();
            boolean captchaEnabled = captchaProperties.getEnable();
            // 验证码开关
            if (captchaEnabled) {
                validateCaptcha(tenantId, username, registerBody.getCode(), registerBody.getUuid());
            }
            SysUserBo sysUser = new SysUserBo();
            sysUser.setUserName(username);
            sysUser.setNickName(username);
            sysUser.setPassword(BCrypt.hashpw(password));
            sysUser.setUserType(userType);

            boolean unique = userService.checkUserNameUnique(sysUser);

            if (!unique) {
                throw new UserException("user.register.save.error", username);
            }
            boolean regFlag = userService.registerUser(sysUser, tenantId);
            if (!regFlag) {
                throw new UserException("user.register.error");
            }
            recordLogininfor(tenantId, username, MessageUtils.message("user.register.success"));
        });
    }

    /**
     * 校验验证码
     *
     * @param tenantId 租户ID
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    public void validateCaptcha(Long tenantId, String username, String code, String uuid) {
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.blankToDefault(uuid, "");
        String captcha = RedisUtils.getCacheObject(verifyKey);
        RedisUtils.deleteObject(verifyKey);
        if (captcha == null) {
            recordLogininfor(tenantId, username, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            recordLogininfor(tenantId, username, MessageUtils.message("user.jcaptcha.error"));
            throw new CaptchaException();
        }
    }

    /**
     * 记录登录信息
     *
     * @param tenantId 租户ID
     * @param username 用户名
     * @param message  消息内容
     */
    private void recordLogininfor(Long tenantId, String username, String message) {
        LogininforEvent logininforEvent = new LogininforEvent();
        logininforEvent.setTenantId(tenantId);
        logininforEvent.setUsername(username);
        logininforEvent.setStatus(Constants.REGISTER);
        logininforEvent.setMessage(message);
        logininforEvent.setRequest(ServletUtils.getRequest());
        SpringUtils.context().publishEvent(logininforEvent);
    }
}
