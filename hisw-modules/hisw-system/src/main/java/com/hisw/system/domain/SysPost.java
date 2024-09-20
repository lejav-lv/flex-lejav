package com.hisw.system.domain;

import com.hisw.common.orm.core.domain.BaseEntity;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 岗位表 sys_post
 *
 * @author lejav
 */
@Data
@Table(value = "sys_post")
public class SysPost extends BaseEntity {
    /** 岗位序号 */
    @Id
    private Long postId;

    /** 岗位编码 */
    private String postCode;

    /** 岗位名称 */
    private String postName;

    /** 岗位排序 */
    private Integer postSort;

    /** 状态（0正常 1停用） */
    private String status;

    /** 备注   */
    private String remark;

    /** 用户是否存在此岗位标识 默认不存在 */
    @Column(ignore = true)
    private boolean flag = false;
}
