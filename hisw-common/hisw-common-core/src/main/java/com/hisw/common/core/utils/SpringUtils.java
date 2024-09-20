package com.hisw.common.core.utils;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.aop.framework.AopContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * spring工具类，方便在非spring管理环境中获取bean
 *
 * @author lejav
 */
@Component
public final class SpringUtils extends SpringUtil {
    /**
     * 获取spring上下文
     */
    public static ApplicationContext context() {
        return getApplicationContext();
    }

    /**
     * 获取aop代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker) {
        return (T) AopContext.currentProxy();
    }

    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     *
     * @return boolean
     */
    public static boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    /**
     * 获取当前的环境配置，无配置返回null
     *
     * @return 当前的环境配置
     */
    public static String[] getActiveProfiles() {
        return context().getEnvironment().getActiveProfiles();
    }

    /**
     * 获取当前的环境配置，当有多个环境配置时，只获取第一个
     *
     * @return 当前的环境配置
     */
    public static String getActiveProfile() {
        final String[] activeProfiles = getActiveProfiles();
        return StringUtils.isNotEmpty(activeProfiles) ? activeProfiles[0] : null;
    }

}
