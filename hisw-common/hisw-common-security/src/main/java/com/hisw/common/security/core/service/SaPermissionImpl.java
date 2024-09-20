package com.hisw.common.security.core.service;

import cn.dev33.satoken.stp.StpInterface;
import com.hisw.common.core.core.domain.model.LoginUser;
import com.hisw.common.core.enums.UserType;
import com.hisw.common.security.utils.LoginHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * sa-token 权限管理实现类
 *
 * @author lejav
 */
@SuppressWarnings("all")
public class SaPermissionImpl implements StpInterface {

    /**
     * 获取菜单权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        UserType userType = UserType.getUserType(loginUser.getUserType());
        if (userType == UserType.SYS_USER) {
            return new ArrayList<>(loginUser.getMenuPermission());
        }
        return new ArrayList<>();
    }

    /**
     * 获取角色权限列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        UserType userType = UserType.getUserType(loginUser.getUserType());
        if (userType == UserType.SYS_USER) {
            return new ArrayList<>(loginUser.getRolePermission());
        }
        return new ArrayList<>();
    }
}
