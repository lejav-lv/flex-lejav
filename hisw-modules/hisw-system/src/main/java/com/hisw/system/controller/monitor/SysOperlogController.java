package com.hisw.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.hisw.common.core.core.domain.R;
import com.hisw.common.excel.utils.ExcelUtil;
import com.hisw.common.log.annotation.Log;
import com.hisw.common.log.enums.BusinessType;
import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.web.core.BaseController;
import com.hisw.system.domain.bo.SysOperLogBo;
import com.hisw.system.domain.vo.SysOperLogVo;
import com.hisw.system.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 操作日志记录
 *
 * @author lejav
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/monitor/operlog")
public class SysOperlogController extends BaseController {

    @Resource
    private ISysOperLogService operLogService;

    /**
     * 获取操作日志记录列表
     */
    @SaCheckPermission("monitor:operlog:list")
    @GetMapping("/list")
    public TableDataInfo<SysOperLogVo> list(SysOperLogBo operLogBo) {
        return operLogService.selectPage(operLogBo);
    }

    /**
     * 导出操作日志记录列表
     */
    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @SaCheckPermission("monitor:operlog:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysOperLogBo operLog) {
        List<SysOperLogVo> list = operLogService.selectOperLogList(operLog);
        ExcelUtil.exportExcel(list, "操作日志", SysOperLogVo.class, response);
    }

    /**
     * 根据编号获取详细信息
     * @param operId 日志id
     */
    @SaCheckPermission("monitor:operlog:list")
    @GetMapping("/{operId}")
    public R<SysOperLogVo> getInfo(@PathVariable Long operId) {
        return R.ok(operLogService.selectOperLogById(operId));
    }

    /**
     * 批量删除操作日志记录
     * @param operIds 日志ids
     */
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @SaCheckPermission("monitor:operlog:remove")
    @DeleteMapping("/{operIds}")
    public R<Void> remove(@PathVariable Long[] operIds) {
        boolean deleted = operLogService.deleteOperLogByIds(operIds);
        if (!deleted) {
            R.fail("删除操作日志记录失败!");
        }
        return R.ok();
    }


    /**
     * 清理操作日志记录
     */
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @SaCheckPermission("monitor:operlog:remove")
    @DeleteMapping("/clean")
    public R<Void> clean() {
        boolean cleaned = operLogService.cleanOperLog();
        if (!cleaned) {
            R.fail("清除操作日志记录失败!");
        }
        return R.ok();
    }
}
