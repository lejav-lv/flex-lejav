package com.hisw.system.service;

import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.orm.core.service.IBaseService;
import com.hisw.system.domain.SysPost;
import com.hisw.system.domain.bo.SysPostBo;
import com.hisw.system.domain.vo.SysPostVo;

import java.util.List;

/**
 * 岗位信息 服务层
 *
 * @author lejav
 */
public interface ISysPostService extends IBaseService<SysPost> {
    /**
     * 查询岗位信息集合
     *
     * @param post 岗位信息
     * @return 岗位列表
     */
    List<SysPostVo> selectPostList(SysPostBo post);

    /**
     * 分页查询公告列表
     *
     * @param postBo 公告信息
     * @return 公告集合
     */
    TableDataInfo<SysPostVo> selectPage(SysPostBo postBo);

    /**
     * 通过岗位ID查询岗位信息
     *
     * @param postId 岗位ID
     * @return 角色对象信息
     */
    SysPostVo selectPostById(Long postId);

    /**
     * 根据用户ID获取岗位选择框列表
     *
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    List<Long> selectPostListByUserId(Long userId);

    /**
     * 通过岗位ID串查询岗位
     *
     * @param postIds 岗位id串
     * @return 岗位列表信息
     */
    List<SysPostVo> selectPostByIds(List<Long> postIds);

    /**
     * 校验岗位名称
     *
     * @param post 岗位信息
     * @return 结果
     */
    boolean checkPostNameUnique(SysPostBo post);

    /**
     * 校验岗位编码
     *
     * @param post 岗位信息
     * @return 结果
     */
    boolean checkPostCodeUnique(SysPostBo post);

    /**
     * 批量删除岗位信息
     *
     * @param postIds 需要删除的岗位ID
     * @return 结果:true 删除成功，false 删除失败。
     */
    boolean deletePostByIds(Long[] postIds);

    /**
     * 新增保存岗位信息
     *
     * @param post 岗位信息
     * @return true 操作成功，false 操作失败
     */
    boolean insertPost(SysPostBo post);

    /**
     * 修改保存岗位信息
     *
     * @param post 岗位信息
     * @return 结果:true 更新成功，false 更新失败
     */
    boolean updatePost(SysPostBo post);


    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果:SysPostVo集合
     */
    List<SysPostVo> selectPostsByUserName(String userName);
}
