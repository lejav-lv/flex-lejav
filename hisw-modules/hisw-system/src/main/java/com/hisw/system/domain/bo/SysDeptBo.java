package com.hisw.system.domain.bo;

import com.hisw.common.orm.core.domain.BaseEntity;
import com.hisw.system.domain.SysDept;
import io.github.linpeilie.annotations.AutoMapper;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门业务对象 sys_dept
 *
 * @author lejav
 */

@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysDept.class, reverseConvertGenerate = false)
public class SysDeptBo extends BaseEntity {

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 父部门ID
     */
    private Long parentId;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 30, message = "部门名称长度不能超过{max}个字符")
    private String deptName;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    /**
     * 负责人
     */
    private String leader;

    /**
     * 联系电话
     */
    @Size( max = 11, message = "联系电话长度不能超过{max}个字符")
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size( max = 50, message = "邮箱长度不能超过{max}个字符")
    private String email;

    /**
     * 部门状态（0正常 1停用）
     */
    private String status;

}
