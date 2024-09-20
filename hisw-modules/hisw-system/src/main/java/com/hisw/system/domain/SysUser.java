package com.hisw.system.domain;

import com.hisw.common.core.constant.UserConstants;
import com.hisw.common.orm.core.domain.BaseEntity;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 用户对象 sys_user
 *
 * @author lejav
 */
@Data
@NoArgsConstructor
@Table(value = "sys_user")
public class SysUser extends BaseEntity {
    /** 用户ID */
    @Id
    private Long userId;

    /** 部门ID */
    private Long deptId;

    /** 部门对象 */
    private SysDept dept;

    /** 用户账号 */
    private String userName;

    /** 用户昵称 */
    private String nickName;

    /**
     * 用户登录设备类型（sys_user系统用户）
     */
    private String userType;

    /** 用户邮箱 */
    private String email;

    /** 手机号码 */
    private String phonenumber;

    /** 用户性别 */
    private String gender;

    /** 用户头像 */
    private Long avatar;

    /** 密码 */
    private String password;

    /** 帐号状态（0正常 1停用） */
    private String status;

    /** 删除标志（0代表存在 1代表删除） */
    private Integer delFlag;

    /** 最后登录IP */
    private String loginIp;

    /** 最后登录时间 */
    private Date loginDate;

    /** 角色对象 */
    private List<SysRole> roles;

    /** 角色组 */
    private Long[] roleIds;


    private List<SysPost> postList;

    /** 岗位组 */
    private Long[] postIds;

    /**
     * 备注
     */
    private String remark;


    public boolean isSuperAdmin() {
        return UserConstants.SUPER_ADMIN_ID.equals(this.userId);
    }

}
