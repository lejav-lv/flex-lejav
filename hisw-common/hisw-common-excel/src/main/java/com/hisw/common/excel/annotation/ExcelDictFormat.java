package com.hisw.common.excel.annotation;

import com.hisw.common.core.utils.StringUtils;

import java.lang.annotation.*;

/**
 * 字典格式化
 *
 * @author lejav
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelDictFormat {

    /**
     * 如果是字典类型，请设置字典的type值 (如: sys_user_gender)
     */
    String dictType() default "";

    /**
     * 读取内容转表达式 (如: 0=男,1=女,2=未知)
     */
    String readConverterExp() default "";

    /**
     * 分隔符，读取字符串组内容
     */
    String separator() default StringUtils.SEPARATOR;

}
