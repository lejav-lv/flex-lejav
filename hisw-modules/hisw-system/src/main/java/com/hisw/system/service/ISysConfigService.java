package com.hisw.system.service;

import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.orm.core.service.IBaseService;
import com.hisw.system.domain.SysConfig;
import com.hisw.system.domain.bo.SysConfigBo;
import com.hisw.system.domain.vo.SysConfigVo;

import java.util.List;

/**
 * 参数配置 服务层
 *
 * @author lejav
 */
public interface ISysConfigService extends IBaseService<SysConfig> {
    /**
     * 查询参数配置信息
     *
     * @param configId 参数配置ID
     * @return 参数配置信息Vo
     */
    SysConfigVo selectConfigById(Long configId);

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数键名
     * @return 参数键值
     */
    SysConfigVo selectConfigByKey(String configKey);

    /**
     * 获取注册开关（不走Mybatis-Flex租户插件）
     *
     * @param tenantId 租户id
     * @return true开启，false关闭
     */
    boolean selectRegisterEnabled(Long tenantId);

    /**
     * 查询参数配置列表
     *
     * @param configBo 参数配置信息
     * @return 参数配置集合
     */
    List<SysConfigVo> selectConfigList(SysConfigBo configBo);

    /**
     * 分页查询参数配置列表
     *
     * @param configBo 参数配置信息
     * @return 参数配置集合
     */
    TableDataInfo<SysConfigVo> selectConfigPage(SysConfigBo configBo);

    /**
     * 新增参数配置
     *
     * @param config 参数配置信息
     * @return true 操作成功，false 操作失败
     */
    boolean insertConfig(SysConfigBo config);

    /**
     * 修改参数配置
     *
     * @param configBo 参数配置信息
     * @return 结果
     */
    boolean updateConfig(SysConfigBo configBo);

    /**
     * 根据Key修改参数配置
     *
     * @param configBo 参数配置信息
     * @return 结果
     */
    boolean updateConfigByKey(SysConfigBo configBo);

    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    void deleteConfigByIds(Long[] configIds);

    /**
     * 重置参数缓存数据
     */
    void resetConfigCache();

    /**
     * 校验参数键名是否唯一
     *
     * @param configBo 参数信息
     * @return 结果
     */
    boolean checkConfigKeyUnique(SysConfigBo configBo);
}
