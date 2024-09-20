package com.hisw.system.mapper;

import com.mybatisflex.core.BaseMapper;
import com.hisw.system.domain.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色与菜单关联表 数据层
 *
 * @author lejav
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {


}
