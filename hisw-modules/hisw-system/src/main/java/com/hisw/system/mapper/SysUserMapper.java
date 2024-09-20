package com.hisw.system.mapper;

import com.mybatisflex.core.BaseMapper;
import com.hisw.system.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 数据层
 *
 * @author lejav
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
