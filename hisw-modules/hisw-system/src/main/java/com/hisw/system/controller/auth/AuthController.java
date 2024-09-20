package com.hisw.system.controller.auth;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.ObjectUtil;
import com.hisw.common.core.constant.UserConstants;
import com.hisw.common.core.core.domain.R;
import com.hisw.common.core.core.domain.model.LoginBody;
import com.hisw.common.core.core.domain.model.RegisterBody;
import com.hisw.common.core.utils.MessageUtils;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.core.utils.ValidatorUtils;
import com.hisw.common.encrypt.annotation.ApiEncrypt;
import com.hisw.common.json.utils.JsonUtils;
import com.hisw.common.security.utils.LoginHelper;
import com.hisw.common.websocket.utils.WebSocketUtils;
import com.hisw.system.domain.vo.LoginVo;
import com.hisw.system.domain.vo.SysClientVo;
import com.hisw.system.service.*;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.hisw.system.domain.table.SysClientTableDef.SYS_CLIENT;


/**
 * 认证
 *
 * @author lejav
 */
@Slf4j
@SaIgnore
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SysLoginService loginService;
    private final ISysClientService clientService;
    private final SysRegisterService registerService;
    private final ISysConfigService configService;
    private final ScheduledExecutorService scheduledExecutorService;

    /**
     * 登录方法
     *
     * @param body 登录信息
     * @return 结果
     */
    @ApiEncrypt
    @PostMapping("/login")
    public R<LoginVo> login(@RequestBody String body) {
        LoginBody loginBody = JsonUtils.parseObject(body, LoginBody.class);
        ValidatorUtils.validate(loginBody);
        // 授权类型和客户端id
        String clientId = loginBody.getClientId();
        String grantType = loginBody.getGrantType();
        QueryWrapper query = QueryWrapper.create().from(SYS_CLIENT).where(SYS_CLIENT.CLIENT_ID.eq(clientId));
        SysClientVo client = clientService.getOneAs(query, SysClientVo.class);
        // 查询不到 client 或 client 内不包含 grantType
        if (ObjectUtil.isNull(client) || !StringUtils.contains(client.getGrantType(), grantType)) {
            log.info("客户端id: {} 认证类型：{} 异常!.", clientId, grantType);
            return R.fail(MessageUtils.message("auth.grant.type.error"));
        } else if (!UserConstants.NORMAL.equals(client.getStatus())) {
            return R.fail(MessageUtils.message("auth.grant.type.blocked"));
        }
        // 校验租户
        loginService.checkTenant(loginBody.getTenantId());
        // 登录
        LoginVo loginVo = IAuthStrategy.login(body, client, grantType);
        Long userId = LoginHelper.getUserId();
        scheduledExecutorService.schedule(() -> WebSocketUtils.sendMessage(userId, "欢迎登录Hisw-Flex后台管理系统"), 3, TimeUnit.SECONDS);
        return R.ok(loginVo);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public R<Void> logout() {
        loginService.logout();
        return R.ok("退出成功！");
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public R<Void> register(@Validated @RequestBody RegisterBody user) {
        if (!configService.selectRegisterEnabled(user.getTenantId())) {
            return R.fail("当前系统没有开启注册功能！");
        }
        registerService.register(user);
        return R.ok();
    }

}
