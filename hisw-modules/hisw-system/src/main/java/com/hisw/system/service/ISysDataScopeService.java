package com.hisw.system.service;

import com.mybatisflex.core.query.QueryWrapper;

/**
 * 通用"数据权限过滤“服务
 *
 * @author lejav
 */
public interface ISysDataScopeService {
    /**
     * 添加数据查询过滤条件
     * @return 添加了过滤条件后的queryWrapper
     */
    QueryWrapper addCondition(QueryWrapper queryWrapper);
}
