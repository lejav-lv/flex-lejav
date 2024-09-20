package com.hisw.system.mapper;

import com.hisw.system.domain.SysRole;
import com.hisw.system.domain.vo.SysRoleVo;
import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

import static com.hisw.system.domain.table.SysDeptTableDef.SYS_DEPT;
import static com.hisw.system.domain.table.SysRoleTableDef.SYS_ROLE;
import static com.hisw.system.domain.table.SysUserRoleTableDef.SYS_USER_ROLE;
import static com.hisw.system.domain.table.SysUserTableDef.SYS_USER;

/**
 * 角色表 数据层
 *
 * @author lejav
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据用户ID查询其拥有的角色列表
     * 为了避免在数据权限SysDataScopeServiceImpl引用产生循环引用问题，将该方法的实现由service转到mapper中（2023.11.21）
     *
     * @param userId 用户ID
     * @return 拥有的角色列表
     */
    default List<SysRoleVo> selectUserRolesByUserId(Long userId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(QueryMethods.distinct(SYS_ROLE.ALL_COLUMNS))
            .from(SYS_ROLE.as("r"))
            .leftJoin(SYS_USER_ROLE).as("ur").on(SYS_USER_ROLE.ROLE_ID.eq(SYS_ROLE.ROLE_ID))
            .leftJoin(SYS_USER).as("u").on(SYS_USER.USER_ID.eq(SYS_USER_ROLE.USER_ID))
            .leftJoin(SYS_DEPT).as("d").on(SYS_DEPT.DEPT_ID.eq(SYS_USER.DEPT_ID));
        queryWrapper.where(SYS_ROLE.DEL_FLAG.eq(0))
            .and(SYS_USER_ROLE.USER_ID.eq(userId));
        return selectListByQueryAs(queryWrapper, SysRoleVo.class);
    }
}
