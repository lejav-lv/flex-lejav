package com.hisw.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


import java.io.Serializable;
import java.util.Date;


/**
 * 字典类型视图对象 sys_dict_type
 *
 * @author lejav
 */
@Data
@ExcelIgnoreUnannotated
public class SysDictTypeVo implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 字典主键
     */
    @ExcelProperty(value = "字典主键")
    private Long dictId;

    /**
     * 字典名称
     */
    @ExcelProperty(value = "字典名称")
    private String dictName;

    /**
     * 字典类型
     */
    @ExcelProperty(value = "字典类型")
    private String dictType;

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
