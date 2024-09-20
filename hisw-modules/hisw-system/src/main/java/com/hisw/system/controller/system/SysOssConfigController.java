package com.hisw.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.ListUtil;
import com.hisw.common.core.core.domain.R;
import com.hisw.common.log.annotation.Log;
import com.hisw.common.log.enums.BusinessType;
import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.web.annotation.RepeatSubmit;
import com.hisw.common.web.core.BaseController;
import com.hisw.system.domain.bo.SysOssConfigBo;
import com.hisw.system.domain.vo.SysOssConfigVo;
import com.hisw.system.service.ISysOssConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * 对象存储配置
 *
 * @author lejav
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/resource/oss/config")
public class SysOssConfigController extends BaseController {

    @Resource
    private final ISysOssConfigService ossConfigService;

    /**
     * 查询对象存储配置列表
     */
    @SaCheckPermission("system:ossConfig:list")
    @GetMapping("/list")
    public TableDataInfo<SysOssConfigVo> list(SysOssConfigBo bo) {
        return ossConfigService.queryPageList(bo);
    }

    /**
     * 获取对象存储配置详细信息
     *
     * @param ossConfigId OSS配置ID
     */
    @SaCheckPermission("system:ossConfig:list")
    @GetMapping("/{ossConfigId}")
    public R<SysOssConfigVo> getInfo(@NotNull(message = "主键不能为空") @PathVariable Long ossConfigId) {
        return R.ok(ossConfigService.queryById(ossConfigId));
    }

    /**
     * 新增对象存储配置
     */
    @SaCheckPermission("system:ossConfig:add")
    @Log(title = "对象存储配置", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysOssConfigBo bo) {
        boolean inserted = ossConfigService.insertByBo(bo);
        if (!inserted) {
            return R.fail("新增对象存储配置记录失败！");
        }
        return R.ok();
    }

    /**
     * 修改对象存储配置
     */
    @SaCheckPermission("system:ossConfig:edit")
    @Log(title = "对象存储配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysOssConfigBo bo) {
        boolean updated = ossConfigService.updateByBo(bo);
        if (!updated) {
            return R.fail("修改对象存储配置记录失败!");
        }
        return R.ok();
    }

    /**
     * 删除对象存储配置
     *
     * @param ossConfigIds OSS配置ID串
     */
    @SaCheckPermission("system:ossConfig:remove")
    @Log(title = "对象存储配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ossConfigIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ossConfigIds) {
        boolean deleted = ossConfigService.deleteWithValidByIds(ListUtil.of(ossConfigIds), true);
        if (!deleted) {
            return R.fail("删除对象存储配置记录失败!");
        }
        return R.ok();
    }

    /**
     * 状态修改
     */
    @SaCheckPermission("system:ossConfig:edit")
    @Log(title = "对象存储状态修改", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R<Void> changeStatus(@RequestBody SysOssConfigBo bo) {
        boolean updated = ossConfigService.updateOssConfigStatus(bo);
        if (!updated) {
            return R.fail("状态修改失败!");
        }
        return R.ok();
    }
}
