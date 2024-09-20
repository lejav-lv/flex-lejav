package com.hisw.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.hisw.common.core.constant.TenantConstants;
import com.hisw.common.core.core.domain.R;
import com.hisw.common.excel.utils.ExcelUtil;
import com.hisw.common.log.annotation.Log;
import com.hisw.common.log.enums.BusinessType;
import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.web.annotation.RepeatSubmit;
import com.hisw.common.web.core.BaseController;
import com.hisw.system.domain.bo.SysTenantPackageBo;
import com.hisw.system.domain.vo.SysTenantPackageVo;
import com.hisw.system.service.ISysTenantPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 租户套餐管理
 *
 * @author lejav
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/tenant/package")
public class SysTenantPackageController extends BaseController {

    @Resource
    private ISysTenantPackageService tenantPackageService;

    /**
     * 查询租户套餐列表
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:list")
    @GetMapping("/list")
    public TableDataInfo<SysTenantPackageVo> list(SysTenantPackageBo sysTenantPackageBo) {
        return tenantPackageService.selectPage(sysTenantPackageBo);
    }

    /**
     * 查询租户套餐下拉选列表
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:list")
    @GetMapping("/selectList")
    public R<List<SysTenantPackageVo>> selectList() {
        return R.ok(tenantPackageService.selectList());
    }

    /**
     * 导出租户套餐列表
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:export")
    @Log(title = "租户套餐", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysTenantPackageBo sysTenantPackageBo, HttpServletResponse response) {
        List<SysTenantPackageVo> list = tenantPackageService.queryList(sysTenantPackageBo);
        ExcelUtil.exportExcel(list, "租户套餐", SysTenantPackageVo.class, response);
    }

    /**
     * 获取租户套餐详细信息
     *
     * @param packageId 主键
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:query")
    @GetMapping("/{packageId}")
    public R<SysTenantPackageVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long packageId) {
        return R.ok(tenantPackageService.selectById(packageId));
    }

    /**
     * 新增租户套餐
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:add")
    @Log(title = "租户套餐", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysTenantPackageBo sysTenantPackageBo) {
        boolean inserted = tenantPackageService.insert(sysTenantPackageBo);
        if (!inserted) {
            return R.fail("新增租户套餐记录失败！");
        }
        return R.ok();
    }

    /**
     * 修改租户套餐
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:edit")
    @Log(title = "租户套餐", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysTenantPackageBo sysTenantPackageBo) {
        boolean updated = tenantPackageService.update(sysTenantPackageBo);
        if (!updated) {
            return R.fail("修改租户套餐记录失败!");
        }
        return R.ok();
    }

    /**
     * 状态修改
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:edit")
    @Log(title = "租户套餐", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysTenantPackageBo sysTenantPackageBo) {
        boolean updated = tenantPackageService.updatePackageStatus(sysTenantPackageBo);
        if (!updated) {
            return R.fail("修改租户套餐记录状态失败!");
        }
        return R.ok();
    }

    /**
     * 删除租户套餐
     *
     * @param packageIds 主键串
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:remove")
    @Log(title = "租户套餐", businessType = BusinessType.DELETE)
    @DeleteMapping("/{packageIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] packageIds) {
        boolean deleted = tenantPackageService.deleteByIds(packageIds, true);
        if (!deleted) {
            return R.fail("删除租户套餐记录失败!");
        }
        return R.ok();
    }
}
