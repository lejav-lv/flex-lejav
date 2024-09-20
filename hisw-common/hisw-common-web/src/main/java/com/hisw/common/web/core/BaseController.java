package com.hisw.common.web.core;

import com.hisw.common.core.core.domain.R;
import com.hisw.common.core.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Date;

/**
 * web层通用数据处理
 *
 * @author lejav
 */
@Slf4j
public class BaseController {

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 返回成功
     */
    public <T> R<T> ok() {
        return R.ok();
    }

    /**
     * 返回失败消息
     */
    public <T> R<T> fail() {
        return R.fail();
    }

    /**
     * 返回失败消息
     */
    public <T> R<T> fail(String message) {
        return R.fail(message);
    }

    /**
     * 返回成功消息
     */
    public <T> R<T> ok(String message) {
        return R.ok(message);
    }

    /**
     * 返回成功消息
     */
    public <T> R<T> ok(T data) {
        return R.ok(data);
    }

    /**
     * 返回警告消息
     */
    public <T> R<T> warn(String message) {
        return R.warn(message);
    }

}
