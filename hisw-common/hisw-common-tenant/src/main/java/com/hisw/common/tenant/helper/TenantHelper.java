package com.hisw.common.tenant.helper;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.spring.SpringMVCUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.hisw.common.core.constant.GlobalConstants;
import com.hisw.common.redis.utils.RedisUtils;
import com.hisw.common.security.utils.LoginHelper;
import com.mybatisflex.core.tenant.TenantManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * 租户助手
 *
 * @author lejav
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TenantHelper {

    private static final String DYNAMIC_TENANT_KEY = GlobalConstants.GLOBAL_REDIS_KEY + "dynamicTenant";

    private static final ThreadLocal<Long> TEMP_DYNAMIC_TENANT = new TransmittableThreadLocal<>();

    /**
     * 开启忽略租户(开启后需手动调用 {@link #disableIgnore()} 关闭)
     */
    public static void enableIgnore() {
        TenantManager.ignoreTenantCondition();
    }

    /**
     * 关闭忽略租户
     */
    public static void disableIgnore() {
        TenantManager.restoreTenantCondition();
    }

    /**
     * 在忽略租户中执行
     *
     * @param handle 处理执行方法
     */
    public static void ignore(Runnable handle) {
        enableIgnore();
        try {
            handle.run();
        } finally {
            disableIgnore();
        }
    }

    /**
     * 在忽略租户中执行
     *
     * @param handle 处理执行方法
     */
    public static <T> T ignore(Supplier<T> handle) {
        enableIgnore();
        try {
            return handle.get();
        } finally {
            disableIgnore();
        }
    }

    /**
     * 设置动态租户(一直有效 需要手动清理)
     * <p>
     * 如果为非web环境 那么只在当前线程内生效
     */
    public static void setDynamic(Long tenantId) {
        if (!SpringMVCUtil.isWeb()) {
            TEMP_DYNAMIC_TENANT.set(tenantId);
            return;
        }
        String cacheKey = DYNAMIC_TENANT_KEY + ":" + LoginHelper.getUserId();
        RedisUtils.setCacheObject(cacheKey, tenantId);
        SaHolder.getStorage().set(cacheKey, tenantId);
    }

    /**
     * 获取动态租户(一直有效 需要手动清理)
     * <p>
     * 如果为非web环境 那么只在当前线程内生效
     */
    public static Long getDynamic() {
        if (!SpringMVCUtil.isWeb()) {
            return TEMP_DYNAMIC_TENANT.get();
        }
        String cacheKey = DYNAMIC_TENANT_KEY + ":" + LoginHelper.getUserId();
        Long tenantId = (Long) SaHolder.getStorage().get(cacheKey);
        if (ObjectUtil.isNotNull(tenantId)) {
            return tenantId;
        }
        if (ObjectUtil.isNotNull(RedisUtils.getCacheObject(cacheKey))) {
            tenantId = Long.valueOf(RedisUtils.getCacheObject(cacheKey));
        }
        if (ObjectUtil.isNotNull(tenantId)) {
            SaHolder.getStorage().set(cacheKey, tenantId);
        }
        return tenantId;
    }

    /**
     * 清除动态租户
     */
    public static void clearDynamic() {
        if (!SpringMVCUtil.isWeb()) {
            TEMP_DYNAMIC_TENANT.remove();
            return;
        }
        String cacheKey = DYNAMIC_TENANT_KEY + ":" + LoginHelper.getUserId();
        RedisUtils.deleteObject(cacheKey);
        SaHolder.getStorage().delete(cacheKey);
    }

    /**
     * 在动态租户中执行
     *
     * @param handle 处理执行方法
     */
    public static void dynamic(Long tenantId, Runnable handle) {
        setDynamic(tenantId);
        try {
            handle.run();
        } finally {
            clearDynamic();
        }
    }

    /**
     * 获取当前租户id(动态租户优先)
     */
    public static Long getTenantId() {
        Long tenantId = TenantHelper.getDynamic();
        if (ObjectUtil.isNull(tenantId)) {
            tenantId = LoginHelper.getTenantId();
        }
        return tenantId;
    }

}
