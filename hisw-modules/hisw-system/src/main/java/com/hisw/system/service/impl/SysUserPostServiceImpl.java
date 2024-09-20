package com.hisw.system.service.impl;

import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.hisw.common.orm.core.service.impl.BaseServiceImpl;
import com.hisw.system.domain.SysUserPost;
import com.hisw.system.mapper.SysUserPostMapper;
import com.hisw.system.service.ISysUserPostService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.hisw.system.domain.table.SysUserPostTableDef.SYS_USER_POST;

/**
 * @author lejav
 */
@Service
public class SysUserPostServiceImpl  extends BaseServiceImpl<SysUserPostMapper, SysUserPost> implements ISysUserPostService {

    @Resource
    private SysUserPostMapper userPostMapper;

    @Override
    public QueryWrapper query() {
        return super.query().from(SYS_USER_POST);
    }


    /**
     * 通过用户ID删除用户和岗位关联
     *
     * @param userId 用户ID
     */
    @Override
    public void deleteUserPostByUserId(Long userId) {
        QueryWrapper queryWrapper = query().where(SYS_USER_POST.USER_ID.eq(userId));
        this.remove(queryWrapper);
    }

    /**
     * 批量删除用户和岗位关联
     *
     * @param ids 需要删除的数据ID
     */
    @Override
    public void deleteUserPost(Long[] ids) {
        QueryWrapper queryWrapper = query().where(SYS_USER_POST.USER_ID.in(Arrays.asList(ids)));
        this.remove(queryWrapper);
    }

    /**
     * 通过岗位ID查询岗位使用数量
     * @param postId 岗位ID
     * @return 结果：数量
     */
    @Override
    public int countUserPostById(Long postId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(QueryMethods.count(SYS_USER_POST.USER_ID))
            .from(SYS_USER_POST)
            .where(SYS_USER_POST.POST_ID.eq(postId));

        return userPostMapper.selectObjectByQueryAs(queryWrapper,Integer.class);
    }
}
