package com.hisw.common.core.constant;

/**
 * 租户常量信息
 *
 * @author lejav
 */
public interface TenantConstants {

    /**
     * 租户正常状态
     */
    String NORMAL = "0";

    /**
     * 超级管理员ID
     */
    Long SUPER_ADMIN_ID = 1L;

    /**
     * 超级管理员角色 roleKey
     */
    String SUPER_ADMIN_ROLE_KEY = "SuperAminRole";

    /**
     * 租户管理员角色 roleKey
     */
    String TENANT_ADMIN_ROLE_KEY = "AdminRole";

    /**
     * 租户管理员角色名称
     */
    String TENANT_ADMIN_ROLE_NAME = "管理员角色";

    /**
     * 默认租户ID
     */
    Long DEFAULT_TENANT_ID = 1L;

}
