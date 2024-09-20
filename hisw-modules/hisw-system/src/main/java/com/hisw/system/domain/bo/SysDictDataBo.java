package com.hisw.system.domain.bo;

import com.hisw.common.orm.core.domain.BaseEntity;
import com.hisw.system.domain.SysDictData;
import io.github.linpeilie.annotations.AutoMapper;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据业务对象 sys_dict_data
 *
 * @author lejav
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysDictData.class, reverseConvertGenerate = false)
public class SysDictDataBo extends BaseEntity {

    /**
     * 字典编码
     */
    private Long dictCode;

    /**
     * 字典排序
     */
    private Integer dictSort;

    /**
     * 字典标签
     */
    @NotBlank(message = "字典标签不能为空")
    @Size( max = 100, message = "字典标签长度不能超过{max}个字符")
    private String dictLabel;

    /**
     * 字典键值
     */
    @NotBlank(message = "字典键值不能为空")
    @Size( max = 100, message = "字典键值长度不能超过{max}个字符")
    private String dictValue;

    /**
     * 字典类型
     */
    @NotBlank(message = "字典类型不能为空")
    @Size( max = 100, message = "字典类型长度不能超过{max}个字符")
    private String dictType;

    /**
     * 样式属性（其他样式扩展）
     */
    @Size( max = 100, message = "样式属性长度不能超过{max}个字符")
    private String cssClass;

    /**
     * 表格回显样式
     */
    private String listClass;

    /**
     * 是否默认（Y是 N否）
     */
    private String isDefault;

    /**
     * 创建部门
     */
    private Long createDept;

    /**
     * 备注
     */
    private String remark;

}
