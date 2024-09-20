package com.hisw.system.service;

import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.orm.core.service.IBaseService;
import com.hisw.system.domain.SysLogininfor;
import com.hisw.system.domain.bo.SysLogininforBo;
import com.hisw.system.domain.vo.SysLogininforVo;

import java.util.List;

/**
 * 系统访问日志情况信息 服务层
 *
 * @author lejav
 */
public interface ISysLogininforService extends IBaseService<SysLogininfor> {
    /**
     * 新增系统登录日志
     *
     * @param logininfor 访问日志对象
     */
    void insertLogininfor(SysLogininforBo logininfor);

    /**
     * 查询系统登录日志集合
     *
     * @param logininfor 访问日志对象
     * @return 登录记录集合
     */
    List<SysLogininforVo> selectLogininforList(SysLogininforBo logininfor);

    /**
     * 分页查询登录日志列表
     *
     * @param logininforBo 登录日志
     * @return 登录日志集合
     */
    TableDataInfo<SysLogininforVo> selectPage(SysLogininforBo logininforBo);

    /**
     * 批量删除系统登录日志
     *
     * @param infoIds 需要删除的登录日志ID
     * @return 结果:true 删除成功，false 删除失败
     */
    boolean deleteLogininforByIds(Long[] infoIds);

    /**
     * 清空系统登录日志
     * 返回结果：true 删除成功，false 删除失败
     */
    boolean cleanLogininfor();
}
