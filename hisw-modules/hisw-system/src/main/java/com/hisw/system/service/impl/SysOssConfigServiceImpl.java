package com.hisw.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.hisw.common.oss.constant.OssConstant;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.hisw.common.orm.core.service.impl.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.hisw.common.core.constant.CacheNames;
import com.hisw.common.core.exception.ServiceException;
import com.hisw.common.core.utils.MapstructUtils;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.json.utils.JsonUtils;
import com.hisw.common.orm.core.page.PageQuery;
import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.redis.utils.CacheUtils;
import com.hisw.common.redis.utils.RedisUtils;
import com.hisw.system.domain.SysOssConfig;
import com.hisw.system.domain.bo.SysOssConfigBo;
import com.hisw.system.domain.vo.SysOssConfigVo;
import com.hisw.system.mapper.SysOssConfigMapper;
import com.hisw.system.service.ISysOssConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static com.hisw.system.domain.table.SysOssConfigTableDef.SYS_OSS_CONFIG;

/**
 * 对象存储配置Service业务层处理
 *
 * @author lejav
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysOssConfigServiceImpl extends BaseServiceImpl<SysOssConfigMapper, SysOssConfig> implements ISysOssConfigService {

    @Override
    public QueryWrapper query() {
        return super.query().from(SYS_OSS_CONFIG);
    }

    /**
     * 项目启动时，初始化参数到缓存，加载配置类
     */
    @Override
    public void init() {
        List<SysOssConfig> list = this.list();
        // 加载OSS初始化配置
        for (SysOssConfig config : list) {
            String configKey = config.getConfigKey();
            if ("0".equals(config.getStatus())) {
                RedisUtils.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, configKey);
            }
            CacheUtils.put(CacheNames.SYS_OSS_CONFIG, config.getConfigKey(), JsonUtils.toJsonString(config));
        }
    }

    @Override
    public SysOssConfigVo queryById(Long ossConfigId) {
        return this.getOneAs(query().where(SYS_OSS_CONFIG.OSS_CONFIG_ID.eq(ossConfigId)), SysOssConfigVo.class);
    }

    /**
     * 根据bo构建QueryWrapper查询条件
     *
     * @return 查询条件
     */
    private QueryWrapper buildQueryWrapper(SysOssConfigBo bo) {
        return super.buildBaseQueryWrapper()
            .and(SYS_OSS_CONFIG.CONFIG_KEY.eq(bo.getConfigKey()))
            .and(SYS_OSS_CONFIG.BUCKET_NAME.like(bo.getBucketName()))
            .and(SYS_OSS_CONFIG.STATUS.eq(bo.getStatus()))
            .orderBy(SYS_OSS_CONFIG.OSS_CONFIG_ID.asc());
    }

    @Override
    public TableDataInfo<SysOssConfigVo> queryPageList(SysOssConfigBo bo) {
        QueryWrapper queryWrapper = buildQueryWrapper(bo);
        Page<SysOssConfigVo> page = this.pageAs(PageQuery.build(), queryWrapper, SysOssConfigVo.class);
        return TableDataInfo.build(page);
    }


    @Override
    public Boolean insertByBo(SysOssConfigBo bo) {
        SysOssConfig config = MapstructUtils.convert(bo, SysOssConfig.class);
        validEntityBeforeSave(config);
        boolean flag = this.save(config);
        if (flag) {
            // 从数据库查询完整的数据做缓存
            config = this.getById(config.getOssConfigId());
            CacheUtils.put(CacheNames.SYS_OSS_CONFIG, config.getConfigKey(), JsonUtils.toJsonString(config));
        }
        return flag;
    }

    @Override
    public Boolean updateByBo(SysOssConfigBo bo) {
        SysOssConfig config = MapstructUtils.convert(bo, SysOssConfig.class);
        validEntityBeforeSave(config);
        boolean flag = this.updateById(config);
        if (flag) {
            // 从数据库查询完整的数据做缓存
            config = this.getById(config.getOssConfigId());
            CacheUtils.put(CacheNames.SYS_OSS_CONFIG, config.getConfigKey(), JsonUtils.toJsonString(config));
        }
        return flag;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(SysOssConfig entity) {
        if (StringUtils.isNotEmpty(entity.getConfigKey())
            && !checkConfigKeyUnique(entity)) {
            throw new ServiceException("操作配置'" + entity.getConfigKey() + "'失败, 配置key已存在!");
        }
    }

    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            if (CollUtil.containsAny(ids, OssConstant.SYSTEM_DATA_IDS)) {
                throw new ServiceException("系统内置, 不可删除!");
            }
        }
        List<SysOssConfig> list = CollUtil.newArrayList();
        for (Long configId : ids) {
            SysOssConfig config = this.getById(configId);
            list.add(config);
        }
        boolean flag = this.removeByIds(ids);
        if (flag) {
            list.forEach(sysOssConfig ->
                CacheUtils.evict(CacheNames.SYS_OSS_CONFIG, sysOssConfig.getConfigKey()));
        }
        return flag;
    }

    /**
     * 判断configKey是否唯一
     */
    private boolean checkConfigKeyUnique(SysOssConfig sysOssConfig) {
        long ossConfigId = ObjectUtil.isNull(sysOssConfig.getOssConfigId()) ? -1L : sysOssConfig.getOssConfigId();

        QueryWrapper queryWrapper = query();
        if (StringUtils.isNotEmpty(sysOssConfig.getConfigKey())) {
            queryWrapper.where(SYS_OSS_CONFIG.CONFIG_KEY.eq(sysOssConfig.getConfigKey()));
        }
        SysOssConfig info = this.getOne(queryWrapper);
        if (ObjectUtil.isNotNull(info) && info.getOssConfigId() != ossConfigId) {
            return false;
        }
        return true;
    }

    /**
     * 转到非默认状态：停用
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOssConfigStatus(SysOssConfigBo bo) {
        SysOssConfig sysOssConfig = MapstructUtils.convert(bo, SysOssConfig.class);
        boolean updated = this.updateById(sysOssConfig);
        if (updated) {
            RedisUtils.setCacheObject(OssConstant.DEFAULT_CONFIG_KEY, sysOssConfig.getConfigKey());
        }
        return updated;
    }

}
