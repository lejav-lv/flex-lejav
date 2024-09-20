package com.hisw.system.service;

import com.hisw.common.orm.core.service.IBaseService;
import com.hisw.system.domain.SysTenantPackage;
import com.hisw.system.domain.vo.SysTenantPackageVo;
import com.hisw.system.domain.bo.SysTenantPackageBo;
import com.hisw.common.orm.core.page.TableDataInfo;

import java.util.List;

/**
 * 租户套餐Service接口
 *
 * @author lejav
 */
public interface ISysTenantPackageService extends IBaseService<SysTenantPackage> {

    /**
     * 查询租户套餐
     */
    SysTenantPackageVo selectById(Long packageId);

    /**
     * 分页查询租户套餐
     *
     * @param sysTenantPackageBo 租户套餐Bo
     * @return 租户套餐集合
     */
    TableDataInfo<SysTenantPackageVo> selectPage(SysTenantPackageBo sysTenantPackageBo);

    /**
     * 查询租户套餐下拉选列表
     */
    List<SysTenantPackageVo> selectList();

    /**
     * 查询租户套餐列表
     */
    List<SysTenantPackageVo> queryList(SysTenantPackageBo bo);

    /**
     * 新增租户套餐
     */
    boolean insert(SysTenantPackageBo sysTenantPackageBo);

    /**
     * 修改租户套餐
     */
    boolean update(SysTenantPackageBo sysTenantPackageBo);

    /**
     * 修改套餐状态
     */
    boolean updatePackageStatus(SysTenantPackageBo sysTenantPackageBo);

    /**
     * 校验并批量删除租户套餐信息
     */
    boolean deleteByIds(Long[] packageIds, Boolean isValid);
}
