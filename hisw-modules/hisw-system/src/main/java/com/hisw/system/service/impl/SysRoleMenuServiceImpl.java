package com.hisw.system.service.impl;

import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.hisw.common.orm.core.service.impl.BaseServiceImpl;
import com.hisw.system.domain.SysRoleMenu;
import com.hisw.system.mapper.SysRoleMenuMapper;
import com.hisw.system.service.ISysRoleMenuService;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.hisw.system.domain.table.SysRoleMenuTableDef.SYS_ROLE_MENU;

/**
 * SysRoleMenu服务实现类
 *
 * @author lejav
 */
@Service
public class SysRoleMenuServiceImpl extends BaseServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements ISysRoleMenuService {

    @Resource
    private SysRoleMenuMapper roleMenuMapper;

    /**
     * 批量删除角色菜单关联信息
     *
     * @param ids 需要删除的数据ID
     */
    @Override
    public void deleteRoleMenu(Long[] ids) {
        QueryWrapper queryWrapper = QueryWrapper.create().from(SYS_ROLE_MENU).where(SYS_ROLE_MENU.ROLE_ID.in(Arrays.asList(ids)));
        this.remove(queryWrapper);
    }

    /**
     * 通过角色ID删除用户和菜单关联
     *
     * @param roleId 角色ID
     */
    @Override
    public void deleteRoleMenuByRoleId(Long roleId) {
        QueryWrapper queryWrapper = QueryWrapper.create().from(SYS_ROLE_MENU).where(SYS_ROLE_MENU.ROLE_ID.eq(roleId));
        this.remove(queryWrapper);
    }

    /**
     * 查询菜单使用数量
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public int checkMenuExistRole(Long menuId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(QueryMethods.count(SYS_ROLE_MENU.ROLE_ID))
            .from(SYS_ROLE_MENU)
            .where(SYS_ROLE_MENU.MENU_ID.eq(menuId));
        return roleMenuMapper.selectObjectByQueryAs(queryWrapper,Integer.class);
    }
}
