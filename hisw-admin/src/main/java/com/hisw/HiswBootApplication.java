package com.hisw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 启动程序
 *
 * @author lejav
 */
@SpringBootApplication(exclude = SpringDataWebAutoConfiguration.class)
public class HiswBootApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(HiswBootApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
    }
}
