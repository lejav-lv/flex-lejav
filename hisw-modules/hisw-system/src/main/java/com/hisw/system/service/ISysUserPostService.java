package com.hisw.system.service;

import com.hisw.common.orm.core.service.IBaseService;
import com.hisw.system.domain.SysUserPost;

/**
 * @author lejav
 */
public interface ISysUserPostService extends IBaseService<SysUserPost> {

    /**
     * 通过用户ID删除用户和岗位关联
     *
     * @param userId 用户ID
     */
    void deleteUserPostByUserId(Long userId);

    /**
     * 批量删除用户和岗位关联
     *
     * @param ids 需要删除的数据ID
     */
    void deleteUserPost(Long[] ids);

    /**
     * 通过岗位ID查询岗位使用数量
     *
     * @param postId 岗位ID
     * @return 结果：数量
     */
    int countUserPostById(Long postId);

}
