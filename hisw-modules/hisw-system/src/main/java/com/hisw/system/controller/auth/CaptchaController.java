package com.hisw.system.controller.auth;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.IdUtil;
import com.hisw.common.core.constant.Constants;
import com.hisw.common.core.constant.GlobalConstants;
import com.hisw.common.core.core.domain.R;
import com.hisw.common.core.utils.SpringUtils;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.core.utils.reflect.ReflectUtils;
import com.hisw.common.encrypt.utils.RSAUtils;
import com.hisw.common.redis.utils.RedisUtils;
import com.hisw.common.web.config.properties.CaptchaProperties;
import com.hisw.common.web.enums.CaptchaType;
import com.hisw.system.domain.vo.CaptchaVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 验证码操作处理
 *
 * @author lejav
 */
@SaIgnore
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
public class CaptchaController {
    private final CaptchaProperties captchaProperties;

    /**
     * 生成验证码
     */
    @GetMapping("/auth/code")
    public R<CaptchaVo> getCode() {
        CaptchaVo captchaVo = new CaptchaVo();
        boolean captchaEnabled = captchaProperties.getEnable();
        if (!captchaEnabled) {
            captchaVo.setCaptchaEnabled(false);
            return R.ok(captchaVo);
        }
        // 保存验证码信息
        String uuid = IdUtil.simpleUUID();
        String verifyKey = GlobalConstants.CAPTCHA_CODE_KEY + uuid;
        // 生成验证码
        CaptchaType captchaType = captchaProperties.getType();
        boolean isMath = CaptchaType.MATH == captchaType;
        Integer length = isMath ? captchaProperties.getNumberLength() : captchaProperties.getCharLength();
        CodeGenerator codeGenerator = ReflectUtils.newInstance(captchaType.getClazz(), length);
        AbstractCaptcha captcha = SpringUtils.getBean(captchaProperties.getCategory().getClazz());
        captcha.setGenerator(codeGenerator);
        captcha.createCode();
        // 如果是数学验证码，使用SpEL表达式处理验证码结果
        String code = captcha.getCode();
        if (isMath) {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(StringUtils.remove(code, "="));
            code = exp.getValue(String.class);
        }
        RedisUtils.setCacheObject(verifyKey, code, Duration.ofMinutes(Constants.CAPTCHA_EXPIRATION));

        captchaVo.setUuid(uuid);
        captchaVo.setImg(captcha.getImageBase64());
        return R.ok(captchaVo);
    }

    @GetMapping("/genKeyPair")
    public R<Map<String, String>> genKeyPair() {
        Map<String, String> map = new HashMap<>();
        try {
            log.info("开始生产rsa秘钥");
            Map<String, Object> keyPair = RSAUtils.genKeyPair();
            String publicKey = RSAUtils.getPublicKey(keyPair);
            String privateKey = RSAUtils.getPrivateKey(keyPair);
            log.info("privateKey：" + privateKey);
            String uuid = "hisw_" + UUID.randomUUID().toString().replace("-", "");
            RedisUtils.setCacheMapValue("loginRsa", uuid, privateKey);
            RedisUtils.expire("loginRsa", 60 * 60);
            log.info("写入redis完成");
            map.put("uuidPrivateKey", uuid);
            map.put("RSA_PUBLIC_KEY", publicKey);
        } catch (Exception e) {
            return R.fail("生成RSA秘钥失败," + e.getMessage());
        }
        return R.ok(map);
    }
}
