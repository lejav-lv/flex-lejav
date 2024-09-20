package com.hisw.common.core.config;

import cn.hutool.core.util.ArrayUtil;
import com.hisw.common.core.exception.ServiceException;
import com.hisw.common.core.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.util.Arrays;
import java.util.concurrent.Executor;

/**
 * 异步配置
 * 如果未使用虚拟线程则生效
 * @author lejav
 */
@Slf4j
@AutoConfiguration
public class AsyncConfig implements AsyncConfigurer {

    /**
     * 自定义 @Async 注解使用系统线程池
     */
    @Override
    public Executor getAsyncExecutor() {
        return SpringUtils.getBean("scheduledExecutorService");
    }

    /**
     * 异步执行异常处理
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            log.error("异步任务异常：", throwable);
            StringBuilder sb = new StringBuilder();
            sb.append("Exception message - ").append(throwable.getMessage())
                .append(", Method name - ").append(method.getName());
            if (ArrayUtil.isNotEmpty(objects)) {
                sb.append(", Parameter value - ").append(Arrays.toString(objects));
            }
            throw new ServiceException(sb.toString());
        };
    }

}
