package com.hisw.system.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.hisw.common.core.constant.Constants;
import com.hisw.common.core.utils.MapstructUtils;
import com.hisw.common.core.utils.ServletUtils;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.core.utils.ip.AddressUtils;
import com.hisw.common.log.event.LogininforEvent;
import com.hisw.common.orm.core.page.PageQuery;
import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.orm.core.service.impl.BaseServiceImpl;
import com.hisw.common.security.utils.LoginHelper;
import com.hisw.common.tenant.helper.TenantHelper;
import com.hisw.system.domain.SysClient;
import com.hisw.system.domain.bo.SysLogininforBo;
import com.hisw.system.domain.vo.SysLogininforVo;
import com.hisw.system.service.ISysClientService;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.hisw.system.domain.SysLogininfor;
import com.hisw.system.mapper.SysLogininforMapper;
import com.hisw.system.service.ISysLogininforService;
import org.springframework.transaction.annotation.Transactional;

import static com.hisw.system.domain.table.SysLogininforTableDef.SYS_LOGININFOR;


/**
 * 系统访问日志情况信息 服务层处理
 *
 * @author lejav
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SysLogininforServiceImpl extends BaseServiceImpl<SysLogininforMapper, SysLogininfor> implements ISysLogininforService {

    @Resource
    private ISysClientService clientService;

    @Override
    public QueryWrapper query() {
        return super.query().from(SYS_LOGININFOR);
    }

    /**
     * 记录登录信息
     *
     * @param logininforEvent 登录事件
     */
    @Async
    @EventListener
    public void recordLogininfor(LogininforEvent logininforEvent) {
        HttpServletRequest request = logininforEvent.getRequest();
        final UserAgent userAgent = UserAgentUtil.parse(request.getHeader("User-Agent"));
        final String ip = ServletUtils.getClientIP(request);
        // 客户端信息
        String clientId = request.getHeader(LoginHelper.CLIENT_KEY);
        SysClient client = null;
        if (StringUtils.isNotBlank(clientId)) {
            client = clientService.selectByClientId(clientId);
        }

        String address = AddressUtils.getRealAddressByIP(ip);
        String s = getBlock(ip) +
            address +
            getBlock(logininforEvent.getUsername()) +
            getBlock(logininforEvent.getStatus()) +
            getBlock(logininforEvent.getMessage());
        // 打印信息到日志
        log.info(s, logininforEvent.getArgs());
        // 获取客户端操作系统
        String os = userAgent.getOs().getName();
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();
        // 封装对象
        SysLogininforBo logininfor = new SysLogininforBo();
        logininfor.setTenantId(logininforEvent.getTenantId());
        logininfor.setUserName(logininforEvent.getUsername());
        if (ObjectUtil.isNotNull(client)) {
            logininfor.setClientKey(client.getClientKey());
            logininfor.setDeviceType(client.getDeviceType());
        }
        logininfor.setIpaddr(ip);
        logininfor.setLoginLocation(address);
        logininfor.setBrowser(browser);
        logininfor.setOs(os);
        logininfor.setMsg(logininforEvent.getMessage());
        // 日志状态
        if (StringUtils.equalsAny(logininforEvent.getStatus(), Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
            logininfor.setStatus(Constants.SUCCESS);
        } else if (Constants.LOGIN_FAIL.equals(logininforEvent.getStatus())) {
            logininfor.setStatus(Constants.FAIL);
        }
        // 插入数据
        TenantHelper.dynamic(logininforEvent.getTenantId(), () -> insertLogininfor(logininfor));//支持注册时的动态多租户
    }

    private String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }
        return "[" + msg + "]";
    }

    /**
     * 新增系统登录日志
     *
     * @param logininforBo 访问日志对象
     */
    @Override
    public void insertLogininfor(SysLogininforBo logininforBo) {
        SysLogininfor logininfor = MapstructUtils.convert(logininforBo, SysLogininfor.class);
        logininfor.setLoginTime(new Date());
        this.save(logininfor);
    }

    /**
     * 根据logininforBo构建QueryWrapper查询条件
     *
     * @return 查询条件
     */
    private QueryWrapper buildQueryWrapper(SysLogininforBo logininforBo) {
        return query()
            .and(SYS_LOGININFOR.IPADDR.like(logininforBo.getIpaddr()))
            .and(SYS_LOGININFOR.STATUS.eq(logininforBo.getStatus()))
            .and(SYS_LOGININFOR.USER_NAME.like(logininforBo.getUserName()))
            .and(SYS_LOGININFOR.LOGIN_TIME.between(logininforBo.getParams().get("beginTime"), logininforBo.getParams().get("endTime")))
            .orderBy(SYS_LOGININFOR.INFO_ID.desc());
    }

    /**
     * 查询系统登录日志集合
     *
     * @param logininforBo 访问日志对象
     * @return 登录记录集合
     */
    @Override
    public List<SysLogininforVo> selectLogininforList(SysLogininforBo logininforBo) {
        QueryWrapper queryWrapper = buildQueryWrapper(logininforBo);
        return this.listAs(queryWrapper, SysLogininforVo.class);
    }

    /**
     * 分页查询登录日志列表
     *
     * @param logininforBo 登录日志
     * @return 登录日志集合
     */
    @Override
    public TableDataInfo<SysLogininforVo> selectPage(SysLogininforBo logininforBo) {
        QueryWrapper queryWrapper = buildQueryWrapper(logininforBo);
        Page<SysLogininforVo> page = this.pageAs(PageQuery.build(), queryWrapper, SysLogininforVo.class);
        return TableDataInfo.build(page);
    }

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果:true 删除成功，false 删除失败
     */
    @Override
    @Transactional
    public boolean deleteLogininforByIds(Long[] infoIds) {
        return this.removeByIds(Arrays.asList(infoIds));
    }

    /**
     * 清空系统登录日志
     * delete from sys_logininfor where info_id>0
     * 返回结果：true 删除成功，false 删除失败
     */
    @Override
    public boolean cleanLogininfor() {
        QueryWrapper queryWrapper = query().from(SYS_LOGININFOR).where(SYS_LOGININFOR.INFO_ID.gt(0));
        return this.remove(queryWrapper);
    }
}
