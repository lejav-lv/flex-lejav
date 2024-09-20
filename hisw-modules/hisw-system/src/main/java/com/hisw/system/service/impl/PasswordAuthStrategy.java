package com.hisw.system.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.hisw.common.core.constant.Constants;
import com.hisw.common.core.constant.GlobalConstants;
import com.hisw.common.core.core.domain.model.LoginUser;
import com.hisw.common.core.core.domain.model.PasswordLoginBody;
import com.hisw.common.core.enums.LoginType;
import com.hisw.common.core.enums.UserStatus;
import com.hisw.common.core.exception.user.CaptchaException;
import com.hisw.common.core.exception.user.CaptchaExpireException;
import com.hisw.common.core.exception.user.UserException;
import com.hisw.common.core.utils.MessageUtils;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.core.utils.ValidatorUtils;
import com.hisw.common.json.utils.JsonUtils;
import com.hisw.common.redis.utils.RedisUtils;
import com.hisw.common.security.utils.LoginHelper;
import com.hisw.common.web.config.properties.CaptchaProperties;
import com.hisw.system.domain.vo.LoginVo;
import com.hisw.system.domain.vo.SysClientVo;
import com.hisw.system.domain.vo.SysUserVo;
import com.hisw.system.service.IAuthStrategy;
import com.hisw.system.service.ISysUserService;
import com.hisw.system.service.SysLoginService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 密码认证策略
 *
 * @author lejav
 */
@Slf4j
@Service("password" + IAuthStrategy.BASE_NAME)
@RequiredArgsConstructor
public class PasswordAuthStrategy implements IAuthStrategy {

    private final CaptchaProperties captchaProperties;
    @Resource
    private SysLoginService loginService;
    @Resource
    private ISysUserService userService;

    @Override
    public LoginVo login(String body, SysClientVo client) {
        PasswordLoginBody loginBody = JsonUtils.parseObject(body, PasswordLoginBody.class);
        ValidatorUtils.validate(loginBody);
        Long tenantId = loginBody.getTenantId();
        String username = loginBody.getUsername();
        String password = loginBody.getPassword();
        String code = loginBody.getCode();
        String uuid = loginBody.getUuid();

        boolean captchaEnabled = captchaProperties.getEnable();
        // 验证码开关
        if (captchaEnabled) {
            validateCaptcha(tenantId, username, code, uuid);
        }
        SysUserVo user = loadUserByUsername(tenantId, username);
        loginService.checkLogin(LoginType.PASSWORD, tenantId, username, () -> !BCrypt.checkpw(password, user.getPassword()));
        // 此处可根据登录用户的数据不同 自行创建 loginUser
        LoginUser loginUser = loginService.buildLoginUser(user);
        loginUser.setClientKey(client.getClientKey());
        loginUser.setDeviceType(client.getDeviceType());
        SaLoginModel model = new SaLoginModel();
        model.setDevice(client.getDeviceType());
        // 自定义分配 不同用户体系 不同 token 授权时间 不设置默认走全局 yml 配置
        // 例如: 后台用户30分钟过期 app用户1天过期
        model.setTimeout(client.getTimeout());
        model.setActiveTimeout(client.getActiveTimeout());
        model.setExtra(LoginHelper.CLIENT_KEY, client.getClientId());
        // 生成token
        LoginHelper.login(loginUser, model);

        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(client.getClientId());

        return loginVo;
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code     验证码
     * @param uuid     唯一标识
     */
    private void validateCaptcha(Long tenantId, String username, String code, String uuid) {
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + StringUtils.defaultString(uuid, "");
        String captcha = RedisUtils.getCacheObject(verifyKey);
        RedisUtils.deleteObject(verifyKey);
        if (captcha == null) {
            loginService.recordLogininfor(tenantId, username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha)) {
            loginService.recordLogininfor(tenantId, username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error"));
            throw new CaptchaException();
        }
    }

    private SysUserVo loadUserByUsername(Long tenantId, String username) {
        SysUserVo user = userService.selectTenantUserByUserName(tenantId, username);
        if (ObjectUtil.isNull(user)) {
            log.info("登录用户：{} 不存在.", username);
            throw new UserException("user.not.exists", username);
        } else if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            throw new UserException("user.blocked", username);
        }
        return user;
    }

}
