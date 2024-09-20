package com.hisw.common.orm.listener;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.mybatisflex.annotation.UpdateListener;
import com.hisw.common.core.exception.ServiceException;
import com.hisw.common.orm.core.domain.BaseEntity;
import com.hisw.common.orm.helper.ListenerManager;
import com.hisw.common.security.utils.LoginHelper;

import java.util.Date;

/**
 * Entity实体类全局更新数据监听器
 *
 * @author lejav
 */
public class EntityUpdateListener implements UpdateListener {
    @Override
    public void onUpdate(Object entity) {
        try {
            if (ListenerManager.isDoUpdateListener() && ObjectUtil.isNotNull(entity) && (entity instanceof BaseEntity)) {
                BaseEntity baseEntity = (BaseEntity) entity;
                baseEntity.setUpdateBy(LoginHelper.getUserId());
                baseEntity.setUpdateTime(new Date());
            }
        } catch (Exception e) {
            throw new ServiceException("全局更新数据监听器注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }

    }
}
