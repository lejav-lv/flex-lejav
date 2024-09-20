package com.hisw.system.domain;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 用户和岗位关联 sys_user_post
 *
 * @author lejav
 */
@Data
@Table(value = "sys_user_post")
public class SysUserPost {
    /** 用户ID */
    @Id
    private Long userId;

    /** 岗位ID */
    @Id
    private Long postId;
}
