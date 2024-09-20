package com.hisw.system.service.impl;


import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.hisw.common.core.utils.MapstructUtils;
import com.hisw.common.orm.core.page.PageQuery;
import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.security.utils.LoginHelper;
import com.hisw.system.domain.bo.SysClientBo;
import com.hisw.system.domain.vo.SysClientVo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.hisw.system.service.ISysClientService;
import com.hisw.system.domain.SysClient;
import com.hisw.system.mapper.SysClientMapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.hisw.system.domain.table.SysClientTableDef.SYS_CLIENT;

/**
 * 系统授权表 服务层实现。
 *
 * @author lejav
 */
@Service
public class SysClientServiceImpl extends ServiceImpl<SysClientMapper, SysClient> implements ISysClientService {

    @Override
    public QueryWrapper query() {
        return super.query().from(SYS_CLIENT);
    }

    private QueryWrapper buildQueryWrapper(SysClientBo bo) {
        return query()
            .where(SYS_CLIENT.CLIENT_ID.eq(bo.getClientId()))
            .and(SYS_CLIENT.CLIENT_KEY.eq(bo.getClientKey()))
            .and(SYS_CLIENT.CLIENT_SECRET.eq(bo.getClientSecret()))
            .and(SYS_CLIENT.STATUS.eq(bo.getStatus()))
            .orderBy(SYS_CLIENT.ID, true);
    }

    @Override
    public SysClientVo selectById(Long id) {
        SysClientVo sysClientVo = this.getOneAs(query().where(SYS_CLIENT.ID.eq(id)), SysClientVo.class);
        sysClientVo.setGrantTypeList(StrUtil.split(sysClientVo.getGrantType(),","));
        return sysClientVo;
    }

    @Override
    public SysClient selectByClientId(String clientId) {
        return this.getOne(query().where(SYS_CLIENT.CLIENT_ID.eq(clientId)));
    }

    @Override
    public TableDataInfo<SysClientVo> selectPage(SysClientBo sysClientBo) {
        QueryWrapper queryWrapper = buildQueryWrapper(sysClientBo);
        Page<SysClientVo> page = this.pageAs(PageQuery.build(), queryWrapper, SysClientVo.class);
        page.getRecords().forEach(r -> r.setGrantTypeList(StrUtil.split(r.getGrantType(),",")));
        return TableDataInfo.build(page);
    }

    @Override
    public List<SysClientVo> selectList(SysClientBo sysClientBo) {
        QueryWrapper queryWrapper = buildQueryWrapper(sysClientBo);
        List<SysClientVo> list = this.listAs(queryWrapper, SysClientVo.class);
        list.forEach(r -> r.setGrantTypeList(StrUtil.split(r.getGrantType(),",")));
        return list;
    }

    /**
     * 新增客户端管理
     */
    @Override
    public Boolean insert(SysClientBo sysClientBo) {
        SysClient sysClient = MapstructUtils.convert(sysClientBo, SysClient.class);
        sysClient.setGrantType(String.join(",", sysClientBo.getGrantTypeList()));
        // 生成clientId
        String clientKey = sysClientBo.getClientKey();
        String clientSecret = sysClientBo.getClientSecret();
        sysClient.setClientId(SecureUtil.md5(clientKey + clientSecret));

        Long loginUserId = LoginHelper.getUserId();
        Date createTime = new Date();
        sysClient.setCreateBy(loginUserId);
        sysClient.setCreateTime(createTime);
        sysClient.setUpdateBy(loginUserId);
        sysClient.setUpdateTime(createTime);

        return this.save(sysClient);
    }

    /**
     * 修改客户端管理
     */
    @Override
    public Boolean update(SysClientBo sysClientBo) {
        SysClient sysClient = MapstructUtils.convert(sysClientBo, SysClient.class);
        sysClient.setGrantType(String.join(",", sysClientBo.getGrantTypeList()));

        Long loginUserId = LoginHelper.getUserId();
        Date createTime = new Date();
        sysClient.setUpdateBy(loginUserId);
        sysClient.setUpdateTime(createTime);

        return this.updateById(sysClient);
    }

    /**
     * 修改状态
     */
    @Override
    public boolean updateStatus(SysClientBo sysClientBo) {
        SysClient sysClient = MapstructUtils.convert(sysClientBo, SysClient.class);

        Long loginUserId = LoginHelper.getUserId();
        Date createTime = new Date();
        sysClient.setUpdateBy(loginUserId);
        sysClient.setUpdateTime(createTime);
        return this.updateById(sysClient);
    }

    /**
     * 批量删除客户端管理
     */
    @Override
    public Boolean deleteByIds(Collection<Long> ids) {
        return this.removeByIds(ids);
    }
}
