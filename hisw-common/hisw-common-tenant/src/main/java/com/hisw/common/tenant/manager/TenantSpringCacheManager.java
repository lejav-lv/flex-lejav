package com.hisw.common.tenant.manager;

import com.hisw.common.core.constant.GlobalConstants;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.redis.manager.FlexSpringCacheManager;
import com.hisw.common.tenant.helper.TenantHelper;
import lombok.NoArgsConstructor;
import org.springframework.cache.Cache;

/**
 * 重写 cacheName 处理方法 支持多租户
 *
 * @author lejav
 */
@NoArgsConstructor
public class TenantSpringCacheManager extends FlexSpringCacheManager {

    @Override
    public Cache getCache(String name) {
        if (StringUtils.contains(name, GlobalConstants.GLOBAL_REDIS_KEY)) {
            return super.getCache(name);
        }
        Long tenantId = TenantHelper.getTenantId();
        if (StringUtils.startsWith(name, tenantId + "")) {
            // 如果存在则直接返回
            return super.getCache(name);
        }
        return super.getCache(tenantId + ":" + name);
    }

}
