package com.hisw.common.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.hisw.common.redis.config.properties.RedissonProperties;
import com.hisw.common.redis.handler.KeyPrefixHandler;
import lombok.extern.slf4j.Slf4j;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * redis配置
 *
 * @author lejav
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(RedissonProperties.class)
public class RedisConfig {

    @Autowired
    private RedissonProperties redissonProperties;

    @Resource
    private ObjectMapper objectMapper;

    @Bean
    public RedissonAutoConfigurationCustomizer redissonCustomizer() {
        return config -> {
            ObjectMapper om = objectMapper.copy();
            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            // 指定序列化输入的类型，类必须是非final修饰的。序列化时将对象全类名一起保存下来
            om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
            TypedJsonJacksonCodec jsonCodec = new TypedJsonJacksonCodec(Object.class, om);
            // 组合序列化 key 使用 String 内容使用通用 json 格式
            CompositeCodec codec = new CompositeCodec(StringCodec.INSTANCE, jsonCodec, jsonCodec);
            config.setThreads(redissonProperties.getThreads())
                .setNettyThreads(redissonProperties.getNettyThreads())
                // 缓存 Lua 脚本 减少网络传输(redisson 大部分的功能都是基于 Lua 脚本实现)
                .setUseScriptCache(true)
                .setCodec(codec);
            RedissonProperties.SingleServerConfig singleServerConfig = redissonProperties.getSingleServerConfig();
            // 使用单机模式
            config.useSingleServer()
                //设置redis key前缀
                .setNameMapper(new KeyPrefixHandler(redissonProperties.getKeyPrefix()))
                .setTimeout(singleServerConfig.getTimeout())
                .setClientName(singleServerConfig.getClientName())
                .setIdleConnectionTimeout(singleServerConfig.getIdleConnectionTimeout())
                .setSubscriptionConnectionPoolSize(singleServerConfig.getSubscriptionConnectionPoolSize())
                .setConnectionMinimumIdleSize(singleServerConfig.getConnectionMinimumIdleSize())
                .setConnectionPoolSize(singleServerConfig.getConnectionPoolSize());
            log.info("初始化 redis 配置");
        };
    }
}
