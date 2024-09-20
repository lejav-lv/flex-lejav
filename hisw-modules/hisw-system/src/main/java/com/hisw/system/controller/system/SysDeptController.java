package com.hisw.system.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.collection.ListUtil;
import com.hisw.common.core.constant.UserConstants;
import com.hisw.common.core.core.domain.R;
import com.hisw.common.core.core.text.Convert;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.log.annotation.Log;
import com.hisw.common.log.enums.BusinessType;
import com.hisw.common.web.core.BaseController;
import com.hisw.system.domain.bo.SysDeptBo;
import com.hisw.system.domain.vo.SysDeptVo;
import com.hisw.system.service.ISysDeptService;
import com.hisw.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 部门信息
 *
 * @author lejav
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController {

    @Resource
    private ISysDeptService deptService;

    @Resource
    private ISysUserService sysUserService;

    /**
     * 获取部门列表
     */
    @SaCheckPermission("system:dept:list")
    @GetMapping("/list")
    public R<List<SysDeptVo>> list(SysDeptBo deptBo) {
        List<SysDeptVo> depts = deptService.selectDeptList(deptBo);
        return R.ok(depts);
    }

    /**
     * 查询部门列表（排除节点）
     */
    @SaCheckPermission("system:dept:list")
    @GetMapping("/list/exclude/{deptId}")
    public R<List<SysDeptVo>> excludeChild(@PathVariable(value = "deptId", required = false) Long deptId) {
        List<SysDeptVo> depts = deptService.selectDeptList(new SysDeptBo());
        depts.removeIf(d -> d.getDeptId().equals(deptId) || StringUtils.splitList(d.getAncestors()).contains(Convert.toStr(deptId)));
        return R.ok(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @SaCheckPermission("system:dept:query")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @GetMapping(value = "/{deptId}")
    public R<SysDeptVo> getInfo(@PathVariable Long deptId) {
        deptService.checkDeptDataScope(deptId);
        return R.ok(deptService.selectDeptById(deptId));
    }

    /**
     * 新增部门
     */
    @SaCheckPermission("system:dept:add")
    @PostMapping
    public R<Void> add(@Validated @RequestBody SysDeptBo dept) {
        if (!deptService.checkDeptNameUnique(dept)) {
            return R.fail("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        boolean inserted = deptService.insertDept(dept);
        if (!inserted) {
            return R.fail("新增部门记录失败！");
        }
        return R.ok();
    }

    /**
     * 修改部门
     */
    @SaCheckPermission("system:dept:edit")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> edit(@Validated @RequestBody SysDeptBo dept) {
        Long deptId = dept.getDeptId();
        deptService.checkDeptDataScope(deptId);
        if (!deptService.checkDeptNameUnique(dept)) {
            return R.fail("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        } else if (dept.getParentId().equals(deptId)) {
            return R.fail("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        } else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus())) {
            if (deptService.selectNormalChildrenDeptById(deptId) > 0) {
                return R.fail("该部门包含未停用的子部门！");
            } else if (sysUserService.checkDeptExistUser(deptId)) {
                return R.fail("该部门下存在已分配用户，不能禁用!");
            }
        }
        boolean updated = deptService.updateDept(dept);
        if (!updated) {
            return R.fail("修改部门'" + dept.getDeptName() + "'失败");
        }
        return R.ok();
    }

    /**
     * 删除部门
     */
    @SaCheckPermission("system:dept:remove")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public R<Void> remove(@PathVariable Long deptId) {
        if (deptService.hasChildByDeptId(deptId)) {
            return R.warn("存在下级部门,不允许删除");
        }
        if (sysUserService.checkDeptExistUser(deptId)) {
            return R.warn("部门存在用户,不允许删除");
        }
        deptService.checkDeptDataScope(deptId);
        boolean updated = deptService.deleteDeptById(deptId);
        if (!updated) {
            return R.fail("删除部门失败");
        }
        return R.ok();
    }

    /**
     * 获取部门选择框列表
     *
     * @param deptIds 部门ID串
     */
    @SaCheckPermission("system:dept:query")
    @GetMapping("/optionselect")
    public R<List<SysDeptVo>> optionselect(@RequestParam(required = false) Long[] deptIds) {
        return R.ok(deptService.selectDeptByIds(deptIds == null ? null : ListUtil.of(deptIds)));
    }
}
