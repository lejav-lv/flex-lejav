package com.hisw.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.hutool.core.util.ObjectUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.hisw.common.core.constant.CacheNames;
import com.hisw.common.core.service.DictService;
import com.hisw.common.core.utils.MapstructUtils;
import com.hisw.common.core.utils.SpringUtils;
import com.hisw.common.core.utils.StreamUtils;
import com.hisw.common.orm.core.page.PageQuery;
import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.orm.core.service.impl.BaseServiceImpl;
import com.hisw.common.redis.utils.CacheUtils;
import com.hisw.system.domain.SysDictType;
import com.hisw.system.domain.bo.SysDictTypeBo;
import com.hisw.system.domain.vo.SysDictDataVo;
import com.hisw.system.domain.vo.SysDictTypeVo;
import com.hisw.system.service.ISysDictDataService;
import javax.annotation.Resource;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hisw.common.core.constant.UserConstants;
import com.hisw.common.core.exception.ServiceException;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.system.mapper.SysDictTypeMapper;
import com.hisw.system.service.ISysDictTypeService;

import static com.hisw.system.domain.table.SysDictTypeTableDef.SYS_DICT_TYPE;

/**
 * 字典 业务层处理
 *
 * @author lejav
 * @author lejav
 */
@Service
public class SysDictTypeServiceImpl extends BaseServiceImpl<SysDictTypeMapper, SysDictType> implements ISysDictTypeService, DictService {

    @Resource
    private ISysDictDataService sysDictDataService;

    @Override
    public QueryWrapper query() {
        return super.query().from(SYS_DICT_TYPE);
    }

    /**
     * 构造查询条件
     *
     * @return QueryWrapper
     */

    private QueryWrapper buildQueryWrapper(SysDictTypeBo dictTypeBo) {
        return super.buildBaseQueryWrapper()
            .and(SYS_DICT_TYPE.DICT_NAME.like(dictTypeBo.getDictName()))
            .and(SYS_DICT_TYPE.DICT_TYPE.eq(dictTypeBo.getDictType()))
            .and(SYS_DICT_TYPE.CREATE_TIME.between(dictTypeBo.getParams().get("beginTime"), dictTypeBo.getParams().get("endTime")))
            .orderBy(SYS_DICT_TYPE.TENANT_ID.asc(),SYS_DICT_TYPE.DICT_TYPE.asc());
    }

    /**
     * 根据条件分页查询字典类型
     *
     * @param dictTypeBo 字典类型信息
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictTypeVo> selectDictTypeList(SysDictTypeBo dictTypeBo) {
        QueryWrapper queryWrapper = buildQueryWrapper(dictTypeBo);

        return this.listAs(queryWrapper, SysDictTypeVo.class);
    }

    /**
     * 分页查询字典类型
     *
     * @param dictTypeBo 字典类型信息
     * @return 字典类型集合信息
     */
    @Override
    public TableDataInfo<SysDictTypeVo> selectPage(SysDictTypeBo dictTypeBo) {
        QueryWrapper queryWrapper = buildQueryWrapper(dictTypeBo);
        Page<SysDictTypeVo> page = this.pageAs(PageQuery.build(), queryWrapper, SysDictTypeVo.class);
        return TableDataInfo.build(page);
    }

    /**
     * 根据所有字典类型
     *
     * @return 字典类型集合信息
     */
    @Override
    public List<SysDictTypeVo> selectDictTypeAll() {
        return this.listAs(query(), SysDictTypeVo.class);
    }

    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    @Cacheable(cacheNames = CacheNames.SYS_DICT, key = "#dictType")
    @Override
    public List<SysDictDataVo> selectDictDataByType(String dictType) {
        List<SysDictDataVo> lists = sysDictDataService.selectDictDataByType(dictType);
        if (StringUtils.isNotEmpty(lists)) {
            return lists;
        }
        return null;
    }

    /**
     * 根据字典类型ID查询信息
     *
     * @param dictId 字典类型ID
     * @return 字典类型
     */
    @Override
    public SysDictTypeVo selectDictTypeById(Long dictId) {
        return this.getOneAs(query().where(SYS_DICT_TYPE.DICT_ID.eq(dictId)), SysDictTypeVo.class);
    }

    /**
     * 批量删除字典类型信息
     *
     * @param dictIds 需要删除的字典ID
     * @return true 删除成功，false 删除失败。
     */
    @Override
    @Transactional
    public boolean deleteDictTypeByIds(Long[] dictIds) {
        for (Long dictId : dictIds) {
            SysDictTypeVo dictType = selectDictTypeById(dictId);
            if (sysDictDataService.countDictDataByType(dictType.getDictType()) > 0) {
                throw new ServiceException(String.format("%1$s已分配,不能删除", dictType.getDictName()));
            }
            CacheUtils.evict(CacheNames.SYS_DICT, dictType.getDictType());
        }
        return this.removeByIds(Arrays.asList(dictIds));
    }

    /**
     * 重置字典缓存数据
     */
    @Override
    public void resetDictCache() {
        CacheUtils.clear(CacheNames.SYS_DICT);
    }

    /**
     * 新增保存字典类型信息
     *
     * @param sysDictTypeBo 字典类型信息
     * @return 结果
     */
    @CachePut(cacheNames = CacheNames.SYS_DICT, key = "#sysDictTypeBo.dictType")
    @Override
    public List<SysDictDataVo> insertDictType(SysDictTypeBo sysDictTypeBo) {
        SysDictType dict = MapstructUtils.convert(sysDictTypeBo, SysDictType.class);
        boolean saved = this.save(dict);
        if (saved) {
            // 新增 type 下无 data 数据 返回空防止缓存穿透
            return new ArrayList<>();
        }
        throw new ServiceException("插入操作失败");
    }

    /**
     * 修改保存字典类型信息
     *
     * @param dictTypeBo 字典类型信息
     * @return 结果
     */
    @CachePut(cacheNames = CacheNames.SYS_DICT, key = "#dictTypeBo.dictType")
    @Override
    @Transactional
    public List<SysDictDataVo> updateDictType(SysDictTypeBo dictTypeBo) {
        SysDictType dict = MapstructUtils.convert(dictTypeBo, SysDictType.class);
        SysDictTypeVo oldDict = selectDictTypeById(dict.getDictId());
        if (!oldDict.getDictType().equals(dict.getDictType())) {
            sysDictDataService.updateDictDataType(oldDict.getDictType(), dict.getDictType());
        }

        boolean updated = this.updateById(dict);
        if (updated) {
            CacheUtils.evict(CacheNames.SYS_DICT, oldDict.getDictType());
            return sysDictDataService.selectDictDataByType(dict.getDictType());
        }
        throw new ServiceException("修改操作失败");
    }

    /**
     * 校验字典类型称是否唯一
     *
     * @param dictTypeBo 字典类型
     * @return 结果
     */
    @Override
    public boolean checkDictTypeUnique(SysDictTypeBo dictTypeBo) {
        long dictId = StringUtils.isNull(dictTypeBo.getDictId()) ? -1L : dictTypeBo.getDictId();
        SysDictType dictType = this.getOne(query().where(SYS_DICT_TYPE.DICT_TYPE.eq(dictTypeBo.getDictType())));
        if (StringUtils.isNotNull(dictType) && dictType.getDictId().longValue() != dictId) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param dictType  字典类型
     * @param dictValue 字典值
     * @param separator 分隔符
     * @return 字典标签
     */
    @Override
    public String getDictLabel(String dictType, String dictValue, String separator) {
        List<SysDictDataVo> lists = SpringUtils.getAopProxy(this).selectDictDataByType(dictType);
        if (ObjectUtil.isNull(lists)) {
            return StringUtils.EMPTY;
        }
        Map<String, String> map = StreamUtils.toMap(lists, SysDictDataVo::getDictValue, SysDictDataVo::getDictLabel);
        if (StringUtils.containsAny(dictValue, separator)) {
            return Arrays.stream(dictValue.split(separator))
                .map(v -> map.getOrDefault(v, StringUtils.EMPTY))
                .collect(Collectors.joining(separator));
        } else {
            return map.getOrDefault(dictValue, StringUtils.EMPTY);
        }
    }

    /**
     * 根据字典类型和字典标签获取字典值
     *
     * @param dictType  字典类型
     * @param dictLabel 字典标签
     * @param separator 分隔符
     * @return 字典值
     */
    @Override
    public String getDictValue(String dictType, String dictLabel, String separator) {
        List<SysDictDataVo> lists = SpringUtils.getAopProxy(this).selectDictDataByType(dictType);
        if (ObjectUtil.isNull(lists)) {
            return StringUtils.EMPTY;
        }
        Map<String, String> map = StreamUtils.toMap(lists, SysDictDataVo::getDictLabel, SysDictDataVo::getDictValue);
        if (StringUtils.containsAny(dictLabel, separator)) {
            return Arrays.stream(dictLabel.split(separator))
                .map(l -> map.getOrDefault(l, StringUtils.EMPTY))
                .collect(Collectors.joining(separator));
        } else {
            return map.getOrDefault(dictLabel, StringUtils.EMPTY);
        }
    }

    @Override
    public Map<String, String> getAllDictByDictType(String dictType) {
        List<SysDictDataVo> list = selectDictDataByType(dictType);
        return StreamUtils.toMap(list, SysDictDataVo::getDictValue, SysDictDataVo::getDictLabel);
    }
}
