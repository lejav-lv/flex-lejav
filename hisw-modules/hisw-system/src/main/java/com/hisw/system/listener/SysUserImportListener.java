package com.hisw.system.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.hisw.common.core.exception.ServiceException;
import com.hisw.common.core.utils.SpringUtils;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.core.utils.ValidatorUtils;
import com.hisw.common.excel.core.ExcelListener;
import com.hisw.common.excel.core.ExcelResult;
import com.hisw.common.security.utils.LoginHelper;
import com.hisw.system.domain.bo.SysUserBo;
import com.hisw.system.domain.vo.SysDeptVo;
import com.hisw.system.domain.vo.SysUserImportVo;
import com.hisw.system.domain.vo.SysUserVo;
import com.hisw.system.service.ISysConfigService;
import com.hisw.system.service.ISysDeptService;
import com.hisw.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 系统用户自定义导入
 *
 * @author lejav
 */
@Slf4j
public class SysUserImportListener extends AnalysisEventListener<SysUserImportVo> implements ExcelListener<SysUserImportVo> {

    private final ISysUserService userService;

    private final ISysDeptService deptService;

    private final String password;

    private final Boolean isUpdateSupport;

    private final Long operateUserId;

    private int successNum = 0;
    private int failureNum = 0;
    private final StringBuilder successMsg = new StringBuilder();
    private final StringBuilder failureMsg = new StringBuilder();

    public SysUserImportListener(Boolean isUpdateSupport) {
        String initPassword = SpringUtils.getBean(ISysConfigService.class).selectConfigByKey("sys.user.initPassword").getConfigValue();
        this.userService = SpringUtils.getBean(ISysUserService.class);
        this.deptService = SpringUtils.getBean(ISysDeptService.class);
        this.password = BCrypt.hashpw(initPassword);
        this.isUpdateSupport = isUpdateSupport;
        this.operateUserId = LoginHelper.getUserId();
    }

    @Override
    public void invoke(SysUserImportVo userVo, AnalysisContext context) {
        SysUserVo sysUser = this.userService.selectUserByUserName(userVo.getUserName());
        try {
            if (StringUtils.isNotEmpty(userVo.getDeptName())) {
                long deptCount = deptService.selectDeptCountByName(userVo.getDeptName());
                if (deptCount == 1) {
                    SysDeptVo dept = deptService.selectDeptByName(userVo.getDeptName());
                    if (ObjectUtil.isNotNull(dept)) {
                        // 验证是否存在这个用户
                        if (ObjectUtil.isNull(sysUser)) {
                            //用户不存在，插入
                            userVo.setUserId(null);//屏蔽掉前台传过来的用户ID，使用系统的雪花算法值
                            SysUserBo user = BeanUtil.toBean(userVo, SysUserBo.class);
                            user.setDeptId(dept.getDeptId());//获取用户部门ID
                            ValidatorUtils.validate(user);
                            user.setPassword(password);
                            user.setCreateBy(operateUserId);
                            userService.insertUser(user);
                            successNum++;
                            successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getUserName()).append(" 导入成功");
                        } else if (isUpdateSupport) {
                            Long userId = sysUser.getUserId();
                            SysUserBo user = BeanUtil.toBean(userVo, SysUserBo.class);
                            user.setUserId(userId);
                            user.setDeptId(dept.getDeptId());//获取用户部门ID
                            ValidatorUtils.validate(user);
                            userService.checkUserAllowed(user.getUserId());
                            userService.checkUserDataScope(user.getUserId());
                            user.setUpdateBy(operateUserId);
                            userService.updateUser(user);
                            successNum++;
                            successMsg.append("<br/>").append(successNum).append("、账号 ").append(user.getUserName()).append(" 更新成功");
                        } else {
                            failureNum++;
                            failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(sysUser.getUserName()).append(" 已存在");
                        }
                    } else {
                        failureNum++;
                        failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(sysUser.getUserName()).append(" 的部门名称 ").append(userVo.getDeptName()).append(" 在部门表中不存在，无法导入");
                    }
                }
                if (deptCount == 0) {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(userVo.getUserName()).append(" 的部门名称 ").append(userVo.getDeptName()).append(" 在部门表中不存在，无法导入");
                }
                if (deptCount > 1) {
                    failureNum++;
                    failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(userVo.getUserName()).append(" 的部门名称 ").append(userVo.getDeptName()).append(" 存在两条以上部门记录，无法导入");
                }
            } else {
                failureNum++;
                failureMsg.append("<br/>").append(failureNum).append("、账号 ").append(userVo.getUserName()).append(" 的部门名称为空，无法导入");
            }
        } catch (Exception e) {
            failureNum++;
            String msg = "<br/>" + failureNum + "、账号 " + userVo.getUserName() + " 导入失败：";
            failureMsg.append(msg).append(e.getMessage());
            log.error(msg, e);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

    @Override
    public ExcelResult<SysUserImportVo> getExcelResult() {
        return new ExcelResult<SysUserImportVo>() {
            @Override
            public String getAnalysis() {
                if (failureNum > 0) {
                    failureMsg.insert(0, "很抱歉，导入失败！共 " + failureNum + " 条数据没有成功导入，错误如下：");
                    throw new ServiceException(failureMsg.toString());
                } else {
                    successMsg.insert(0, "恭喜您，数据已全部导入成功！共 " + successNum + " 条，数据如下：");
                }
                return successMsg.toString();
            }
            @Override
            public List<SysUserImportVo> getList() {
                return null;
            }

            @Override
            public List<String> getErrorList() {
                return null;
            }
        };
    }
}
