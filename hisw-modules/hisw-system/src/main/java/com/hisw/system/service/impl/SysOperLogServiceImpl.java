package com.hisw.system.service.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import cn.hutool.core.util.ArrayUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.hisw.common.core.utils.MapstructUtils;
import com.hisw.common.core.utils.ip.AddressUtils;
import com.hisw.common.log.event.OperLogEvent;
import com.hisw.common.orm.core.page.PageQuery;
import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.orm.core.service.impl.BaseServiceImpl;
import com.hisw.system.domain.bo.SysOperLogBo;
import com.hisw.system.domain.vo.SysOperLogVo;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.hisw.system.domain.SysOperLog;
import com.hisw.system.mapper.SysOperLogMapper;
import com.hisw.system.service.ISysOperLogService;

import static com.hisw.system.domain.table.SysOperLogTableDef.SYS_OPER_LOG;

/**
 * 操作日志 服务层处理
 *
 * @author lejav
 */
@Service
public class SysOperLogServiceImpl extends BaseServiceImpl<SysOperLogMapper, SysOperLog> implements ISysOperLogService {

    @Override
    public QueryWrapper query() {
        return super.query().from(SYS_OPER_LOG);
    }

    /**
     * 操作日志记录
     *
     * @param operLogEvent 操作日志事件
     */
    @Async
    @EventListener
    public void recordLog(OperLogEvent operLogEvent) {
        SysOperLogBo operLog = MapstructUtils.convert(operLogEvent, SysOperLogBo.class);
        // 远程查询操作地点
        operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
        insertOperlog(operLog);
    }

    /**
     * 新增操作日志
     *
     * @param operLogBo 操作日志对象
     */
    @Override
    public void insertOperlog(SysOperLogBo operLogBo) {
        SysOperLog operLog = MapstructUtils.convert(operLogBo, SysOperLog.class);
        operLog.setOperTime(new Date());
        this.save(operLog);
    }

    /**
     * 根据operLogBo构建QueryWrapper查询条件
     *
     * @return 查询条件
     */
    private QueryWrapper buildQueryWrapper(SysOperLogBo operLogBo) {
        QueryWrapper queryWrapper = super.buildBaseQueryWrapper()
            .and(SYS_OPER_LOG.TITLE.like(operLogBo.getTitle()))
            .and(SYS_OPER_LOG.BUSINESS_TYPE.eq(operLogBo.getBusinessType()))
            .and(SYS_OPER_LOG.STATUS.eq(operLogBo.getStatus()))
            .and(SYS_OPER_LOG.OPER_NAME.eq(operLogBo.getOperName()))
            .and(SYS_OPER_LOG.OPER_IP.like(operLogBo.getOperIp()))
            .and(SYS_OPER_LOG.OPER_TIME.between(operLogBo.getParams().get("beginTime"), operLogBo.getParams().get("endTime")))
            .orderBy(SYS_OPER_LOG.OPER_ID.desc());
        if (ArrayUtil.isNotEmpty(operLogBo.getBusinessTypes())) {
            queryWrapper.and(SYS_OPER_LOG.BUSINESS_TYPE.in(Collections.singletonList(operLogBo.getBusinessTypes())));
        }
        return queryWrapper;
    }

    /**
     * 查询系统操作日志集合
     *
     * @param operLogBo 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<SysOperLogVo> selectOperLogList(SysOperLogBo operLogBo) {
        QueryWrapper queryWrapper = buildQueryWrapper(operLogBo);
        return this.listAs(queryWrapper, SysOperLogVo.class);
    }

    /**
     * 分页查询系统操作日志集合
     *
     * @param operLogBo 操作日志对象
     * @return 分页操作日志对象集合
     */
    @Override
    public TableDataInfo<SysOperLogVo> selectPage(SysOperLogBo operLogBo) {
        QueryWrapper queryWrapper = buildQueryWrapper(operLogBo);
        Page<SysOperLogVo> page = this.pageAs(PageQuery.build(), queryWrapper, SysOperLogVo.class);
        return TableDataInfo.build(page);
    }

    /**
     * 批量删除系统操作日志
     * delete from sys_oper_log where oper_id in
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果:true 删除成功，false 删除失败。
     */
    @Override
    public boolean deleteOperLogByIds(Long[] operIds) {
        return this.removeByIds(Arrays.asList(operIds));
    }

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    @Override
    public SysOperLogVo selectOperLogById(Long operId) {
        QueryWrapper queryWrapper = query();
        queryWrapper.where(SYS_OPER_LOG.OPER_ID.eq(operId));
        return this.getOneAs(queryWrapper, SysOperLogVo.class);
    }

    /**
     * 清空操作日志
     * delete from sys_oper_log where oper_id>0
     */
    @Override
    public boolean cleanOperLog() {
        QueryWrapper queryWrapper = query().from(SYS_OPER_LOG).where(SYS_OPER_LOG.OPER_ID.gt(0));
        return this.remove(queryWrapper);
    }
}
