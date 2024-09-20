package com.hisw.system.domain;

import java.util.ArrayList;
import java.util.List;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.hisw.common.orm.core.domain.BaseEntity;

/**
 * 部门表 sys_dept
 *
 * @author lejav
 */
@Data
@Table(value = "sys_dept")
public class SysDept extends BaseEntity
{

    /** 部门ID */
    @Id
    private Long deptId;

    /** 父部门ID */
    private Long parentId;

    /** 祖级列表 */
    private String ancestors;

    /** 部门名称 */
    private String deptName;

    /** 显示顺序 */
    private Integer orderNum;

    /** 负责人 */
    private String leader;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 部门状态:0正常,1停用 */
    private String status;

    /** 删除标志（0代表存在 1代表删除） */
    private Integer delFlag;

    /** 父部门名称 */
    @Column(ignore = true)
    private String parentName;

    /** 子部门 */
    private List<SysDept> children = new ArrayList<>();
}
