package com.hisw.system.mapper;

import com.mybatisflex.core.BaseMapper;
import com.hisw.system.domain.SysRoleDept;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色与部门关联表 数据层
 *
 * @author lejav
 */
@Mapper
public interface SysRoleDeptMapper extends BaseMapper<SysRoleDept> {

}
