package com.hisw.system.mapper;

import com.mybatisflex.core.BaseMapper;
import com.hisw.system.domain.SysUserPost;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户与岗位关联表 数据层
 *
 * @author lejav
 */
@Mapper
public interface SysUserPostMapper extends BaseMapper<SysUserPost> {

}
