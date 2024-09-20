package com.hisw.system.service;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.hisw.common.core.constant.Constants;
import com.hisw.common.core.constant.GlobalConstants;
import com.hisw.common.core.constant.TenantConstants;
import com.hisw.common.core.core.domain.dto.RoleDTO;
import com.hisw.common.core.core.domain.model.LoginUser;
import com.hisw.common.core.enums.LoginType;
import com.hisw.common.core.enums.TenantStatus;
import com.hisw.common.core.exception.user.UserException;
import com.hisw.common.core.utils.DateUtils;
import com.hisw.common.core.utils.MessageUtils;
import com.hisw.common.core.utils.ServletUtils;
import com.hisw.common.core.utils.SpringUtils;
import com.hisw.common.log.event.LogininforEvent;
import com.hisw.common.redis.utils.RedisUtils;
import com.hisw.common.security.utils.LoginHelper;
import com.hisw.common.tenant.exception.TenantException;
import com.hisw.common.tenant.helper.TenantHelper;
import com.hisw.system.domain.SysUser;
import com.hisw.system.domain.bo.SysUserBo;
import com.hisw.system.domain.vo.SysTenantVo;
import com.hisw.system.domain.vo.SysUserVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

/**
 * 登录校验方法
 *
 * @author lejav
 * @author lejav
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SysLoginService {

    @Value("${user.password.maxRetryCount}")
    private Integer maxRetryCount;

    @Value("${user.password.lockTime}")
    private Integer lockTime;

    @Resource
    private ISysPermissionService permissionService;

    @Resource
    private ISysUserService userService;

    @Resource
    private ISysTenantService tenantService;

    /**
     * 退出登录
     */
    public void logout() {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            if (ObjectUtil.isNull(loginUser)) {
                return;
            }
            if (LoginHelper.isSuperAdmin()) {
                // 超级管理员 登出清除动态租户
                TenantHelper.clearDynamic();
            }
            recordLogininfor(loginUser.getTenantId(), loginUser.getUsername(), Constants.LOGOUT, MessageUtils.message("user.logout.success"));
        } catch (NotLoginException ignored) {
        } finally {
            try {
                StpUtil.logout();
            } catch (NotLoginException ignored) {
            }
        }
    }

    /**
     * 登录校验
     */
    public void checkLogin(LoginType loginType, Long tenantId, String username, Supplier<Boolean> supplier) {
        String errorKey = GlobalConstants.PWD_ERR_CNT_KEY + username;
        String loginFail = Constants.LOGIN_FAIL;
        // 获取用户登录错误次数，默认为0 (可自定义限制策略 例如: key + username + ip)
        int errorNumber = ObjectUtil.defaultIfNull(RedisUtils.getCacheObject(errorKey), 0);
        // 锁定时间内登录 则踢出
        if (errorNumber >= maxRetryCount) {
            recordLogininfor(tenantId, username, loginFail, MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
            throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
        }
        if (supplier.get()) {
            // 错误次数递增
            errorNumber++;
            RedisUtils.setCacheObject(errorKey, errorNumber, Duration.ofMinutes(lockTime));
            // 达到规定错误次数 则锁定登录
            if (errorNumber >= maxRetryCount) {
                recordLogininfor(tenantId, username, loginFail, MessageUtils.message(loginType.getRetryLimitExceed(), maxRetryCount, lockTime));
                throw new UserException(loginType.getRetryLimitExceed(), maxRetryCount, lockTime);
            } else {
                // 未达到规定错误次数
                recordLogininfor(tenantId, username, loginFail, MessageUtils.message(loginType.getRetryLimitCount(), errorNumber));
                throw new UserException(loginType.getRetryLimitCount(), errorNumber);
            }
        }
        // 登录成功 清空错误次数
        RedisUtils.deleteObject(errorKey);
    }

    /**
     * 校验租户
     *
     * @param tenantId 租户编号
     */
    public void checkTenant(Long tenantId) {
        if (TenantConstants.DEFAULT_TENANT_ID.equals(tenantId)) {
            return;
        }
        if (ObjectUtil.isNull(tenantId)) {
            throw new TenantException("tenant.number.not.blank");
        }
        SysTenantVo tenant = tenantService.selectById(tenantId);
        if (ObjectUtil.isNull(tenant)) {
            log.info("登录租户：{} 不存在.", tenantId);
            throw new TenantException("tenant.not.exists");
        } else if (TenantStatus.DISABLE.getCode().equals(tenant.getStatus())) {
            log.info("登录租户：{} 已被停用.", tenantId);
            throw new TenantException("tenant.blocked");
        } else if (ObjectUtil.isNotNull(tenant.getExpireTime())
            && new Date().after(tenant.getExpireTime())) {
            log.info("登录租户：{} 已超过有效期.", tenantId);
            throw new TenantException("tenant.expired");
        }
    }

    /**
     * 构建登录用户
     */
    public LoginUser buildLoginUser(SysUserVo user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setTenantId(user.getTenantId());
        loginUser.setUserId(user.getUserId());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setUsername(user.getUserName());
        loginUser.setNickname(user.getNickName());
        loginUser.setUserType(user.getUserType());
        loginUser.setMenuPermission(permissionService.getMenuPermission(user.getUserId()));
        loginUser.setRolePermission(permissionService.getRolePermission(user.getUserId()));
        loginUser.setDeptName(ObjectUtil.isNull(user.getDept()) ? "" : user.getDept().getDeptName());
        List<RoleDTO> roles = BeanUtil.copyToList(user.getRoles(), RoleDTO.class);
        loginUser.setRoles(roles);
        return loginUser;
    }

    /**
     * 记录租户登录信息
     *
     * @param tenantId 租户ID
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     */
    public void recordLogininfor(Long tenantId, String username, String status, String message) {
        LogininforEvent logininforEvent = new LogininforEvent();
        logininforEvent.setTenantId(tenantId);
        logininforEvent.setUsername(username);
        logininforEvent.setStatus(status);
        logininforEvent.setMessage(message);
        logininforEvent.setRequest(ServletUtils.getRequest());
        SpringUtils.context().publishEvent(logininforEvent);
    }

    /**
     * 记录登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId, String ip) {
        SysUserVo sysUserVo = userService.selectUserById(userId);
        if (ObjectUtil.isNull(sysUserVo)) {
            return;
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(ip);
        sysUser.setLoginDate(new Date());
        sysUser.setUpdateBy(userId);
        sysUser.setVersion(sysUserVo.getVersion());
        userService.updateById(sysUser);
    }
}
