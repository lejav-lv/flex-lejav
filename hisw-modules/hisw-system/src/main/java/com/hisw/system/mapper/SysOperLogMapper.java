package com.hisw.system.mapper;


import com.mybatisflex.core.BaseMapper;
import com.hisw.system.domain.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 数据层
 *
 * @author lejav
 */
@Mapper
public interface SysOperLogMapper extends BaseMapper<SysOperLog> {

}
