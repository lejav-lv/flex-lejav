package com.hisw.system.service;


import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.system.domain.SysClient;
import com.mybatisflex.core.service.IService;
import com.hisw.system.domain.bo.SysClientBo;
import com.hisw.system.domain.vo.SysClientVo;

import java.util.Collection;
import java.util.List;

/**
 * 系统授权表 服务层。
 *
 * @author lejav
 */
public interface ISysClientService extends IService<SysClient> {
    /**
     * 查询客户端管理
     */
    SysClientVo selectById(Long id);

    /**
     * 查询客户端信息基于客户端id
     */
    SysClient selectByClientId(String clientId);

    /**
     * 查询客户端管理列表
     */
    TableDataInfo<SysClientVo> selectPage(SysClientBo sysClientBo);

    /**
     * 查询客户端管理列表
     */
    List<SysClientVo> selectList(SysClientBo sysClientBo);

    /**
     * 新增客户端管理
     */
    Boolean insert(SysClientBo sysClientBo);

    /**
     * 修改客户端管理
     */
    Boolean update(SysClientBo sysClientBo);

    /**
     * 修改状态
     */
    boolean updateStatus(SysClientBo sysClientBo);

    /**
     * 校验并批量删除客户端管理信息
     */
    Boolean deleteByIds(Collection<Long> ids);
}
