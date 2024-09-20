package com.hisw.common.tenant.config;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.hutool.core.util.ObjectUtil;
import com.hisw.common.core.utils.reflect.ReflectUtils;
import com.hisw.common.orm.config.MyBatisFlexConfig;
import com.hisw.common.redis.config.RedisConfig;
import com.hisw.common.redis.config.properties.RedissonProperties;
import com.hisw.common.tenant.core.TenantSaTokenDao;
import com.hisw.common.tenant.handle.MyTenantFactory;
import com.hisw.common.tenant.handle.TenantKeyPrefixHandler;
import com.hisw.common.tenant.manager.TenantSpringCacheManager;
import com.mybatisflex.core.tenant.TenantFactory;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 租户配置类
 *
 * @author lejav
 */
@AutoConfiguration(after = {RedisConfig.class, MyBatisFlexConfig.class})
public class TenantConfig {

    /**
     * 多租户工厂，用于获取当前租户ID
     */
    @Bean
    public TenantFactory tenantFactory() {
        return new MyTenantFactory();
    }

    @Bean
    public RedissonAutoConfigurationCustomizer tenantRedissonCustomizer(RedissonProperties redissonProperties) {
        return config -> {
            TenantKeyPrefixHandler nameMapper = new TenantKeyPrefixHandler(redissonProperties.getKeyPrefix());
            SingleServerConfig singleServerConfig = ReflectUtils.invokeGetter(config, "singleServerConfig");
            if (ObjectUtil.isNotNull(singleServerConfig)) {
                // 使用单机模式
                // 设置多租户 redis key前缀
                singleServerConfig.setNameMapper(nameMapper);
                ReflectUtils.invokeSetter(config, "singleServerConfig", singleServerConfig);
            }
            ClusterServersConfig clusterServersConfig = ReflectUtils.invokeGetter(config, "clusterServersConfig");
            // 集群配置方式 参考下方注释
            if (ObjectUtil.isNotNull(clusterServersConfig)) {
                // 设置多租户 redis key前缀
                clusterServersConfig.setNameMapper(nameMapper);
                ReflectUtils.invokeSetter(config, "clusterServersConfig", clusterServersConfig);
            }
        };
    }

    /**
     * 多租户缓存管理器
     */
    @Primary
    @Bean
    public CacheManager tenantCacheManager() {
        return new TenantSpringCacheManager();
    }

    /**
     * 多租户鉴权dao实现
     */
    @Primary
    @Bean
    public SaTokenDao tenantSaTokenDao() {
        return new TenantSaTokenDao();
    }

}
