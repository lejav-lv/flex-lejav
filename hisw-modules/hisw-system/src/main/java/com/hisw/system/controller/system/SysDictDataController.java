package com.hisw.system.controller.system;

import java.util.ArrayList;
import java.util.List;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.hisw.common.core.core.domain.R;
import com.hisw.common.excel.utils.ExcelUtil;
import com.hisw.common.log.annotation.Log;
import com.hisw.common.log.enums.BusinessType;
import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.system.domain.bo.SysDictDataBo;
import com.hisw.system.domain.vo.SysDictDataVo;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hisw.common.web.core.BaseController;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.system.service.ISysDictDataService;
import com.hisw.system.service.ISysDictTypeService;

/**
 * 数据字典信息
 *
 * @author lejav
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController extends BaseController {

    @Resource
    private ISysDictDataService dictDataService;

    @Resource
    private ISysDictTypeService dictTypeService;

    @SaCheckPermission("system:dict:list")
    @GetMapping("/list")
    public TableDataInfo<SysDictDataVo> list(SysDictDataBo dictDataBo) {
        return dictDataService.selectPage(dictDataBo);
    }

    /**
     * 导出字典数据列表
     */
    @SaCheckPermission("system:dict:export")
    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictDataBo dictDataBo) {
        List<SysDictDataVo> list = dictDataService.selectDictDataList(dictDataBo);
        ExcelUtil.exportExcel(list, "字典数据", SysDictDataVo.class, response);
    }

    /**
     * 查询字典数据详细
     */
    @SaCheckPermission("system:dict:query")
    @GetMapping(value = "/{dictCode}")
    public R<SysDictDataVo> getInfo(@PathVariable Long dictCode) {
        return R.ok(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @SaIgnore
    @GetMapping(value = "/type/{dictType}")
    public R<List<SysDictDataVo>> dictType(@PathVariable String dictType) {
        List<SysDictDataVo> data = dictTypeService.selectDictDataByType(dictType);
        if (StringUtils.isNull(data)) {
            data = new ArrayList<>();
        }
        return R.ok(data);
    }

    /**
     * 新增字典类型
     */
    @SaCheckPermission("system:dict:add")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysDictDataBo dictDataBo) {
        boolean inserted = dictDataService.insertDictData(dictDataBo);
        if (!inserted) {
            return R.fail("新增字典数据记录失败！");
        }
        return R.ok();
    }

    /**
     * 修改保存字典类型
     */
    @SaCheckPermission("system:dict:edit")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysDictDataBo dictDataBo) {
        boolean updated = dictDataService.updateDictData(dictDataBo);
        if (!updated) {
            return R.fail("修改字典数据记录失败！");
        }
        return R.ok();
    }

    /**
     * 删除字典类型
     */
    @SaCheckPermission("system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public R<Void> remove(@PathVariable Long[] dictCodes) {
        boolean deleted = dictDataService.deleteDictDataByIds(dictCodes);
        if (!deleted) {
            return R.fail("删除字典数据记录失败!");
        }
        return R.ok();
    }
}
