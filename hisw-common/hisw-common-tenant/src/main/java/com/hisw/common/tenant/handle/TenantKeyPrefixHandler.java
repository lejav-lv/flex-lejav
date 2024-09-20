package com.hisw.common.tenant.handle;

import cn.hutool.core.util.ObjectUtil;
import com.hisw.common.core.constant.GlobalConstants;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.redis.handler.KeyPrefixHandler;
import com.hisw.common.tenant.helper.TenantHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * 多租户redis缓存key前缀处理
 *
 * @author lejav
 */
@Slf4j
public class TenantKeyPrefixHandler extends KeyPrefixHandler {

    public TenantKeyPrefixHandler(String keyPrefix) {
        super(keyPrefix);
    }

    /**
     * 增加前缀
     */
    @Override
    public String map(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        if (StringUtils.contains(name, GlobalConstants.GLOBAL_REDIS_KEY)) {
            return super.map(name);
        }
        String key;
        Long tenantId = TenantHelper.getTenantId();
        if (ObjectUtil.isNull(tenantId)) {
            key = "";
        }else {
            key = String.valueOf(tenantId);
        }
        if (StringUtils.startsWith(name, key)) {
            // 如果存在则直接返回
            return super.map(name);
        }
        return super.map(key + ":" + name);
    }

    /**
     * 去除前缀
     */
    @Override
    public String unmap(String name) {
        String unmap = super.unmap(name);
        if (StringUtils.isBlank(unmap)) {
            return null;
        }
        if (StringUtils.contains(name, GlobalConstants.GLOBAL_REDIS_KEY)) {
            return super.unmap(name);
        }
        Long tenantId = TenantHelper.getTenantId();
        if (StringUtils.startsWith(unmap, tenantId + "")) {
            // 如果存在则删除
            return unmap.substring((tenantId + ":").length());
        }
        return unmap;
    }

}
