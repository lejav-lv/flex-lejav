package com.hisw.common.mongodb;

import cn.hutool.core.util.StrUtil;
import com.anwen.mongo.cache.global.DataSourceNameCache;
import com.anwen.mongo.conditions.query.QueryWrapper;
import com.anwen.mongo.conditions.update.UpdateWrapper;
import com.anwen.mongo.mapper.BaseMapper;
import com.anwen.mongo.mapping.TypeReference;
import com.anwen.mongo.model.PageParam;
import com.anwen.mongo.model.PageResult;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 无实体类操作
 */
@Service
public class NoEntityMongoService {

    private static final TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
    };

    @Autowired
    private BaseMapper baseMapper;

    /**
     *
     * @param collectionName  mongo 集合名称
     * @param query 查询条件
     * @param pageParam 分页参数
     * @return 分页对象
     */
    public PageResult<Map<String, Object>> page(String collectionName, QueryWrapper<?> query, PageParam pageParam) {
        return baseMapper.page(collectionName, query, pageParam.getPageNum(), pageParam.getPageSize(), typeReference);
    }

    /**
     * @param database 数据库
     * @param collectionName  mongo 集合名称
     * @param query 查询条件
     * @param pageParam 分页参数
     * @return 分页对象
     */
    public PageResult<Map<String, Object>> page(String database, String collectionName, QueryWrapper<?> query, PageParam pageParam) {
        return baseMapper.page(database, collectionName, query, pageParam.getPageNum(), pageParam.getPageSize(), typeReference);
    }

    /**
     *
     * @param collectionName mongo 集合名称
     * @param entityMapList 实体类Map集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean save(String collectionName, List<Map<String, Object>> entityMapList) {
        return baseMapper.saveBatch(collectionName, entityMapList);
    }

    /**
     *
     * @param database 数据库
     * @param collectionName mongo 集合名称
     * @param entityMapList 实体类Map集合
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean save(String database, String collectionName, List<Map<String, Object>> entityMapList) {
        return baseMapper.saveBatch(database, collectionName, entityMapList);
    }

    /**
     *
     * @param collectionName  mongo 集合名称
     * @param entityMap 单个实体类Map
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean save(String collectionName, Map<String, Object> entityMap) {
        return baseMapper.save(collectionName, entityMap);
    }

    /**
     * @param database 数据库
     * @param collectionName  mongo 集合名称
     * @param entityMap 单个实体类Map
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean save(String database, String collectionName, Map<String, Object> entityMap) {
        return baseMapper.save(database, collectionName, entityMap);
    }


    /**
     *
     * @param collectionName mongo 集合名称
     * @param query 条件
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(String collectionName, UpdateWrapper<?> query) {
        return baseMapper.update(collectionName, query);
    }

    /**
     *@param database 数据库
     * @param collectionName mongo 集合名称
     * @param query 条件
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(String database, String collectionName, UpdateWrapper<?> query) {
        return baseMapper.update(database, collectionName, query);
    }

    /**
     *
     * @param collectionName mongo 集合名称
     * @return Map集合
     */
    public List<Map<String, Object>> list(String collectionName) {
        return baseMapper.list(collectionName, typeReference);
    }

    /**
     *
     * @param database 数据库
     * @param collectionName mongo 集合名称
     * @return Map集合
     */
    public List<Map<String, Object>> list(String database, String collectionName) {
        return baseMapper.list(database, collectionName, typeReference);
    }

    /**
     *
     * @param collectionName  mongo 集合名称
     * @param query 条件
     * @return Map集合
     */
    public List<Map<String, Object>> list(String collectionName, QueryWrapper<?> query) {
        return baseMapper.list(collectionName, query, typeReference);
    }

    /**
     * @param database 数据库
     * @param collectionName  mongo 集合名称
     * @param query 条件
     * @return Map集合
     */
    public List<Map<String, Object>> list(String database, String collectionName, QueryWrapper<?> query) {
        return baseMapper.list(database, collectionName, query, typeReference);
    }

    /**
     *
     * @param collectionName mongo 集合名称
     * @param query 条件
     * @return 单个Map
     */
    public Map<String, Object> one(String collectionName, QueryWrapper<?> query) {
        return baseMapper.one(collectionName, query, typeReference);
    }

    /**
     * @param database 数据库
     * @param collectionName mongo 集合名称
     * @param query 条件
     * @return 单个Map
     */
    public Map<String, Object> one(String database, String collectionName, QueryWrapper<?> query) {
        return baseMapper.one(database, collectionName, query, typeReference);
    }

    /**
     *
     * @param collectionName  mongo 集合名称
     * @param entityMap 单个Map
     * @param idColumnName 唯一主键名称
     * @param query 如果数据已存在的修改条件
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveOrUpdate(String collectionName, Map<String, Object> entityMap, String idColumnName, UpdateWrapper<?> query) {
        Object o = entityMap.get(idColumnName);
        if (null == o || StrUtil.isBlank(o.toString())) {
            return this.save(collectionName, entityMap);
        }
        return baseMapper.update(collectionName, query);
    }


    /**
     * @param database 数据库
     * @param collectionName  mongo 集合名称
     * @param entityMap 单个Map
     * @param idColumnName 唯一主键名称
     * @param query 如果数据已存在的修改条件
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveOrUpdate(String database, String collectionName, Map<String, Object> entityMap, String idColumnName, UpdateWrapper<?> query) {
        Object o = entityMap.get(idColumnName);
        if (null == o || StrUtil.isBlank(o.toString())) {
            return baseMapper.save(database, collectionName, entityMap);
        }
        return baseMapper.update(database, collectionName, query);
    }

    /**
     * @param collectionName  mongo 集合名称
     * @param entityMap Map集合
     * @param idColumnName 唯一主键名称
     * @param query 如果数据已存在的修改条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(String collectionName, List<Map<String, Object>> entityMap, String idColumnName, UpdateWrapper<?> query) {
        entityMap.forEach(m -> {
            Object o = m.get(idColumnName);
            if (null == o || StrUtil.isBlank(o.toString())) {
                baseMapper.save(collectionName, entityMap);
            } else {
                baseMapper.update(collectionName, query);
            }
        });
    }

    /**
     * @param database 数据库
     * @param collectionName  mongo 集合名称
     * @param entityMap Map集合
     * @param idColumnName 唯一主键名称
     * @param query 如果数据已存在的修改条件
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdate(String database, String collectionName, List<Map<String, Object>> entityMap, String idColumnName, UpdateWrapper<?> query) {
        entityMap.forEach(m -> {
            Object o = m.get(idColumnName);
            if (null == o || StrUtil.isBlank(o.toString())) {
                baseMapper.save(database, collectionName, entityMap);
            } else {
                baseMapper.update(database, collectionName, query);
            }
        });
    }


    /**
     * 删除集合
     * @param collectionName 集合名称
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCollection(String collectionName) {
        MongoClient mongoClient = baseMapper.getMongoPlusClient().getMongoClient();
        MongoDatabase database = mongoClient.getDatabase(DataSourceNameCache.getDatabase());
        database.getCollection(collectionName).drop();
    }

    /**
     * 删除集合
     * @param collectionName 集合名称
     * @param database 数据库
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCollection(String database, String collectionName) {
        MongoClient mongoClient = baseMapper.getMongoPlusClient().getMongoClient();
        MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
        mongoDatabase.getCollection(collectionName).drop();
    }


}
