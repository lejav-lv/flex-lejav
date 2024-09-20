package com.hisw.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.hisw.common.core.core.domain.R;
import com.hisw.common.excel.utils.ExcelUtil;
import com.hisw.common.log.annotation.Log;
import com.hisw.common.log.enums.BusinessType;
import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.web.core.BaseController;
import com.hisw.system.domain.bo.SysDictTypeBo;
import com.hisw.system.domain.vo.SysDictTypeVo;
import com.hisw.system.service.ISysDictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 数据字典信息
 *
 * @author lejav
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/dict/type")
public class SysDictTypeController extends BaseController {

    @Resource
    private ISysDictTypeService dictTypeService;

    @SaCheckPermission("system:dict:list")
    @GetMapping("/list")
    public TableDataInfo<SysDictTypeVo> list(SysDictTypeBo dictTypeBo) {
        return dictTypeService.selectPage(dictTypeBo);
    }

    @Log(title = "字典类型", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:dict:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictTypeBo dictTypeBo) {
        List<SysDictTypeVo> list = dictTypeService.selectDictTypeList(dictTypeBo);
        ExcelUtil.exportExcel(list, "数据字典类型信息数据", SysDictTypeVo.class, response);
    }

    /**
     * 查询字典类型详细
     */
    @SaCheckPermission("system:dict:query")
    @GetMapping(value = "/{dictId}")
    public R<SysDictTypeVo> getInfo(@PathVariable Long dictId) {
        return R.ok(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 新增字典类型
     */
    @SaCheckPermission("system:dict:add")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysDictTypeBo dictTypeBo) {
        if (!dictTypeService.checkDictTypeUnique(dictTypeBo)) {
            return R.fail("新增字典'" + dictTypeBo.getDictName() + "'失败，字典类型已存在");
        }
        dictTypeService.insertDictType(dictTypeBo);
        return R.ok();
    }

    /**
     * 修改字典类型
     */
    @SaCheckPermission("system:dict:edit")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysDictTypeBo dictTypeBo) {
        if (!dictTypeService.checkDictTypeUnique(dictTypeBo)) {
            return R.fail("修改字典'" + dictTypeBo.getDictName() + "'失败，字典类型已存在");
        }
        dictTypeService.updateDictType(dictTypeBo);
        return R.ok();
    }

    /**
     * 删除字典类型
     */
    @SaCheckPermission("system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    public R<Void> remove(@PathVariable Long[] dictIds) {
        boolean deleted = dictTypeService.deleteDictTypeByIds(dictIds);
        if (!deleted) {
            return R.fail("删除字典类型记录失败!");
        }
        return R.ok();
    }

    /**
     * 刷新字典缓存
     */
    @SaCheckPermission("system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public R<Void> refreshCache() {
        dictTypeService.resetDictCache();
        return R.ok();
    }

    /**
     * 获取字典选择框列表
     */
    @GetMapping("/optionselect")
    public R<List<SysDictTypeVo>> optionselect() {
        List<SysDictTypeVo> dictTypes = dictTypeService.selectDictTypeAll();
        return R.ok(dictTypes);
    }
}
