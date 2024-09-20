SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;
-- ----------------------------
-- Table structure for sys_client
-- ----------------------------
DROP TABLE IF EXISTS `sys_client`;
CREATE TABLE `sys_client`
(
    `id`             bigint(20) NOT NULL COMMENT 'id',
    `client_id`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '客户端id',
    `client_key`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '客户端key',
    `client_secret`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '客户端秘钥',
    `grant_type`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '授权类型',
    `device_type`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '设备类型',
    `active_timeout` int(11) NULL DEFAULT 1800 COMMENT 'token活跃超时时间',
    `timeout`        int(11) NULL DEFAULT 604800 COMMENT 'token固定超时',
    `status`         char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `version`        int(11) NULL DEFAULT 0 COMMENT '乐观锁',
    `del_flag`       smallint(6) NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    `create_dept`    bigint(20) NULL DEFAULT NULL COMMENT '创建部门',
    `create_by`      bigint(20) NULL DEFAULT NULL COMMENT '创建者',
    `create_time`    datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`      bigint(20) NULL DEFAULT NULL COMMENT '更新者',
    `update_time`    datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark`         varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统授权表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_client
-- ----------------------------

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`
(
    `config_id`    bigint(20) NOT NULL COMMENT '参数主键',
    `tenant_id`    bigint(20) NOT NULL DEFAULT 1 COMMENT '租户编号',
    `config_name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '参数名称',
    `config_key`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '参数键名',
    `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '参数键值',
    `config_type`  char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
    `version`      int(11) NULL DEFAULT 0 COMMENT '乐观锁',
    `create_by`    bigint(20) NOT NULL DEFAULT 0 COMMENT '创建者',
    `create_time`  datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`    bigint(20) NOT NULL DEFAULT 0 COMMENT '更新者',
    `update_time`  datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark`       varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config`
VALUES (1, 1, '主框架页-默认皮肤样式', 'sys.index.skinName', 'skin-blue', 'Y', 0, 1, '2023-06-03 21:32:30', 1,
        '2023-09-12 17:56:29', '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config`
VALUES (2, 1, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 1, 1, '2023-06-03 21:32:30', 1,
        '2024-09-11 19:07:35', '初始化密码 123456');
INSERT INTO `sys_config`
VALUES (3, 1, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 0, 1, '2023-06-03 21:32:30', 1, NULL,
        '深色主题theme-dark，浅色主题theme-light');
INSERT INTO `sys_config`
VALUES (5, 1, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'N', 0, 1, '2023-06-03 21:32:30', 1,
        '2023-09-13 17:16:25', '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `sys_config`
VALUES (6, 1, 'OSS预览列表资源开关', 'sys.oss.previewListResource', 'true', 'Y', 0, 1, '2023-09-30 21:55:15', 1,
        '2023-12-10 19:49:53', 'true:开启, false:关闭');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`
(
    `dept_id`     bigint(20) NOT NULL COMMENT '部门id',
    `tenant_id`   bigint(20) NOT NULL DEFAULT 1 COMMENT '租户编号',
    `parent_id`   bigint(20) NULL DEFAULT 0 COMMENT '父部门id',
    `ancestors`   varchar(760) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '祖级列表',
    `dept_name`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '部门名称',
    `order_num`   int(11) NULL DEFAULT 0 COMMENT '显示顺序',
    `leader`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '负责人',
    `phone`       varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系电话',
    `email`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '邮箱',
    `status`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
    `version`     int(11) NULL DEFAULT 0 COMMENT '乐观锁',
    `del_flag`    smallint(6) NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    `create_by`   bigint(20) NOT NULL DEFAULT 0 COMMENT '创建者',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   bigint(20) NOT NULL DEFAULT 0 COMMENT '更新者',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept`
VALUES (1, 1, 0, '0', 'xxx公司', 0, 'hisw', '18888888888', 'dataprince@foxmail.com', '0', 0, 0, 1,
        '2023-06-03 21:32:28', 1, '2024-01-07 21:44:45');

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`
(
    `dict_code`   bigint(20) NOT NULL COMMENT '字典编码',
    `tenant_id`   bigint(20) NOT NULL DEFAULT 1 COMMENT '租户编号',
    `dict_sort`   int(11) NULL DEFAULT 0 COMMENT '字典排序',
    `dict_label`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '字典标签',
    `dict_value`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '字典键值',
    `dict_type`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '字典类型',
    `css_class`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
    `list_class`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '表格回显样式',
    `is_default`  char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
    `version`     int(11) NULL DEFAULT 0 COMMENT '乐观锁',
    `create_by`   bigint(20) NOT NULL DEFAULT 0 COMMENT '创建者',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   bigint(20) NOT NULL DEFAULT 0 COMMENT '更新者',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data`
VALUES (1, 1, 1, '男', '0', 'sys_user_gender', '', '', 'Y', 0, 1, '2023-06-03 21:32:30', 1, '2023-09-20 09:53:27',
        '性别男');
INSERT INTO `sys_dict_data`
VALUES (2, 1, 2, '女', '1', 'sys_user_gender', '', '', 'N', 0, 1, '2023-06-03 21:32:30', 1, '2023-09-20 09:53:27',
        '性别女');
INSERT INTO `sys_dict_data`
VALUES (3, 1, 3, '未知', '2', 'sys_user_gender', '', '', 'N', 0, 1, '2023-06-03 21:32:30', 1, '2023-09-20 09:53:27',
        '性别未知');
INSERT INTO `sys_dict_data`
VALUES (4, 1, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', 0, 1, '2023-06-03 21:32:30', 1, NULL, '显示菜单');
INSERT INTO `sys_dict_data`
VALUES (5, 1, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', 0, 1, '2023-06-03 21:32:30', 1, NULL, '隐藏菜单');
INSERT INTO `sys_dict_data`
VALUES (6, 1, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', 0, 1, '2023-06-03 21:32:30', 1, NULL,
        '正常状态');
INSERT INTO `sys_dict_data`
VALUES (7, 1, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', 0, 1, '2023-06-03 21:32:30', 1, NULL,
        '停用状态');
INSERT INTO `sys_dict_data`
VALUES (12, 1, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', 0, 1, '2023-06-03 21:32:30', 1, NULL, '系统默认是');
INSERT INTO `sys_dict_data`
VALUES (13, 1, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', 0, 1, '2023-06-03 21:32:30', 1, NULL, '系统默认否');
INSERT INTO `sys_dict_data`
VALUES (14, 1, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', 0, 1, '2023-06-03 21:32:30', 1, NULL, '通知');
INSERT INTO `sys_dict_data`
VALUES (15, 1, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', 0, 1, '2023-06-03 21:32:30', 1, NULL, '公告');
INSERT INTO `sys_dict_data`
VALUES (16, 1, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', 0, 1, '2023-06-03 21:32:30', 1, NULL,
        '正常状态');
INSERT INTO `sys_dict_data`
VALUES (17, 1, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', 0, 1, '2023-06-03 21:32:30', 1, NULL,
        '关闭状态');
INSERT INTO `sys_dict_data`
VALUES (18, 1, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', 0, 1, '2023-06-03 21:32:30', 1, NULL, '其他操作');
INSERT INTO `sys_dict_data`
VALUES (19, 1, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', 0, 1, '2023-06-03 21:32:30', 1, NULL, '新增操作');
INSERT INTO `sys_dict_data`
VALUES (20, 1, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', 0, 1, '2023-06-03 21:32:30', 1, NULL, '修改操作');
INSERT INTO `sys_dict_data`
VALUES (21, 1, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', 0, 1, '2023-06-03 21:32:30', 1, NULL, '删除操作');
INSERT INTO `sys_dict_data`
VALUES (22, 1, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', 0, 1, '2023-06-03 21:32:30', 1, NULL, '授权操作');
INSERT INTO `sys_dict_data`
VALUES (23, 1, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', 0, 1, '2023-06-03 21:32:30', 1, NULL, '导出操作');
INSERT INTO `sys_dict_data`
VALUES (24, 1, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', 0, 1, '2023-06-03 21:32:30', 1, NULL, '导入操作');
INSERT INTO `sys_dict_data`
VALUES (25, 1, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', 0, 1, '2023-06-03 21:32:30', 1, NULL, '强退操作');
INSERT INTO `sys_dict_data`
VALUES (27, 1, 8, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', 0, 1, '2023-06-03 21:32:30', 1, NULL,
        '清空操作');
INSERT INTO `sys_dict_data`
VALUES (28, 1, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', 0, 1, '2023-06-03 21:32:30', 1, NULL,
        '正常状态');
INSERT INTO `sys_dict_data`
VALUES (29, 1, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', 0, 1, '2023-06-03 21:32:30', 1, NULL,
        '停用状态');
INSERT INTO `sys_dict_data`
VALUES (30, 1, 1, '密码认证', 'password', 'sys_grant_type', 'el-check-tag', 'default', 'N', 0, 1, '2023-10-21 11:10:51',
        1, '2023-10-21 11:10:51', '密码认证');
INSERT INTO `sys_dict_data`
VALUES (35, 1, 1, 'PC', 'pc', 'sys_device_type', '', 'default', 'N', 0, 1, '2023-10-21 11:41:10', 1,
        '2023-10-21 11:41:10', 'PC');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`
(
    `dict_id`     bigint(20) NOT NULL COMMENT '字典主键',
    `tenant_id`   bigint(20) NOT NULL DEFAULT 1 COMMENT '租户编号',
    `dict_name`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '字典名称',
    `dict_type`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '字典类型',
    `version`     int(11) NULL DEFAULT 0 COMMENT '乐观锁',
    `create_by`   bigint(20) NOT NULL DEFAULT 0 COMMENT '创建者',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   bigint(20) NOT NULL DEFAULT 0 COMMENT '更新者',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`dict_id`) USING BTREE,
    UNIQUE INDEX `dict_type_unq_idx`(`tenant_id` ASC, `dict_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type`
VALUES (1, 1, '用户性别', 'sys_user_gender', 0, 1, '2023-06-03 21:32:30', 1, '2023-09-20 09:53:27', '用户性别列表');
INSERT INTO `sys_dict_type`
VALUES (2, 1, '菜单状态', 'sys_show_hide', 0, 1, '2023-06-03 21:32:30', 1, NULL, '菜单状态列表');
INSERT INTO `sys_dict_type`
VALUES (3, 1, '系统开关', 'sys_normal_disable', 0, 1, '2023-06-03 21:32:30', 1, NULL, '系统开关列表');
INSERT INTO `sys_dict_type`
VALUES (6, 1, '系统是否', 'sys_yes_no', 0, 1, '2023-06-03 21:32:30', 1, NULL, '系统是否列表');
INSERT INTO `sys_dict_type`
VALUES (7, 1, '通知类型', 'sys_notice_type', 0, 1, '2023-06-03 21:32:30', 1, NULL, '通知类型列表');
INSERT INTO `sys_dict_type`
VALUES (8, 1, '通知状态', 'sys_notice_status', 0, 1, '2023-06-03 21:32:30', 1, NULL, '通知状态列表');
INSERT INTO `sys_dict_type`
VALUES (9, 1, '操作类型', 'sys_oper_type', 0, 1, '2023-06-03 21:32:30', 1, NULL, '操作类型列表');
INSERT INTO `sys_dict_type`
VALUES (10, 1, '系统状态', 'sys_common_status', 0, 1, '2023-06-03 21:32:30', 1, NULL, '登录状态列表');
INSERT INTO `sys_dict_type`
VALUES (11, 1, '授权类型', 'sys_grant_type', 0, 1, '2023-10-21 11:06:33', 1, '2023-10-21 11:06:33', '认证授权类型');
INSERT INTO `sys_dict_type`
VALUES (12, 1, '设备类型', 'sys_device_type', 0, 1, '2023-10-21 11:38:41', 1, '2023-10-21 11:38:41', '客户端设备类型');


-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`
(
    `info_id`        bigint(20) NOT NULL COMMENT '访问ID',
    `tenant_id`      bigint(20) NOT NULL DEFAULT 1 COMMENT '租户编号',
    `user_name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '用户账号',
    `client_key`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '客户端',
    `device_type`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '设备类型',
    `ipaddr`         varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '登录IP地址',
    `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '登录地点',
    `browser`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '浏览器类型',
    `os`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '操作系统',
    `status`         char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
    `msg`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '提示消息',
    `login_time`     datetime NULL DEFAULT NULL COMMENT '访问时间',
    PRIMARY KEY (`info_id`) USING BTREE,
    INDEX            `idx_sys_logininfor_s`(`status` ASC) USING BTREE,
    INDEX            `idx_sys_logininfor_lt`(`login_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `menu_id`     bigint(20) NOT NULL COMMENT '菜单ID',
    `menu_name`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '菜单名称',
    `parent_id`   bigint(20) NULL DEFAULT 0 COMMENT '父菜单ID',
    `order_num`   int(11) NULL DEFAULT 0 COMMENT '显示顺序',
    `path`        varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '路由地址',
    `component`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '组件路径',
    `query_param` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '路由参数',
    `is_frame`    int(11) NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
    `is_cache`    int(11) NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
    `menu_type`   char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
    `visible`     char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
    `status`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
    `perms`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '权限标识',
    `icon`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '#' COMMENT '菜单图标',
    `version`     int(11) NULL DEFAULT 0 COMMENT '乐观锁',
    `create_by`   bigint(20) NOT NULL DEFAULT 0 COMMENT '创建者',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   bigint(20) NULL DEFAULT 0 COMMENT '更新者',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu`
VALUES (1, '系统管理', 0, 1, 'system', NULL, '', 1, 0, 'M', '0', '0', '', 'system', 0, 1, '2023-06-03 21:32:28', 1,
        NULL, '系统管理目录');
INSERT INTO `sys_menu`
VALUES (2, '系统监控', 0, 2, 'monitor', NULL, '', 1, 0, 'M', '0', '0', '', 'monitor', 0, 1, '2023-06-03 21:32:28', 1,
        NULL, '系统监控目录');
INSERT INTO `sys_menu`
VALUES (6, '租户管理', 0, 3, 'tenant', NULL, '', 1, 0, 'M', '0', '0', '', 'chart', 1, 1, '2024-01-03 20:11:18', 1,
        '2024-01-07 21:45:55', '租户管理目录');
INSERT INTO `sys_menu`
VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 0, 1,
        '2023-06-03 21:32:28', 1, NULL, '用户管理菜单');
INSERT INTO `sys_menu`
VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 0,
        1, '2023-06-03 21:32:28', 1, NULL, '角色管理菜单');
INSERT INTO `sys_menu`
VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table',
        0, 1, '2023-06-03 21:32:28', 1, NULL, '菜单管理菜单');
INSERT INTO `sys_menu`
VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 0, 1,
        '2023-06-03 21:32:28', 1, NULL, '部门管理菜单');
INSERT INTO `sys_menu`
VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 0, 1,
        '2023-06-03 21:32:28', 1, NULL, '岗位管理菜单');
INSERT INTO `sys_menu`
VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 0, 1,
        '2023-06-03 21:32:28', 1, NULL, '字典管理菜单');
INSERT INTO `sys_menu`
VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit',
        0, 1, '2023-06-03 21:32:28', 1, NULL, '参数设置菜单');
INSERT INTO `sys_menu`
VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', 1, 0, 'C', '0', '0', 'system:notice:list',
        'message', 0, 1, '2023-06-03 21:32:28', 1, NULL, '通知公告菜单');
INSERT INTO `sys_menu`
VALUES (108, '日志管理', 1, 9, 'log', '', '', 1, 0, 'M', '0', '0', '', 'log', 0, 1, '2023-06-03 21:32:28', 1, NULL,
        '日志管理菜单');
INSERT INTO `sys_menu`
VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', 1, 0, 'C', '0', '0', 'monitor:online:list',
        'online', 0, 1, '2023-06-03 21:32:28', 1, NULL, '在线用户菜单');
INSERT INTO `sys_menu`
VALUES (113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis',
        0, 1, '2023-06-03 21:32:28', 1, NULL, '缓存监控菜单');
INSERT INTO `sys_menu`
VALUES (118, '文件管理', 1, 10, 'oss', 'system/oss/index', '', 1, 0, 'C', '0', '0', 'system:oss:list', 'upload', 0, 1,
        '2023-12-03 08:46:11', 1, '2023-12-03 08:46:11', '文件管理菜单');
INSERT INTO `sys_menu`
VALUES (121, '租户管理', 6, 1, 'tenant', 'system/tenant/index', '', 1, 0, 'C', '0', '0', 'system:tenant:list', 'list',
        0, 1, '2024-01-03 20:11:18', NULL, NULL, '租户管理菜单');
INSERT INTO `sys_menu`
VALUES (122, '租户套餐管理', 6, 2, 'tenantPackage', 'system/tenantPackage/index', '', 1, 0, 'C', '0', '0',
        'system:tenantPackage:list', 'form', 0, 1, '2024-01-03 20:11:18', NULL, NULL, '租户套餐管理菜单');
INSERT INTO `sys_menu`
VALUES (123, '客户端管理', 1, 11, 'client', 'system/client/index', '', 1, 0, 'C', '0', '0', 'system:client:list',
        'international', 0, 1, '2024-01-03 21:29:43', NULL, NULL, '客户端管理菜单');
INSERT INTO `sys_menu`
VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list',
        'form', 0, 1, '2023-06-03 21:32:29', 1, NULL, '操作日志菜单');
INSERT INTO `sys_menu`
VALUES (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', 1, 0, 'C', '0', '0',
        'monitor:logininfor:list', 'logininfor', 0, 1, '2023-06-03 21:32:29', 1, NULL, '登录日志菜单');
INSERT INTO `sys_menu`
VALUES (1000, '用户查询', 100, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1001, '用户新增', 100, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 0, 1, '2023-06-03 21:32:29',
        1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1002, '用户修改', 100, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 0, 1, '2023-06-03 21:32:29',
        1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1003, '用户删除', 100, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1004, '用户导出', 100, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1005, '用户导入', 100, 6, '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1006, '重置密码', 100, 7, '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1007, '角色查询', 101, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1008, '角色新增', 101, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 0, 1, '2023-06-03 21:32:29',
        1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1009, '角色修改', 101, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 0, 1, '2023-06-03 21:32:29',
        1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1010, '角色删除', 101, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1011, '角色导出', 101, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1012, '菜单查询', 102, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1013, '菜单新增', 102, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 0, 1, '2023-06-03 21:32:29',
        1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1014, '菜单修改', 102, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 0, 1, '2023-06-03 21:32:29',
        1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1015, '菜单删除', 102, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1016, '部门查询', 103, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1017, '部门新增', 103, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 0, 1, '2023-06-03 21:32:29',
        1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1018, '部门修改', 103, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 0, 1, '2023-06-03 21:32:29',
        1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1019, '部门删除', 103, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1020, '岗位查询', 104, 1, '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1021, '岗位新增', 104, 2, '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 0, 1, '2023-06-03 21:32:29',
        1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1022, '岗位修改', 104, 3, '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 0, 1, '2023-06-03 21:32:29',
        1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1023, '岗位删除', 104, 4, '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1024, '岗位导出', 104, 5, '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1025, '字典查询', 105, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1026, '字典新增', 105, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 0, 1, '2023-06-03 21:32:29',
        1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1027, '字典修改', 105, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1028, '字典删除', 105, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1029, '字典导出', 105, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1030, '参数查询', 106, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1031, '参数新增', 106, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1032, '参数修改', 106, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1033, '参数删除', 106, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1034, '参数导出', 106, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1035, '公告查询', 107, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1036, '公告新增', 107, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1037, '公告修改', 107, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1038, '公告删除', 107, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1039, '操作查询', 500, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1040, '操作删除', 500, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1041, '日志导出', 500, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1042, '登录查询', 501, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1043, '登录删除', 501, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1044, '日志导出', 501, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1045, '账户解锁', 501, 4, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1046, '在线查询', 109, 1, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1047, '批量强退', 109, 2, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1048, '单条强退', 109, 3, '#', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 0, 1,
        '2023-06-03 21:32:29', 1, NULL, '');
INSERT INTO `sys_menu`
VALUES (1061, '客户端管理查询', 123, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:client:query', '#', 0, 1,
        '2024-01-03 21:29:43', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1062, '客户端管理新增', 123, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:client:add', '#', 0, 1,
        '2024-01-03 21:29:43', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1063, '客户端管理修改', 123, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:client:edit', '#', 0, 1,
        '2024-01-03 21:29:43', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1064, '客户端管理删除', 123, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:client:remove', '#', 0, 1,
        '2024-01-03 21:29:43', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1065, '客户端管理导出', 123, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:client:export', '#', 0, 1,
        '2024-01-03 21:29:43', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1600, '文件查询', 118, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:query', '#', 0, 1,
        '2023-12-25 15:15:25', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1601, '文件上传', 118, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:upload', '#', 0, 1,
        '2023-12-25 15:15:25', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1602, '文件下载', 118, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:download', '#', 0, 1,
        '2023-12-25 15:15:25', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1603, '文件删除', 118, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:oss:remove', '#', 0, 1,
        '2023-12-25 15:15:25', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1606, '租户查询', 121, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:query', '#', 0, 1,
        '2024-01-03 20:11:18', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1607, '租户新增', 121, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:add', '#', 0, 1,
        '2024-01-03 20:11:18', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1608, '租户修改', 121, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:edit', '#', 0, 1,
        '2024-01-03 20:11:18', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1609, '租户删除', 121, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:remove', '#', 0, 1,
        '2024-01-03 20:11:18', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1610, '租户导出', 121, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenant:export', '#', 0, 1,
        '2024-01-03 20:11:18', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1611, '租户套餐查询', 122, 1, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:query', '#', 0, 1,
        '2024-01-03 20:11:19', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1612, '租户套餐新增', 122, 2, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:add', '#', 0, 1,
        '2024-01-03 20:11:19', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1613, '租户套餐修改', 122, 3, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:edit', '#', 0, 1,
        '2024-01-03 20:11:19', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1614, '租户套餐删除', 122, 4, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:remove', '#', 0, 1,
        '2024-01-03 20:11:19', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1615, '租户套餐导出', 122, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:tenantPackage:export', '#', 0, 1,
        '2024-01-03 20:11:19', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1620, '配置列表', 118, 5, '#', '', '', 1, 0, 'F', '0', '0', 'system:ossConfig:list', '#', 0, 1,
        '2023-12-25 15:15:25', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1621, '配置添加', 118, 6, '#', '', '', 1, 0, 'F', '0', '0', 'system:ossConfig:add', '#', 0, 1,
        '2023-12-25 15:15:25', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1622, '配置编辑', 118, 6, '#', '', '', 1, 0, 'F', '0', '0', 'system:ossConfig:edit', '#', 0, 1,
        '2023-12-25 15:15:25', NULL, NULL, '');
INSERT INTO `sys_menu`
VALUES (1623, '配置删除', 118, 6, '#', '', '', 1, 0, 'F', '0', '0', 'system:ossConfig:remove', '#', 0, 1,
        '2023-12-25 15:15:25', NULL, NULL, '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`
(
    `notice_id`      bigint(20) NOT NULL COMMENT '公告ID',
    `tenant_id`      bigint(20) NOT NULL DEFAULT 1 COMMENT '租户编号',
    `notice_title`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '公告标题',
    `notice_type`    char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin     NOT NULL COMMENT '公告类型（1通知 2公告）',
    `notice_content` longblob NULL COMMENT '公告内容',
    `status`         char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
    `version`        int(11) NULL DEFAULT 0 COMMENT '乐观锁',
    `create_by`      bigint(20) NOT NULL DEFAULT 0 COMMENT '创建者',
    `create_time`    datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`      bigint(20) NOT NULL DEFAULT 0 COMMENT '更新者',
    `update_time`    datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`
(
    `oper_id`        bigint(20) NOT NULL COMMENT '日志主键',
    `tenant_id`      bigint(20) NOT NULL DEFAULT 1 COMMENT '租户编号',
    `title`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '模块标题',
    `business_type`  int(11) NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
    `method`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '方法名称',
    `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '请求方式',
    `operator_type`  int(11) NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
    `oper_name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '操作人员',
    `dept_name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '部门名称',
    `oper_url`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '请求URL',
    `oper_ip`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '主机地址',
    `oper_location`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '操作地点',
    `oper_param`     varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '请求参数',
    `json_result`    varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '返回参数',
    `status`         int(11) NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
    `error_msg`      varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '错误消息',
    `oper_time`      datetime NULL DEFAULT NULL COMMENT '操作时间',
    `cost_time`      bigint(20) NULL DEFAULT 0 COMMENT '消耗时间',
    PRIMARY KEY (`oper_id`) USING BTREE,
    INDEX            `idx_sys_oper_log_bt`(`business_type` ASC) USING BTREE,
    INDEX            `idx_sys_oper_log_s`(`status` ASC) USING BTREE,
    INDEX            `idx_sys_oper_log_ot`(`oper_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_oss
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss`;
CREATE TABLE `sys_oss`
(
    `oss_id`        bigint(20) NOT NULL COMMENT '对象存储主键',
    `tenant_id`     bigint(20) NOT NULL DEFAULT 1 COMMENT '租户编号',
    `file_name`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '文件名',
    `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '原名',
    `file_suffix`   varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '文件后缀名',
    `url`           varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'URL地址',
    `service`       varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL DEFAULT 'minio' COMMENT '服务商',
    `version`       int(11) NULL DEFAULT 0 COMMENT '乐观锁',
    `create_by`     bigint(20) NULL DEFAULT NULL COMMENT '上传人',
    `create_time`   datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`     bigint(20) NULL DEFAULT NULL COMMENT '更新人',
    `update_time`   datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`oss_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = 'OSS对象存储表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oss
-- ----------------------------

-- ----------------------------
-- Table structure for sys_oss_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss_config`;
CREATE TABLE `sys_oss_config`
(
    `oss_config_id` bigint(20) NOT NULL COMMENT '主建',
    `tenant_id`     bigint(20) NOT NULL DEFAULT 1 COMMENT '租户编号',
    `config_key`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '配置key',
    `access_key`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT 'accessKey',
    `secret_key`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '秘钥',
    `bucket_name`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '桶名称',
    `prefix`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '前缀',
    `endpoint`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '访问站点',
    `domain`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '自定义域名',
    `is_https`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'N' COMMENT '是否https（Y=是,N=否）',
    `region`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '域',
    `access_policy` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin     NOT NULL DEFAULT '1' COMMENT '桶权限类型(0=private 1=public 2=custom)',
    `status`        char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '1' COMMENT '是否默认（0=是,1=否）',
    `ext1`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '扩展字段',
    `version`       int(11) NULL DEFAULT 0 COMMENT '乐观锁',
    `create_by`     bigint(20) NULL DEFAULT NULL COMMENT '创建者',
    `create_time`   datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`     bigint(20) NULL DEFAULT NULL COMMENT '更新者',
    `update_time`   datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark`        varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`oss_config_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '对象存储配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`
(
    `post_id`     bigint(20) NOT NULL COMMENT '岗位ID',
    `tenant_id`   bigint(20) NOT NULL DEFAULT 1 COMMENT '租户编号',
    `post_code`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '岗位编码',
    `post_name`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '岗位名称',
    `post_sort`   int(11) NOT NULL COMMENT '显示顺序',
    `status`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin     NOT NULL COMMENT '状态（0正常 1停用）',
    `version`     int(11) NULL DEFAULT 0 COMMENT '乐观锁',
    `create_by`   bigint(20) NOT NULL DEFAULT 0 COMMENT '创建者',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   bigint(20) NOT NULL DEFAULT 0 COMMENT '更新者',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '岗位信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post`
VALUES (1, 1, 'ceo', '董事长', 1, '0', 0, 1, '2023-06-03 21:32:28', 1, '2023-09-02 15:43:55', '');
INSERT INTO `sys_post`
VALUES (2, 1, 'se', '项目经理', 2, '0', 0, 1, '2023-06-03 21:32:28', 1, NULL, '');
INSERT INTO `sys_post`
VALUES (3, 1, 'hr', '人力资源', 3, '0', 0, 1, '2023-06-03 21:32:28', 1, NULL, '');
INSERT INTO `sys_post`
VALUES (4, 1, 'users', '普通员工', 4, '0', 0, 1, '2023-06-03 21:32:28', 1, '2023-07-13 21:30:24', '');


-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `role_id`             bigint(20) NOT NULL COMMENT '角色ID',
    `tenant_id`           bigint(20) NOT NULL DEFAULT 1 COMMENT '租户编号',
    `role_name`           varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL COMMENT '角色名称',
    `role_key`            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '角色权限字符串',
    `role_sort`           int(11) NOT NULL COMMENT '显示顺序',
    `data_scope`          char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
    `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
    `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
    `status`              char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin      NOT NULL COMMENT '角色状态（0正常 1停用）',
    `version`             int(11) NULL DEFAULT 0 COMMENT '乐观锁',
    `del_flag`            smallint(6) NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    `create_by`           bigint(20) NOT NULL DEFAULT 0 COMMENT '创建者',
    `create_time`         datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`           bigint(20) NOT NULL DEFAULT 0 COMMENT '更新者',
    `update_time`         datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark`              varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role`
VALUES (1, 1, '超级管理员角色', 'SuperAdminRole', 1, '1', 1, 1, '0', 0, 0, 1, '2023-06-03 21:32:28', 1, NULL,
        '超级管理员');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`
(
    `role_id` bigint(20) NOT NULL COMMENT '角色ID',
    `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
    PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '角色和部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`
(
    `role_id` bigint(20) NOT NULL COMMENT '角色ID',
    `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------

-- ----------------------------
-- Table structure for sys_tenant
-- ----------------------------
DROP TABLE IF EXISTS `sys_tenant`;
CREATE TABLE `sys_tenant`
(
    `tenant_id`         bigint(20) NOT NULL COMMENT '租户编号',
    `contact_user_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系人',
    `contact_phone`     varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '联系电话',
    `company_name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '企业名称',
    `license_number`    varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '统一社会信用代码',
    `address`           varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '地址',
    `intro`             varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '企业简介',
    `domain`            varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '域名',
    `remark`            varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
    `package_id`        bigint(20) NULL DEFAULT NULL COMMENT '租户套餐编号',
    `expire_time`       datetime NULL DEFAULT NULL COMMENT '过期时间',
    `account_count`     int(11) NULL DEFAULT -1 COMMENT '用户数量（-1不限制）',
    `version`           int(11) NULL DEFAULT 0 COMMENT '乐观锁',
    `status`            char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT '租户状态（0正常 1停用）',
    `del_flag`          smallint(6) NULL DEFAULT NULL COMMENT '删除标志（0代表存在 1代表删除）',
    `create_by`         bigint(20) NULL DEFAULT NULL COMMENT '创建者',
    `create_time`       datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`         bigint(20) NULL DEFAULT NULL COMMENT '更新者',
    `update_time`       datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`tenant_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '租户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_tenant
-- ----------------------------
INSERT INTO `sys_tenant`
VALUES (1, '联系人', '16888888888', 'hisw', NULL, NULL, 'flex多租户通用后台管理管理系统', NULL, NULL,
        NULL, NULL, -1, 0, '0', 0, 1, '2024-01-03 20:08:03', NULL, NULL);

-- ----------------------------
-- Table structure for sys_tenant_package
-- ----------------------------
DROP TABLE IF EXISTS `sys_tenant_package`;
CREATE TABLE `sys_tenant_package`
(
    `package_id`          bigint(20) NOT NULL COMMENT '租户套餐id',
    `package_name`        varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '套餐名称',
    `menu_ids`            varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '关联菜单id',
    `remark`              varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
    `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
    `status`              char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `version`             int(11) NULL DEFAULT 0 COMMENT '乐观锁',
    `del_flag`            smallint(6) NULL DEFAULT NULL COMMENT '删除标志（0代表存在 1代表删除）',
    `create_by`           bigint(20) NULL DEFAULT NULL COMMENT '创建者',
    `create_time`         datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`           bigint(20) NULL DEFAULT NULL COMMENT '更新者',
    `update_time`         datetime NULL DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`package_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '租户套餐表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_tenant_package
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `user_id`     bigint(20) NOT NULL COMMENT '用户ID',
    `tenant_id`   bigint(20) NOT NULL DEFAULT 1 COMMENT '租户主键',
    `dept_id`     bigint(20) NULL DEFAULT NULL COMMENT '部门ID',
    `user_name`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户账号',
    `nick_name`   varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '用户昵称',
    `user_type`   varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT 'sys_user' COMMENT '用户类型（sys_user系统用户）',
    `email`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '用户邮箱',
    `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '手机号码',
    `gender`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
    `avatar`      bigint(20) NULL DEFAULT NULL COMMENT '头像地址',
    `password`    varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '密码',
    `status`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
    `version`     int(11) NULL DEFAULT 0 COMMENT '乐观锁',
    `del_flag`    smallint(6) NULL DEFAULT 0 COMMENT '删除标志（0代表存在 1代表删除）',
    `login_ip`    varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '最后登录IP',
    `login_date`  datetime NULL DEFAULT NULL COMMENT '最后登录时间',
    `create_by`   bigint(20) NOT NULL DEFAULT 0 COMMENT '创建者',
    `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
    `update_by`   bigint(20) NOT NULL DEFAULT 0 COMMENT '更新者',
    `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`user_id`) USING BTREE,
    UNIQUE INDEX `sys_user_unqindex_tenant_username`(`tenant_id` ASC, `user_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user`
VALUES (1, 1, 1, 'superadmin', '超级管理员', 'sys_user', 'ry@163.com', '15888888888', '1', NULL,
        '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', 13, 0, '0:0:0:0:0:0:0:1',
        '2024-09-11 18:57:53', 1, '2023-06-03 21:32:28', 1, '2024-09-11 18:57:53', '管理员');

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`
(
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
    PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `role_id` bigint(20) NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------

SET
FOREIGN_KEY_CHECKS = 1;
