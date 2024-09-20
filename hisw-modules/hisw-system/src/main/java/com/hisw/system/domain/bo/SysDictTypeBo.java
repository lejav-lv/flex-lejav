package com.hisw.system.domain.bo;

import com.hisw.common.orm.core.domain.BaseEntity;
import com.hisw.system.domain.SysDictType;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 字典类型业务对象 sys_dict_type
 *
 * @author lejav
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysDictType.class, reverseConvertGenerate = false)
public class SysDictTypeBo extends BaseEntity {

    /**
     * 字典主键
     */
    private Long dictId;

    /**
     * 字典名称
     */
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典类型名称长度不能超过{max}个字符")
    private String dictName;

    /**
     * 字典类型
     */
    @NotBlank(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型类型长度不能超过{max}个字符")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "字典类型必须以字母开头，且只能为（小写字母，数字，下滑线）")
    private String dictType;

    /**
     * 备注
     */
    private String remark;


}
