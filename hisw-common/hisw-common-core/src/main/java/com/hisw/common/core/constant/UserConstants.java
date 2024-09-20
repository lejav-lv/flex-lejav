package com.hisw.common.core.constant;

/**
 * 用户常量信息
 *
 * @author lejav
 */
public class UserConstants {

    /**
     * 正常状态
     */
    public static final String NORMAL = "0";

    /**
     * 用户正常状态
     */
    public static final String USER_NORMAL = "0";

    /**
     * 角色正常状态
     */
    public static final String ROLE_NORMAL = "0";

    /**
     * 角色封禁状态
     */
    public static final String ROLE_DISABLE = "1";

    /**
     * 部门正常状态
     */
    public static final String DEPT_NORMAL = "0";

    /**
     * 部门停用状态
     */
    public static final String DEPT_DISABLE = "1";

    /**
     * 岗位正常状态
     */
    public static final String POST_NORMAL = "0";

    /**
     * 岗位停用状态
     */
    public static final String POST_DISABLE = "1";

    /**
     * 是否为系统默认（是）
     */
    public static final String YES = "Y";

    /**
     * 是否菜单外链（是）
     */
    public static final String YES_FRAME = "0";

    /**
     * 是否菜单外链（否）
     */
    public static final String NO_FRAME = "1";

    /**
     * 菜单正常状态
     */
    public static final String MENU_NORMAL = "0";

    /**
     * 菜单类型（目录）
     */
    public static final String TYPE_DIR = "M";

    /**
     * 菜单类型（菜单）
     */
    public static final String TYPE_MENU = "C";

    /**
     * Layout组件标识
     */
    public final static String LAYOUT = "Layout";

    /**
     * ParentView组件标识
     */
    public final static String PARENT_VIEW = "ParentView";

    /**
     * InnerLink组件标识
     */
    public final static String INNER_LINK = "InnerLink";

    /**
     * 校验是否唯一的返回标识
     */
    public final static boolean UNIQUE = true;
    public final static boolean NOT_UNIQUE = false;

    /**
     * 用户名长度限制
     */
    public static final int USERNAME_MIN_LENGTH = 2;
    public static final int USERNAME_MAX_LENGTH = 20;

    /**
     * 密码长度限制
     */
    public static final int PASSWORD_MIN_LENGTH = 5;
    public static final int PASSWORD_MAX_LENGTH = 20;

    /**
     * 超级管理员ID
     */
    public static final Long SUPER_ADMIN_ID = 1L;
}
