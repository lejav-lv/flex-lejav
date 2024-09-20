package com.hisw.system.mapper;

import com.mybatisflex.core.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.hisw.system.domain.SysUserRole;

/**
 * 用户与角色关联表 数据层
 *
 * @author lejav
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {


}
