package com.hisw.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mybatisflex.annotation.ColumnMask;
import com.mybatisflex.annotation.RelationManyToMany;
import com.mybatisflex.annotation.RelationOneToOne;
import com.mybatisflex.core.mask.Masks;
import com.hisw.common.orm.core.domain.BaseEntity;
import com.hisw.system.domain.SysUser;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;


import java.util.Date;
import java.util.List;


/**
 * 用户信息视图对象 sys_user
 *
 * @author lejav
 */
@Data
@AutoMapper(target = SysUser.class)
public class SysUserVo extends BaseEntity {


    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户类型（sys_user系统用户、app_user手机端用户）
     */
    private String userType;

    /**
     * 用户邮箱
     */
    @ColumnMask(Masks.EMAIL)
    private String email;

    /**
     * 手机号码
     */
    @ColumnMask(Masks.MOBILE)
    private String phonenumber;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private String gender;

    /**
     * 头像地址
     */
    private Long avatar;

    /**
     * 头像地址URL
     */
    @RelationOneToOne(
        selfField = "avatar",
        targetTable = "sys_oss",
        targetField = "ossId",
        valueField = "url"
    )
    private String url;

    /**
     * 密码
     */
    @JsonIgnore
    @JsonProperty
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private Date loginDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 部门对象
     */
    @RelationOneToOne(
        selfField = "deptId", joinSelfColumn = "dept_id",
        targetField = "deptId", joinTargetColumn = "dept_id",
        targetTable = "sys_dept")
    private SysDeptVo dept;

    /**
     * 角色对象
     */
    @RelationManyToMany(
        selfField = "userId", joinSelfColumn = "user_id",
        targetTable = "sys_role", targetField = "roleId",
        joinTargetColumn = "role_id", joinTable = "sys_user_role")
    private List<SysRoleVo> roles;

    /**
     * 角色组
     */
    private Long[] roleIds;

    /**
     * 岗位组
     */
    private Long[] postIds;

    /**
     * 数据权限 当前角色ID
     */
    private Long roleId;

}
