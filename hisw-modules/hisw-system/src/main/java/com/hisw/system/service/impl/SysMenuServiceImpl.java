package com.hisw.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.tree.Tree;
import com.hisw.common.core.constant.Constants;
import com.hisw.common.core.constant.UserConstants;
import com.hisw.common.core.utils.MapstructUtils;
import com.hisw.common.core.utils.StreamUtils;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.core.utils.TreeBuildUtils;
import com.hisw.common.orm.core.service.impl.BaseServiceImpl;
import com.hisw.common.security.utils.LoginHelper;
import com.hisw.system.domain.SysMenu;
import com.hisw.system.domain.SysTenantPackage;
import com.hisw.system.domain.bo.SysMenuBo;
import com.hisw.system.domain.vo.MetaVo;
import com.hisw.system.domain.vo.RouterVo;
import com.hisw.system.domain.vo.SysMenuVo;
import com.hisw.system.domain.vo.SysRoleVo;
import com.hisw.system.mapper.SysMenuMapper;
import com.hisw.system.mapper.SysTenantPackageMapper;
import com.hisw.system.service.ISysMenuService;
import com.hisw.system.service.ISysRoleMenuService;
import com.hisw.system.service.ISysRoleService;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.hisw.system.domain.table.SysMenuTableDef.SYS_MENU;
import static com.hisw.system.domain.table.SysRoleMenuTableDef.SYS_ROLE_MENU;
import static com.hisw.system.domain.table.SysRoleTableDef.SYS_ROLE;
import static com.hisw.system.domain.table.SysUserRoleTableDef.SYS_USER_ROLE;
import static com.hisw.system.domain.table.SysUserTableDef.SYS_USER;
import static com.mybatisflex.core.query.QueryMethods.distinct;
import static com.mybatisflex.core.query.QueryMethods.select;

/**
 * 菜单 业务层处理
 *
 * @author lejav
 */
@Service
public class SysMenuServiceImpl extends BaseServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Resource
    private SysMenuMapper menuMapper;

    @Resource
    private ISysRoleService sysRoleService;

    @Resource
    private ISysRoleMenuService roleMenuService;
    @Resource
    private SysTenantPackageMapper tenantPackageMapper;

    @Override
    public QueryWrapper query() {
        return super.query().from(SYS_MENU);
    }

    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenuVo> selectMenuList(Long userId) {
        return selectMenuList(new SysMenuBo(), userId);
    }

    private QueryWrapper buildQueryWrapper(SysMenuBo menuBo) {
        return super.buildBaseQueryWrapper()
            .and(SYS_MENU.MENU_NAME.like(menuBo.getMenuName()))
            .and(SYS_MENU.VISIBLE.eq(menuBo.getVisible()))
            .and(SYS_MENU.STATUS.eq(menuBo.getStatus()))
            .orderBy(SYS_MENU.PARENT_ID.asc(), SYS_MENU.ORDER_NUM.asc());
    }

    /**
     * 查询系统菜单列表
     *
     * @param menuBo 菜单信息
     * @return 菜单列表
     */
    @Override
    public List<SysMenuVo> selectMenuList(SysMenuBo menuBo, Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        // 管理员显示所有菜单信息
        if (LoginHelper.isSuperAdmin(userId)) {
            queryWrapper = buildQueryWrapper(menuBo);
        } else {
            queryWrapper = queryWrapper
                .select(QueryMethods.distinct(SYS_MENU.MENU_ID, SYS_MENU.PARENT_ID, SYS_MENU.MENU_NAME, SYS_MENU.PATH, SYS_MENU.COMPONENT, SYS_MENU.QUERY_PARAM, SYS_MENU.VISIBLE, SYS_MENU.STATUS, SYS_MENU.PERMS, SYS_MENU.IS_FRAME, SYS_MENU.IS_CACHE, SYS_MENU.MENU_TYPE, SYS_MENU.ICON, SYS_MENU.ORDER_NUM, SYS_MENU.CREATE_TIME))
                .from(SYS_MENU)
                .leftJoin(SYS_ROLE_MENU).on(SYS_MENU.MENU_ID.eq(SYS_ROLE_MENU.MENU_ID))
                .leftJoin(SYS_USER_ROLE).on(SYS_ROLE_MENU.ROLE_ID.eq(SYS_USER_ROLE.ROLE_ID))
                .leftJoin(SYS_ROLE).on(SYS_ROLE.ROLE_ID.eq(SYS_USER_ROLE.ROLE_ID))
                .where(SYS_USER_ROLE.USER_ID.eq(userId));
            if (StringUtils.isNotEmpty(menuBo.getMenuName())) {
                queryWrapper.and(SYS_MENU.MENU_NAME.like(menuBo.getMenuName()));
            }
            if (StringUtils.isNotEmpty(menuBo.getVisible())) {
                queryWrapper.and(SYS_MENU.VISIBLE.eq(menuBo.getVisible()));
            }
            if (StringUtils.isNotEmpty(menuBo.getStatus())) {
                queryWrapper.and(SYS_MENU.STATUS.eq(menuBo.getStatus()));
            }
            queryWrapper.orderBy(SYS_MENU.PARENT_ID.asc(), SYS_MENU.ORDER_NUM.asc());
        }
        List<SysMenuVo> menuList = this.listAs(queryWrapper, SysMenuVo.class);
        return menuList;
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByUserId(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(distinct(SYS_MENU.PERMS))
            .from(SYS_MENU)
            .leftJoin(SYS_ROLE_MENU).on(SYS_ROLE_MENU.MENU_ID.eq(SYS_MENU.MENU_ID))
            .leftJoin(SYS_USER_ROLE).on(SYS_USER_ROLE.ROLE_ID.eq(SYS_ROLE_MENU.ROLE_ID))
            .leftJoin(SYS_ROLE).on(SYS_ROLE.ROLE_ID.eq(SYS_USER_ROLE.ROLE_ID))
            .where(SYS_MENU.STATUS.eq("0"))
            .and(SYS_ROLE.STATUS.eq("0"))
            .and(SYS_USER_ROLE.USER_ID.eq(userId));

        List<String> perms = this.listAs(queryWrapper, String.class);

        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据角色ID查询权限
     *
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByRoleId(Long roleId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(distinct(SYS_MENU.PERMS))
            .from(SYS_MENU)
            .leftJoin(SYS_ROLE_MENU).on(SYS_ROLE_MENU.MENU_ID.eq(SYS_MENU.MENU_ID))
            .where(SYS_MENU.STATUS.eq("0"))
            .and(SYS_ROLE_MENU.ROLE_ID.eq(roleId));

        List<String> perms = this.listAs(queryWrapper, String.class);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms) {
            if (StringUtils.isNotEmpty(perm)) {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    private QueryWrapper buildMenuTreeQueryWrapper() {
        QueryWrapper queryWrapper = super.buildBaseQueryWrapper()
            .and(SYS_MENU.MENU_TYPE.in(UserConstants.TYPE_DIR, UserConstants.TYPE_MENU))
            .and(SYS_MENU.STATUS.eq(UserConstants.MENU_NORMAL))
            .orderBy(SYS_MENU.PARENT_ID.asc(), SYS_MENU.ORDER_NUM.asc());
        return queryWrapper;
    }

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户名称
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (LoginHelper.isSuperAdmin(userId)) {
            queryWrapper = buildMenuTreeQueryWrapper();
        } else {
            queryWrapper = queryWrapper
                .select(QueryMethods.distinct(SYS_MENU.MENU_ID, SYS_MENU.PARENT_ID, SYS_MENU.MENU_NAME, SYS_MENU.PATH, SYS_MENU.COMPONENT, SYS_MENU.QUERY_PARAM, SYS_MENU.VISIBLE, SYS_MENU.STATUS, SYS_MENU.PERMS, SYS_MENU.IS_FRAME, SYS_MENU.IS_CACHE, SYS_MENU.MENU_TYPE, SYS_MENU.ICON, SYS_MENU.ORDER_NUM, SYS_MENU.CREATE_TIME))
                .from(SYS_MENU)
                .leftJoin(SYS_ROLE_MENU).on(SYS_MENU.MENU_ID.eq(SYS_ROLE_MENU.MENU_ID))
                .leftJoin(SYS_USER_ROLE).on(SYS_ROLE_MENU.ROLE_ID.eq(SYS_USER_ROLE.ROLE_ID))
                .leftJoin(SYS_ROLE).on(SYS_ROLE.ROLE_ID.eq(SYS_USER_ROLE.ROLE_ID))
                .leftJoin(SYS_USER).on(SYS_USER.USER_ID.eq(SYS_USER_ROLE.USER_ID))
                .where(SYS_USER.USER_ID.eq(userId))
                .and(SYS_MENU.MENU_TYPE.in("M", "C"))
                .and(SYS_MENU.STATUS.eq("0"))
                .and(SYS_ROLE.STATUS.eq("0"))
                .orderBy(SYS_MENU.PARENT_ID.asc(), SYS_MENU.ORDER_NUM.asc());
        }
        List<SysMenu> menus = this.list(queryWrapper);
        return getChildPerms(menus);
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuListByRoleId(Long roleId) {
        SysRoleVo role = sysRoleService.selectRoleById(roleId);
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(SYS_MENU.MENU_ID)
            .from(SYS_MENU)
            .leftJoin(SYS_ROLE_MENU).on(SYS_ROLE_MENU.MENU_ID.eq(SYS_MENU.MENU_ID))
            .where(SYS_ROLE_MENU.ROLE_ID.eq(roleId));
        if (role.getMenuCheckStrictly()) {
            queryWrapper.and(SYS_MENU.MENU_ID.notIn(select(SYS_MENU.PARENT_ID).from(SYS_MENU).innerJoin(SYS_ROLE_MENU).on(SYS_MENU.MENU_ID.eq(SYS_ROLE_MENU.MENU_ID)).and(SYS_ROLE_MENU.ROLE_ID.eq(roleId))));
        }
        queryWrapper.orderBy(SYS_MENU.PARENT_ID.asc(), SYS_MENU.ORDER_NUM.asc());
        return this.listAs(queryWrapper, Long.class);
    }

    /**
     * 根据租户套餐ID查询菜单树信息
     *
     * @param packageId 租户套餐ID
     * @return 选中菜单列表
     */
    @Override
    public List<Long> selectMenuListByPackageId(Long packageId) {
        SysTenantPackage tenantPackage = tenantPackageMapper.selectOneById(packageId);
        List<Long> menuIds = StringUtils.splitTo(tenantPackage.getMenuIds(), Convert::toLong);
        if (CollUtil.isEmpty(menuIds)) {
            return ListUtil.of();
        }
        List<Long> parentIds = null;
        if (tenantPackage.getMenuCheckStrictly()) {
            parentIds = menuMapper.selectObjectListByQueryAs(QueryWrapper.create().select(SYS_MENU.PARENT_ID).from(SYS_MENU).where(SYS_MENU.MENU_ID.in(menuIds)), Long.class);
        }
        return menuMapper.selectObjectListByQueryAs(QueryWrapper.create().select(SYS_MENU.MENU_ID).from(SYS_MENU).where(SYS_MENU.MENU_ID.in(menuIds)).and(SYS_MENU.MENU_ID.notIn(parentIds)), Long.class);
    }

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus) {
        List<RouterVo> routers = new LinkedList<>();
        for (SysMenu menu : menus) {
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setQuery(menu.getQueryParam());
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
            List<SysMenu> cMenus = menu.getChildren();
            if (StringUtils.isNotEmpty(cMenus) && UserConstants.TYPE_DIR.equals(menu.getMenuType())) {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            } else if (isMenuFrame(menu)) {
                router.setMeta(null);
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), StringUtils.equals("1", menu.getIsCache()), menu.getPath()));
                children.setQuery(menu.getQueryParam());
                childrenList.add(children);
                router.setChildren(childrenList);
            } else if (menu.getParentId().intValue() == 0 && isInnerLink(menu)) {
                router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                router.setPath("/");
                List<RouterVo> childrenList = new ArrayList<>();
                RouterVo children = new RouterVo();
                String routerPath = innerLinkReplaceEach(menu.getPath());
                children.setPath(routerPath);
                children.setComponent(UserConstants.INNER_LINK);
                children.setName(StringUtils.capitalize(routerPath));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon(), menu.getPath()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<Tree<Long>> buildMenuTreeSelect(List<SysMenuVo> menus) {
        if (CollUtil.isEmpty(menus)) {
            return CollUtil.newArrayList();
        }
        return TreeBuildUtils.build(menus, (menu, tree) ->
            tree.setId(menu.getMenuId())
                .setParentId(menu.getParentId())
                .setName(menu.getMenuName())
                .setWeight(menu.getOrderNum()));
    }

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public SysMenuVo selectMenuById(Long menuId) {
        return this.getOneAs(query().where(SYS_MENU.MENU_ID.eq(menuId)), SysMenuVo.class);
    }

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean hasChildByMenuId(Long menuId) {
        return this.exists(query().where(SYS_MENU.PARENT_ID.eq(menuId)));
    }

    /**
     * 查询菜单使用数量
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean checkMenuExistRole(Long menuId) {
        int result = roleMenuService.checkMenuExistRole(menuId);
        return result > 0;
    }

    /**
     * 新增保存菜单信息
     *
     * @param menuBo 菜单信息
     * @return 结果:受影响的行数
     */
    @Override
    public int insertMenu(SysMenuBo menuBo) {
        SysMenu menu = MapstructUtils.convert(menuBo, SysMenu.class);

        Long loginUserId = LoginHelper.getUserId();
        Date createTime = new Date();
        menu.setCreateBy(loginUserId);
        menu.setCreateTime(createTime);
        menu.setUpdateBy(loginUserId);
        menu.setUpdateTime(createTime);

        return menuMapper.insert(menu, false);
    }

    /**
     * 修改保存菜单信息
     *
     * @param menuBo 菜单信息
     * @return 结果:true 更新成功，false 更新失败
     */
    @Override
    public boolean updateMenu(SysMenuBo menuBo) {
        SysMenu menu = MapstructUtils.convert(menuBo, SysMenu.class);

        Long loginUserId = LoginHelper.getUserId();
        Date createTime = new Date();
        menu.setUpdateBy(loginUserId);
        menu.setUpdateTime(createTime);

        return this.updateById(menu);
    }

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果:true 删除成功，false 删除失败。
     */
    @Override
    public boolean deleteMenuById(Long menuId) {
        return this.removeById(menuId);
    }

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public boolean checkMenuNameUnique(SysMenuBo menu) {
        Long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        QueryWrapper queryWrapper = query().where(SYS_MENU.MENU_NAME.eq(menu.getMenuName()))
            .and(SYS_MENU.PARENT_ID.eq(menu.getParentId()));
        SysMenu info = this.getOne(queryWrapper);
        if (StringUtils.isNotNull(info) && info.getMenuId().longValue() != menuId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(SysMenu menu) {
        String routerName = StringUtils.capitalize(menu.getPath());
        // 非外链并且是一级目录（类型为目录）
        if (isMenuFrame(menu)) {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu) {
        String routerPath = menu.getPath();
        // 内链打开外网方式
        if (menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            routerPath = innerLinkReplaceEach(routerPath);
        }
        // 非外链并且是一级目录（类型为目录）
        if (0 == menu.getParentId().intValue() && UserConstants.TYPE_DIR.equals(menu.getMenuType())
            && UserConstants.NO_FRAME.equals(menu.getIsFrame())) {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMenuFrame(menu)) {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(SysMenu menu) {
        String component = UserConstants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMenuFrame(menu)) {
            component = menu.getComponent();
        } else if (StringUtils.isEmpty(menu.getComponent()) && menu.getParentId().intValue() != 0 && isInnerLink(menu)) {
            component = UserConstants.INNER_LINK;
        } else if (StringUtils.isEmpty(menu.getComponent()) && isParentView(menu)) {
            component = UserConstants.PARENT_VIEW;
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isMenuFrame(SysMenu menu) {
        return menu.getParentId().intValue() == 0 && UserConstants.TYPE_MENU.equals(menu.getMenuType())
            && menu.getIsFrame().equals(UserConstants.NO_FRAME);
    }

    /**
     * 是否为内链组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isInnerLink(SysMenu menu) {
        return menu.getIsFrame().equals(UserConstants.NO_FRAME) && StringUtils.ishttp(menu.getPath());
    }

    /**
     * 是否为parent_view组件
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isParentView(SysMenu menu) {
        return menu.getParentId().intValue() != 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType());
    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list     分类表
     * @return String
     */
    private List<SysMenu> getChildPerms(List<SysMenu> list) {
        List<SysMenu> returnList = new ArrayList<>();
        for (SysMenu t : list) {
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == 0) {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表
        List<SysMenu> childList = StreamUtils.filter(list, n -> n.getParentId().equals(t.getMenuId()));
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            // 判断是否有子节点
            if (list.stream().anyMatch(n -> n.getParentId().equals(tChild.getMenuId()))) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 内链域名特殊字符替换
     *
     * @return 替换后的内链域名
     */
    public String innerLinkReplaceEach(String path) {
        return StringUtils.replaceEach(path, new String[]{Constants.HTTP, Constants.HTTPS, Constants.WWW, ".", ":"},
            new String[]{"", "", "", "/", "/"});
    }
}
