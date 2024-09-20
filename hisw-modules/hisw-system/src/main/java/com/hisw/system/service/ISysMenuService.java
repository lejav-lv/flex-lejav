package com.hisw.system.service;

import cn.hutool.core.lang.tree.Tree;
import com.hisw.common.orm.core.service.IBaseService;
import com.hisw.system.domain.SysMenu;
import com.hisw.system.domain.bo.SysMenuBo;
import com.hisw.system.domain.vo.RouterVo;
import com.hisw.system.domain.vo.SysMenuVo;

import java.util.List;
import java.util.Set;

/**
 * 菜单 业务层
 *
 * @author lejav
 */
public interface ISysMenuService extends IBaseService<SysMenu> {
    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenuVo> selectMenuList(Long userId);

    /**
     * 根据用户查询系统菜单列表
     *
     * @param menu 菜单信息
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenuVo> selectMenuList(SysMenuBo menu, Long userId);

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> selectMenuPermsByUserId(Long userId);

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    Set<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单树信息
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    List<Long> selectMenuListByRoleId(Long roleId);

    /**
     * 根据租户套餐ID查询菜单树信息
     *
     * @param packageId 租户套餐ID
     * @return 选中菜单列表
     */
    List<Long> selectMenuListByPackageId(Long packageId);

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    List<RouterVo> buildMenus(List<SysMenu> menus);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    List<Tree<Long>> buildMenuTreeSelect(List<SysMenuVo> menus);

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    SysMenuVo selectMenuById(Long menuId);

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    boolean hasChildByMenuId(Long menuId);

    /**
     * 查询菜单是否存在角色
     *
     * @param menuId 菜单ID
     * @return 结果 true 存在 false 不存在
     */
    boolean checkMenuExistRole(Long menuId);

    /**
     * 新增保存菜单信息
     *
     * @param menuBo 菜单信息
     * @return 结果:受影响的行数
     */
    int insertMenu(SysMenuBo menuBo);

    /**
     * 修改保存菜单信息
     *
     * @param menuBo 菜单信息
     * @return 结果:true 更新成功，false 更新失败
     */
    boolean updateMenu(SysMenuBo menuBo);

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果:true 删除成功，false 删除失败。
     */
    boolean deleteMenuById(Long menuId);

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    boolean checkMenuNameUnique(SysMenuBo menu);
}
