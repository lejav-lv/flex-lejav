package com.hisw.common.orm.helper;

import lombok.extern.slf4j.Slf4j;

/**
 * 监听器管理
 * 考虑任务调度、三方接口回调等可自由控制审计字段
 *
 * @author lejav
 */
@Slf4j
public class ListenerManager {

    private static final ThreadLocal<Boolean> ignoreInsertListenerTl = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private static final ThreadLocal<Boolean> ignoreUpdateListenerTl = ThreadLocal.withInitial(() -> Boolean.FALSE);

    /**
     * 是否执行 InsertListener
     * @return 是否执行
     */
    public static boolean isDoInsertListener() {
        return !ignoreInsertListenerTl.get();
    }

    /**
     * 是否执行 UpdateListener
     * @return 是否执行
     */
    public static boolean isDoUpdateListener() {
        return !ignoreUpdateListenerTl.get();
    }

}
