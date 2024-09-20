package com.hisw.system.service;

import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.orm.core.service.IBaseService;
import com.hisw.system.domain.SysDictData;
import com.hisw.system.domain.bo.SysDictDataBo;
import com.hisw.system.domain.vo.SysDictDataVo;

import java.util.List;

/**
 * 字典 业务层
 *
 * @author lejav
 */
public interface ISysDictDataService extends IBaseService<SysDictData> {
    /**
     * 根据条件分页查询字典数据
     *
     * @param dictDataBo 字典数据信息
     * @return 字典数据集合信息
     */
    List<SysDictDataVo> selectDictDataList(SysDictDataBo dictDataBo);

    /**
     * 分页查询字典数据
     *
     * @param dictDataBo 字典类型信息
     * @return 字典数据集合信息
     */
    TableDataInfo<SysDictDataVo> selectPage(SysDictDataBo dictDataBo);

    /**
     * 根据字典数据ID查询信息
     *
     * @param dictCode 字典数据ID
     * @return 字典数据
     */
    SysDictDataVo selectDictDataById(Long dictCode);

    /**
     * 查询字典数据记录数量
     *
     * @param dictType 字典类型
     * @return 字典数据
     */
    Integer countDictDataByType(String dictType);


    /**
     * 根据字典类型查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据集合信息
     */
    List<SysDictDataVo> selectDictDataByType(String dictType);

    /**
     * 批量删除字典数据信息
     *
     * @param dictCodes 需要删除的字典数据ID
     * @return 结果:true 删除成功，false 删除失败。
     */
    boolean deleteDictDataByIds(Long[] dictCodes);

    /**
     * 新增保存字典数据信息
     *
     * @param dataBo 字典数据信息
     * @return true 操作成功，false 操作失败
     */
    boolean insertDictData(SysDictDataBo dataBo);

    /**
     * 修改保存字典数据信息
     *
     * @param dataBo 字典数据信息
     * @return 结果:true 更新成功，false 更新失败。
     */
    boolean updateDictData(SysDictDataBo dataBo);

    /**
     * 同步修改字典类型
     *
     * @param oldDictType 旧字典类型
     * @param newDictType 新旧字典类型
     */
    void updateDictDataType(String oldDictType, String newDictType);
}
