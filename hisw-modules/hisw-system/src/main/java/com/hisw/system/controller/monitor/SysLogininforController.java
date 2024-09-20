package com.hisw.system.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.hisw.common.core.constant.GlobalConstants;
import com.hisw.common.core.core.domain.R;
import com.hisw.common.excel.utils.ExcelUtil;
import com.hisw.common.log.annotation.Log;
import com.hisw.common.log.enums.BusinessType;
import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.redis.utils.RedisUtils;
import com.hisw.common.web.core.BaseController;
import com.hisw.system.domain.bo.SysLogininforBo;
import com.hisw.system.domain.vo.SysLogininforVo;
import com.hisw.system.service.ISysLogininforService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 系统访问记录
 *
 * @author lejav
 */
@Validated
@RestController
@RequestMapping("/monitor/logininfor")
public class SysLogininforController extends BaseController {

    @Resource
    private ISysLogininforService logininforService;

    @SaCheckPermission("monitor:logininfor:list")
    @GetMapping("/list")
    public TableDataInfo<SysLogininforVo> list(SysLogininforBo logininforBo) {
        return logininforService.selectPage(logininforBo);
    }

    /**
     * 导出系统访问记录列表
     */
    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @SaCheckPermission("monitor:logininfor:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysLogininforBo logininfor) {
        List<SysLogininforVo> list = logininforService.selectLogininforList(logininfor);
        ExcelUtil.exportExcel(list, "登录日志", SysLogininforVo.class, response);
    }

    /**
     * 批量删除登录日志
     * @param infoIds 日志ids
     */
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public R<Void> remove(@PathVariable Long[] infoIds) {
        boolean deleted = logininforService.deleteLogininforByIds(infoIds);
        if (!deleted) {
            R.fail("删除登录日志记录失败!");
        }
        return R.ok();
    }

    /**
     * 清理系统访问记录
     */
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public R<Void> clean() {
        boolean cleaned = logininforService.cleanLogininfor();
        if (!cleaned) {
            R.fail("清除登录日志记录失败!");
        }
        return R.ok();
    }

    @SaCheckPermission("monitor:logininfor:unlock")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @GetMapping("/unlock/{userName}")
    public R<Void> unlock(@PathVariable("userName") String userName) {
        String loginName = GlobalConstants.PWD_ERR_CNT_KEY + userName;
        if (RedisUtils.hasKey(loginName)) {
            RedisUtils.deleteObject(loginName);
        }
        return R.ok();
    }
}
