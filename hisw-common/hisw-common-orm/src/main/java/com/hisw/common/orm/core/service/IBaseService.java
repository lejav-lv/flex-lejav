package com.hisw.common.orm.core.service;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.service.IService;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.SqlUtil;

import java.util.Collection;

/**
 * 自定义的服务基类接口
 *
 * @author lejav
 */
public interface IBaseService<T> extends IService<T> {

    /**
     * 带主键批量保存实体类对象数据，适用于中间表有联合主键场合但是不通过主键生成器生成主键值，而是程序自己提供主键值，例如sys_role_menu。
     *
     * @param entities  实体类对象
     * @param batchSize 每次保存切分的数量
     */
    @SuppressWarnings("unchecked")
    default boolean saveBatchWithPk(Collection<T> entities, int batchSize) {
        Class<BaseMapper<T>> usefulClass = (Class<BaseMapper<T>>) ClassUtil.getUsefulClass(getMapper().getClass());
        return SqlUtil.toBool(Db.executeBatch(entities, batchSize, usefulClass, BaseMapper::insertSelectiveWithPk));
    }
}
