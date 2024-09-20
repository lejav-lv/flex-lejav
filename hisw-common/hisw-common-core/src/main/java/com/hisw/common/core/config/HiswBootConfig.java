package com.hisw.common.core.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 *
 * @author lejav
 */
@Data
@Component
@ConfigurationProperties(prefix = "hisw")
public class HiswBootConfig {

    /**
     * 项目名称
     */
    private String name;

    /**
     * 版本
     */
    private String version;

    /**
     * 文件路径
     */
    @Getter
    private static String profile;

    /**
     * 获取上传路径
     */
    public static String getUploadPath() {
        return getProfile() + "/upload";
    }
}
