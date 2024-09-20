package com.hisw.common.web.config;

import com.hisw.common.web.aspectj.RepeatSubmitAspect;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConfiguration;

/**
 * 幂等功能配置
 *
 * @author lejav
 */
@AutoConfiguration(after = RedisConfiguration.class)
public class RepeatSubmitConfig {

    @Bean
    public RepeatSubmitAspect repeatSubmitAspect() {
        return new RepeatSubmitAspect();
    }

}
