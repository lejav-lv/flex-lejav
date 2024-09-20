package com.hisw.system.controller.auth;

import cn.dev33.satoken.annotation.SaIgnore;
import com.hisw.common.core.config.HiswBootConfig;
import com.hisw.common.core.utils.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 首页
 *
 * @author lejav
 */
@SaIgnore
@RestController
public class SysIndexController {

    /** 系统基础配置 */
    @Resource
    private HiswBootConfig hawBootConfig;

    /**
     * 访问首页，提示语
     */
    @RequestMapping
    public String index() {
        return StringUtils.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。", hawBootConfig.getName(), hawBootConfig.getVersion());
    }
}
