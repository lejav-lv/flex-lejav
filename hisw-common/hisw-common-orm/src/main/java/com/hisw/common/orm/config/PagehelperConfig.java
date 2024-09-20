package com.hisw.common.orm.config;

import com.github.pagehelper.PageInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Pagehelper分页，兼用老项目
 *
 * @author lejav
 */
@Configuration
public class PagehelperConfig {
    @Bean
    public PageInterceptor pageInterceptor(){
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("supportMethodsArguments","true");
        properties.setProperty("autoRuntimeDialect","true");
        pageInterceptor.setProperties(properties);

        return pageInterceptor;
    }
}
