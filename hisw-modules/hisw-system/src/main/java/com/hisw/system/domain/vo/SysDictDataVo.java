package com.hisw.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.hisw.common.excel.annotation.ExcelDictFormat;
import com.hisw.common.excel.convert.ExcelDictConvert;
import lombok.Data;


import java.io.Serializable;
import java.util.Date;


/**
 * 字典数据视图对象 sys_dict_data
 *
 * @author lejav
 */
@Data
@ExcelIgnoreUnannotated
public class SysDictDataVo implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 字典编码
     */
    @ExcelProperty(value = "字典编码")
    private Long dictCode;

    /**
     * 字典排序
     */
    @ExcelProperty(value = "字典排序")
    private Integer dictSort;

    /**
     * 字典标签
     */
    @ExcelProperty(value = "字典标签")
    private String dictLabel;

    /**
     * 字典键值
     */
    @ExcelProperty(value = "字典键值")
    private String dictValue;

    /**
     * 字典类型
     */
    @ExcelProperty(value = "字典类型")
    private String dictType;

    /**
     * 样式属性（其他样式扩展）
     */
    private String cssClass;

    /**
     * 表格回显样式
     */
    private String listClass;

    /**
     * 是否默认（Y是 N否）
     */
    @ExcelProperty(value = "是否默认", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_yes_no")
    private String isDefault;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /** 乐观锁 */
    @ExcelProperty(value = "乐观锁版本号")
    private Integer version;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

}
