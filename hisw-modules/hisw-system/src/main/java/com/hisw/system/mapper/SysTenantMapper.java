package com.hisw.system.mapper;

import com.mybatisflex.core.BaseMapper;
import com.hisw.system.domain.SysTenant;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租户Mapper接口
 *
 * @author lejav
 */
@Mapper
public interface SysTenantMapper extends BaseMapper<SysTenant> {

}
