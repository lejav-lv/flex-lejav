package com.hisw.system.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.hisw.common.core.constant.CacheNames;
import com.hisw.common.core.constant.Constants;
import com.hisw.common.core.constant.TenantConstants;
import com.hisw.common.core.exception.ServiceException;
import com.hisw.common.core.utils.MapstructUtils;
import com.hisw.common.core.utils.SpringUtils;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.orm.core.page.PageQuery;
import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.orm.core.service.impl.BaseServiceImpl;
import com.hisw.common.security.utils.LoginHelper;
import com.hisw.common.tenant.helper.TenantHelper;
import com.hisw.system.domain.*;
import com.hisw.system.domain.bo.SysTenantBo;
import com.hisw.system.domain.vo.SysTenantPackageVo;
import com.hisw.system.domain.vo.SysTenantVo;
import com.hisw.system.mapper.*;
import com.hisw.system.service.ISysRoleService;
import com.hisw.system.service.ISysTenantPackageService;
import com.hisw.system.service.ISysTenantService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.hisw.system.domain.table.SysConfigTableDef.SYS_CONFIG;
import static com.hisw.system.domain.table.SysDictDataTableDef.SYS_DICT_DATA;
import static com.hisw.system.domain.table.SysDictTypeTableDef.SYS_DICT_TYPE;
import static com.hisw.system.domain.table.SysOssConfigTableDef.SYS_OSS_CONFIG;
import static com.hisw.system.domain.table.SysRoleMenuTableDef.SYS_ROLE_MENU;
import static com.hisw.system.domain.table.SysRoleTableDef.SYS_ROLE;
import static com.hisw.system.domain.table.SysTenantTableDef.SYS_TENANT;
import static com.hisw.system.domain.table.SysUserTableDef.SYS_USER;

/**
 * 租户Service业务层处理
 *
 * @author lejav
 */
@RequiredArgsConstructor
@Service
public class SysTenantServiceImpl extends BaseServiceImpl<SysTenantMapper, SysTenant> implements ISysTenantService {

    @Resource
    private SysTenantMapper tenantMapper;
    @Resource
    private SysTenantPackageMapper tenantPackageMapper;
    @Resource
    private ISysTenantPackageService tenantPackageService;
    @Resource
    private SysUserMapper userMapper;
    @Resource
    private SysDeptMapper deptMapper;
    @Resource
    private SysRoleMapper roleMapper;
    @Resource
    private ISysRoleService roleService;
    @Resource
    private SysRoleMenuMapper roleMenuMapper;
    @Resource
    private SysRoleDeptMapper roleDeptMapper;
    @Resource
    private SysUserRoleMapper userRoleMapper;
    @Resource
    private SysDictTypeMapper dictTypeMapper;
    @Resource
    private SysDictDataMapper dictDataMapper;
    @Resource
    private SysConfigMapper configMapper;
    @Resource
    private SysOssConfigMapper ossConfigMapper;

    public QueryWrapper query() {
        return super.query().from(SYS_TENANT);
    }

    private QueryWrapper buildQueryWrapper(SysTenantBo sysTenantBo) {
        return super.buildBaseQueryWrapper()
            .and(SYS_TENANT.CONTACT_USER_NAME.like(sysTenantBo.getContactUserName()))
            .and(SYS_TENANT.CONTACT_PHONE.eq(sysTenantBo.getContactPhone()))
            .and(SYS_TENANT.COMPANY_NAME.like(sysTenantBo.getCompanyName()))
            .and(SYS_TENANT.LICENSE_NUMBER.eq(sysTenantBo.getLicenseNumber()))
            .and(SYS_TENANT.ADDRESS.eq(sysTenantBo.getAddress()))
            .and(SYS_TENANT.INTRO.eq(sysTenantBo.getIntro()))
            .and(SYS_TENANT.DOMAIN.like(sysTenantBo.getDomain()))
            .and(SYS_TENANT.PACKAGE_ID.eq(sysTenantBo.getPackageId()))
            .and(SYS_TENANT.EXPIRE_TIME.eq(sysTenantBo.getExpireTime()))
            .and(SYS_TENANT.ACCOUNT_COUNT.eq(sysTenantBo.getAccountCount()))
            .and(SYS_TENANT.STATUS.eq(sysTenantBo.getStatus()))
            .orderBy(SYS_TENANT.TENANT_ID.desc());
    }

    /**
     * 分页查询租户列表
     *
     * @param sysTenantBo 租户Bo
     * @return 租户列表集合
     */
    public TableDataInfo<SysTenantVo> selectPage(SysTenantBo sysTenantBo) {
        return TenantHelper.ignore(() -> {
            QueryWrapper queryWrapper = buildQueryWrapper(sysTenantBo);
            Page<SysTenantVo> page = this.pageAs(PageQuery.build(), queryWrapper, SysTenantVo.class);
            return TableDataInfo.build(page);
        });
    }

    /**
     * 查询租户列表
     */
    @Override
    public List<SysTenantVo> selectList(SysTenantBo sysTenantBo) {
        return TenantHelper.ignore(() -> {
            QueryWrapper queryWrapper = buildQueryWrapper(sysTenantBo);
            return this.listAs(queryWrapper, SysTenantVo.class);
        });
    }

    /**
     * 基于租户ID查询租户
     */
    @Cacheable(cacheNames = CacheNames.SYS_TENANT, key = "#tenantId")
    @Override
    public SysTenantVo selectById(Long tenantId) {
        return TenantHelper.ignore(() -> this.getOneAs(query().where(SYS_TENANT.TENANT_ID.eq(tenantId)), SysTenantVo.class));
    }

    /**
     * 新增租户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean insert(SysTenantBo sysTenantBo) {
        return TenantHelper.ignore(() -> {
            SysTenant sysTenant = MapstructUtils.convert(sysTenantBo, SysTenant.class);
            if (ObjectUtil.isNull(sysTenant)) {
                throw new ServiceException("新增租户参数为空");
            }
            Long loginUserId = LoginHelper.getUserId();
            Date createTime = new Date();
            sysTenant.setCreateBy(loginUserId);
            sysTenant.setCreateTime(createTime);
            sysTenant.setUpdateBy(loginUserId);
            sysTenant.setUpdateTime(createTime);
            boolean inserted = this.save(sysTenant);
            if (!inserted) {
                throw new ServiceException("新增租户失败");
            }
            Long tenantId = sysTenant.getTenantId();


            // 根据套餐创建角色
            Long roleId = createTenantRole(tenantId, sysTenantBo.getPackageId());

            // 创建部门: 公司名是部门名称
            SysDept dept = new SysDept();
            dept.setTenantId(tenantId);
            dept.setDeptName(sysTenantBo.getCompanyName());
            dept.setParentId(Constants.TOP_PARENT_ID);
            dept.setAncestors(Constants.TOP_PARENT_ID.toString());
            dept.setDelFlag(0);//0 代表存在
            int insertedDeptRows = deptMapper.insert(dept, true);
            if (insertedDeptRows != 1) {
                throw new ServiceException("新增租户的部门记录失败");
            }
            Long deptId = dept.getDeptId();

            // 角色和部门关联表
            SysRoleDept roleDept = new SysRoleDept();
            roleDept.setRoleId(roleId);
            roleDept.setDeptId(deptId);
            int insertedRoleDept = roleDeptMapper.insertWithPk(roleDept);
            if (insertedRoleDept != 1) {
                throw new ServiceException("新增租户的角色部门记录失败");
            }

            // 新增系统用户
            SysUser user = new SysUser();
            user.setTenantId(tenantId);
            user.setUserName(sysTenantBo.getUsername());
            user.setNickName(sysTenantBo.getUsername());
            user.setPassword(BCrypt.hashpw(sysTenantBo.getPassword()));
            user.setDeptId(deptId);
            user.setDelFlag(0);
            user.setStatus("0");
            user.setUserType("sys_user");//sys_user是系统用户
            int insertedUser = userMapper.insert(user, true);
            if (insertedUser != 1) {
                throw new ServiceException("新增租户的用户记录失败");
            }

            // 用户和角色关联表
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getUserId());
            userRole.setRoleId(roleId);
            int insertedUserRole = userRoleMapper.insertWithPk(userRole);
            if (insertedUserRole != 1) {
                throw new ServiceException("新增租户的用户角色记录失败");
            }

            // 增加字典记录
            Long defaultTenantId = TenantConstants.DEFAULT_TENANT_ID;
            List<SysDictType> dictTypeList = dictTypeMapper.selectListByQuery(QueryWrapper.create().from(SYS_DICT_TYPE)
                .where(SYS_DICT_TYPE.TENANT_ID.eq(defaultTenantId)));
            List<SysDictData> dictDataList = dictDataMapper.selectListByQuery(
                QueryWrapper.create().from(SYS_DICT_DATA)
                    .where(SYS_DICT_DATA.TENANT_ID.eq(defaultTenantId)));
            for (SysDictType dictType : dictTypeList) {
                dictType.setDictId(null);
                dictType.setTenantId(tenantId);
            }
            for (SysDictData dictData : dictDataList) {
                dictData.setDictCode(null);
                dictData.setTenantId(tenantId);
            }
            dictTypeMapper.insertBatch(dictTypeList);
            dictDataMapper.insertBatch(dictDataList);

            // 增加参数配置记录
            List<SysConfig> sysConfigList = configMapper.selectListByQuery(
                QueryWrapper.create().from(SYS_CONFIG).where(SYS_CONFIG.TENANT_ID.eq(defaultTenantId)));
            for (SysConfig config : sysConfigList) {
                config.setConfigId(null);
                config.setTenantId(tenantId);
            }
            configMapper.insertBatch(sysConfigList);

            //增加SysOssConfig记录
            List<SysOssConfig> sysOssConfigList = ossConfigMapper.selectListByQuery(
                QueryWrapper.create().from(SYS_OSS_CONFIG).where(SYS_OSS_CONFIG.TENANT_ID.eq(defaultTenantId)));
            for (SysOssConfig ossConfig : sysOssConfigList) {
                ossConfig.setOssConfigId(null);
                ossConfig.setTenantId(tenantId);
            }
            ossConfigMapper.insertBatch(sysOssConfigList);

            return true;
        });

    }

    /**
     * 根据租户菜单创建租户角色
     *
     * @param tenantId  租户编号
     * @param packageId 租户套餐id
     * @return 角色id
     */
    private Long createTenantRole(Long tenantId, Long packageId) {
        // 获取租户套餐
        SysTenantPackageVo tenantPackageVo = tenantPackageService.selectById(packageId);
        if (ObjectUtil.isNull(tenantPackageVo)) {
            throw new ServiceException("套餐不存在");
        }
        // 获取套餐菜单id
        List<Long> menuIds = StringUtils.splitTo(tenantPackageVo.getMenuIds(), Convert::toLong);

        // 创建角色
        SysRole role = new SysRole();
        role.setTenantId(tenantId);
        role.setRoleName(TenantConstants.TENANT_ADMIN_ROLE_NAME);
        role.setRoleKey(TenantConstants.TENANT_ADMIN_ROLE_KEY);
        role.setRoleSort(1);
        role.setStatus(TenantConstants.NORMAL);
        role.setDelFlag(0);
        role.setDataScope("1");//默认1：全部数据权限
        int insertedRows = roleMapper.insert(role, true);
        if (insertedRows != 1) {
            throw new ServiceException("添加租户管理员角色记录失败！");
        }
        Long roleId = role.getRoleId();
        role.setMenuIds(menuIds.toArray(new Long[0]));
        //新增角色菜单记录
        boolean insertedRoleMenu = roleService.insertRoleMenu(role);
        if (!insertedRoleMenu) {
            throw new ServiceException("添加租户管理员的角色菜单记录失败！");
        }

        return roleId;
    }

    /**
     * 修改租户
     */
    @CacheEvict(cacheNames = CacheNames.SYS_TENANT, key = "#sysTenantBo.tenantId")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(SysTenantBo sysTenantBo) {
        SysTenant tenant = MapstructUtils.convert(sysTenantBo, SysTenant.class);
        if (ObjectUtil.isNull(tenant)) {
            throw new ServiceException("租户的参数为空！");
        }

        //如果更换了套餐，则需要同步套餐sys_role_menu表
        SysTenantVo oldTenantVo = selectById(tenant.getTenantId());
        if (!oldTenantVo.getPackageId().equals(tenant.getPackageId())) {
            boolean synced = syncTenantPackage(tenant.getTenantId(), tenant.getPackageId());
            if (!synced) {
                throw new ServiceException("同步套餐失败！");
            }
        }

        return TenantHelper.ignore(() -> {
            Long loginUserId = LoginHelper.getUserId();
            Date createTime = new Date();
            tenant.setUpdateBy(loginUserId);
            tenant.setUpdateTime(createTime);

            return tenantMapper.update(tenant) > 0;
        });
    }

    /**
     * 修改租户状态
     *
     * @param sysTenantBo 租户信息
     * @return 结果
     */
    @CacheEvict(cacheNames = CacheNames.SYS_TENANT, key = "#sysTenantBo.tenantId")
    @Override
    public boolean updateTenantStatus(SysTenantBo sysTenantBo) {
        return TenantHelper.ignore(() -> {
            SysTenant sysTenant = MapstructUtils.convert(sysTenantBo, SysTenant.class);
            Long loginUserId = LoginHelper.getUserId();
            Date createTime = new Date();
            sysTenant.setUpdateBy(loginUserId);
            sysTenant.setUpdateTime(createTime);

            return this.updateById(sysTenant);
        });
    }

    /**
     * 校验租户是否允许操作
     *
     * @param tenantId 租户ID
     */
    @Override
    public void checkTenantAllowed(Long tenantId) {
        if (ObjectUtil.isNotNull(tenantId) && TenantConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            throw new ServiceException("不允许操作超级管理员租户");
        }
    }

    /**
     * 批量删除租户
     */
    @CacheEvict(cacheNames = CacheNames.SYS_TENANT, allEntries = true)
    @Override
    public boolean deleteByIds(Collection<Long> tenantIds, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
            if (tenantIds.contains(TenantConstants.SUPER_ADMIN_ID)) {
                throw new ServiceException("超级管理员租户不能删除");
            }
        }
        return TenantHelper.ignore(() -> this.removeByIds(tenantIds));//逻辑删除
    }

    /**
     * 校验企业名称是否唯一
     */
    @Override
    public boolean checkCompanyNameUnique(SysTenantBo sysTenantBo) {
        return TenantHelper.ignore(() -> tenantMapper.selectCountByQuery(
            query().where(SYS_TENANT.COMPANY_NAME.eq(sysTenantBo.getCompanyName())))) == 0;
    }

    /**
     * 校验账号余额
     */
    @Override
    public boolean checkAccountBalance(Long tenantId) {
        SysTenantVo tenant = SpringUtils.getAopProxy(this).selectById(tenantId);
        // 如果余额为-1则代表不限制
        if (tenant.getAccountCount() == -1) {
            return true;
        }
        Long userNumber = TenantHelper.ignore(() -> userMapper.selectCountByQuery(QueryWrapper.create().from(SYS_USER).where(SYS_USER.TENANT_ID.eq(tenantId))));
        // 如果余额大于0则代表还有可用名额
        return tenant.getAccountCount() - userNumber > 0;
    }

    /**
     * 同步租户套餐
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean syncTenantPackage(Long tenantId, Long packageId) {
        return TenantHelper.ignore(() -> {
                SysTenantPackage tenantPackage = tenantPackageMapper.selectOneById(packageId);
                List<SysRole> roles = roleMapper.selectListByQuery(
                    QueryWrapper.create().from(SYS_ROLE).where(SYS_ROLE.TENANT_ID.eq(tenantId)));
                List<Long> roleIds = new ArrayList<>(roles.size() - 1);
                List<Long> menuIds = StringUtils.splitTo(tenantPackage.getMenuIds(), Convert::toLong);
                roles.forEach(role -> {
                    if (TenantConstants.TENANT_ADMIN_ROLE_KEY.equals(role.getRoleKey())) {
                        int deletedRoleMenu = roleMenuMapper.deleteByQuery(QueryWrapper.create().from(SYS_ROLE_MENU).where(SYS_ROLE_MENU.ROLE_ID.eq(role.getRoleId())));
                        if (deletedRoleMenu == 0) {
                            throw new ServiceException("删除租户的角色菜单记录失败！");
                        }

                        role.setMenuIds(menuIds.toArray(new Long[0]));
                        boolean insertedRoleMenu = roleService.insertRoleMenu(role);
                        if (!insertedRoleMenu) {
                            throw new ServiceException("添加租户管理员的角色菜单记录失败！");
                        }
                    } else {
                        roleIds.add(role.getRoleId());
                    }
                });
                if (!roleIds.isEmpty()) {
                    roleMenuMapper.deleteByQuery(
                        QueryWrapper.create()
                            .from(SYS_ROLE_MENU)
                            .where(SYS_ROLE_MENU.ROLE_ID.in(roleIds))
                            .and(SYS_ROLE_MENU.MENU_ID.notIn(menuIds))
                    );
                }
                return true;
            }
        );
    }
}
