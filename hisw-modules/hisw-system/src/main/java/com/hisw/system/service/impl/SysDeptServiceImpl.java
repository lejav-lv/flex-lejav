package com.hisw.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import com.hisw.common.core.constant.CacheNames;
import com.hisw.common.core.constant.UserConstants;
import com.hisw.common.core.core.text.Convert;
import com.hisw.common.core.exception.ServiceException;
import com.hisw.common.core.utils.MapstructUtils;
import com.hisw.common.core.utils.SpringUtils;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.core.utils.TreeBuildUtils;
import com.hisw.common.orm.core.service.impl.BaseServiceImpl;
import com.hisw.common.security.utils.LoginHelper;
import com.hisw.system.domain.SysDept;
import com.hisw.system.domain.bo.SysDeptBo;
import com.hisw.system.domain.vo.SysDeptVo;
import com.hisw.system.domain.vo.SysRoleVo;
import com.hisw.system.mapper.SysDeptMapper;
import com.hisw.system.service.ISysDataScopeService;
import com.hisw.system.service.ISysDeptService;
import com.hisw.system.service.ISysRoleService;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.update.UpdateChain;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

import static com.hisw.system.domain.table.SysDeptTableDef.SYS_DEPT;
import static com.hisw.system.domain.table.SysRoleDeptTableDef.SYS_ROLE_DEPT;
import static com.mybatisflex.core.query.QueryMethods.select;

/**
 * 部门管理 服务实现
 *
 * @author lejav
 * @author lejav
 */
@RequiredArgsConstructor
@Service
public class SysDeptServiceImpl extends BaseServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    @Resource
    private SysDeptMapper deptMapper;
    @Resource
    private ISysRoleService sysRoleService;
    @Resource
    private ISysDataScopeService dataScopeService;

    @Override
    public QueryWrapper query() {
        return super.query().from(SYS_DEPT);
    }

    /**
     * 根据deptBo构建QueryWrapper查询条件
     *
     * @return 查询条件
     */
    private QueryWrapper buildQueryWrapper(SysDeptBo deptBo) {
        return super.buildBaseQueryWrapper()
            .and(SYS_DEPT.DEPT_ID.eq(deptBo.getDeptId()))
            .and(SYS_DEPT.PARENT_ID.eq(deptBo.getParentId()))
            .and(SYS_DEPT.DEPT_NAME.like(deptBo.getDeptName()))
            .and(SYS_DEPT.STATUS.eq(deptBo.getStatus()))
            .orderBy(SYS_DEPT.ANCESTORS.asc(), SYS_DEPT.PARENT_ID.asc(), SYS_DEPT.ORDER_NUM.asc());
    }

    /**
     * 查询部门管理数据
     *
     * @param deptBo 部门信息
     * @return 部门信息集合
     */
    @Override
    public List<SysDeptVo> selectDeptList(SysDeptBo deptBo) {
        QueryWrapper queryWrapper = dataScopeService.addCondition(buildQueryWrapper(deptBo));//数据权限条件过滤
        return this.listAs(queryWrapper, SysDeptVo.class);
    }

    /**
     * 查询部门树结构信息
     *
     * @param dept 部门信息
     * @return 部门树信息集合
     */
    @Override
    public List<Tree<Long>> selectDeptTreeList(SysDeptBo dept) {
        // 只查询未禁用部门
        dept.setStatus(UserConstants.DEPT_NORMAL);
        List<SysDeptVo> deptLists = SpringUtils.getAopProxy(this).selectDeptList(dept);
        return buildDeptTreeSelect(deptLists);
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param deptLists 部门列表
     * @return 下拉树结构列表
     */
    @Override
    public List<Tree<Long>> buildDeptTreeSelect(List<SysDeptVo> deptLists) {
        if (CollUtil.isEmpty(deptLists)) {
            return CollUtil.newArrayList();
        }
        return TreeBuildUtils.build(deptLists, (dept, tree) ->
            tree.setId(dept.getDeptId())
                .setParentId(dept.getParentId())
                .setName(dept.getDeptName())
                .setWeight(dept.getOrderNum()));
    }

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    @Override
    public List<Long> selectDeptListByRoleId(Long roleId) {
        SysRoleVo role = sysRoleService.selectRoleById(roleId);
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(SYS_DEPT.DEPT_ID)
            .from(SYS_DEPT.as("d"))
            .leftJoin(SYS_ROLE_DEPT).as("rd").on(SYS_ROLE_DEPT.DEPT_ID.eq(SYS_DEPT.DEPT_ID))
            .where(SYS_ROLE_DEPT.ROLE_ID.eq(roleId));
        //部门树选择项是否关联显示
        if (ObjectUtil.isNotNull(role.getDeptCheckStrictly()) && role.getDeptCheckStrictly().equals(true)) {
            queryWrapper.and(SYS_DEPT.DEPT_ID.notIn(select(SYS_DEPT.PARENT_ID).from(SYS_DEPT).innerJoin(SYS_ROLE_DEPT).on(SYS_ROLE_DEPT.DEPT_ID.eq(SYS_DEPT.DEPT_ID).and(SYS_ROLE_DEPT.ROLE_ID.eq(roleId)))));
        }
        queryWrapper.orderBy(SYS_DEPT.ANCESTORS.asc(), SYS_DEPT.PARENT_ID.asc(), SYS_DEPT.ORDER_NUM.asc());


        return this.listAs(queryWrapper, Long.class);
    }

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    @Cacheable(cacheNames = CacheNames.SYS_DEPT, key = "#deptId")
    @Override
    public SysDeptVo selectDeptById(Long deptId) {
        QueryWrapper queryWrapper = query().where(SYS_DEPT.DEPT_ID.eq(deptId));
        return this.getOneAs(queryWrapper, SysDeptVo.class);
    }

    /**
     * 通过部门ID串查询部门
     *
     * @param deptIds 部门id串
     * @return 部门列表信息
     */
    @Override
    public List<SysDeptVo> selectDeptByIds(List<Long> deptIds) {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(SYS_DEPT.DEPT_ID, SYS_DEPT.DEPT_NAME, SYS_DEPT.LEADER)
            .from(SYS_DEPT)
            .where(SYS_DEPT.STATUS.eq(UserConstants.DEPT_NORMAL))
            .and(SYS_DEPT.DEPT_ID.in(deptIds));
        return this.listAs(queryWrapper, SysDeptVo.class);
    }

    /**
     * 根据 部门名称 查询记录数量
     *
     * @param deptName 部门名称
     * @return 记录数量
     */
    @Override
    public long selectDeptCountByName(String deptName) {
        QueryWrapper queryWrapper = query().where(SYS_DEPT.DEPT_NAME.eq(deptName)).and(SYS_DEPT.STATUS.eq("0"));
        return this.count(queryWrapper);
    }

    /**
     * 根据 部门名称 查询部门信息
     *
     * @param deptName 部门名称
     * @return 部门信息
     */
    @Override
    public SysDeptVo selectDeptByName(String deptName) {
        QueryWrapper queryWrapper = query().where(SYS_DEPT.DEPT_NAME.eq(deptName)).and(SYS_DEPT.STATUS.eq("0"));
        return this.getOneAs(queryWrapper, SysDeptVo.class);
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    @Override
    public int selectNormalChildrenDeptById(Long deptId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(QueryMethods.count(SYS_DEPT.DEPT_ID))
            .from(SYS_DEPT)
            .where(SYS_DEPT.STATUS.eq("0"))
            .and(QueryMethods.findInSet(QueryMethods.number(deptId), SYS_DEPT.ANCESTORS).gt(0));

        return deptMapper.selectObjectByQueryAs(queryWrapper, Integer.class);
    }

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean hasChildByDeptId(Long deptId) {
        QueryWrapper queryWrapper = QueryWrapper.create()
            .select(QueryMethods.count(SYS_DEPT.DEPT_ID))
            .from(SYS_DEPT)
            .and(SYS_DEPT.PARENT_ID.eq(deptId));
        int result = deptMapper.selectObjectByQueryAs(queryWrapper, Integer.class);
        return result > 0;
    }


    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public boolean checkDeptNameUnique(SysDeptBo dept) {
        Long deptId = ObjectUtil.isNull(dept.getDeptId()) ? -1L : dept.getDeptId();

        QueryWrapper queryWrapper = query().where(SYS_DEPT.DEPT_NAME.eq(dept.getDeptName()));
        queryWrapper.and(SYS_DEPT.PARENT_ID.eq(dept.getParentId()));
        SysDeptVo info = this.getOneAs(queryWrapper, SysDeptVo.class);

        if (StringUtils.isNotNull(info) && info.getDeptId().longValue() != deptId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
    @Override
    public void checkDeptDataScope(Long deptId) {
        if (ObjectUtil.isNull(deptId)) {
            return;
        }
        if (LoginHelper.isSuperAdmin()) {
            return;
        }

        SysDeptBo dept = new SysDeptBo();
        dept.setDeptId(deptId);
        List<SysDeptVo> deptLists = SpringUtils.getAopProxy(this).selectDeptList(dept);
        if (ObjectUtil.isNull(deptLists)) {
            throw new ServiceException("没有权限访问部门数据！");
        }
    }

    /**
     * 新增保存部门信息
     *
     * @param deptBo 部门信息
     * @return true 操作成功，false 操作失败
     */
    @Override
    public boolean insertDept(SysDeptBo deptBo) {
        SysDeptVo info = selectDeptById(deptBo.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus())) {
            throw new ServiceException("部门停用，不允许新增");
        }
        SysDept dept = MapstructUtils.convert(deptBo, SysDept.class);
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        dept.setDelFlag(0);//0 代表存在
        return this.save(dept);
    }

    /**
     * 修改保存部门信息
     *
     * @param deptBo 部门信息
     * @return 结果:true 更新成功，false 更新失败
     */
    @Transactional
    @CacheEvict(cacheNames = CacheNames.SYS_DEPT, key = "#deptBo.deptId")
    @Override
    public boolean updateDept(SysDeptBo deptBo) {
        SysDept dept = MapstructUtils.convert(deptBo, SysDept.class);
        SysDeptVo newParentDept = selectDeptById(dept.getParentId());
        SysDeptVo oldDept = selectDeptById(dept.getDeptId());
        if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept)) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getDeptId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getDeptId(), newAncestors, oldAncestors);
        }
        boolean result = this.updateById(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus()) && StringUtils.isNotEmpty(dept.getAncestors())
            && !StringUtils.equals("0", dept.getAncestors())) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(dept);
        }
        return result;
    }

    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private void updateParentDeptStatusNormal(SysDept dept) {
        String ancestors = dept.getAncestors();
        Long[] deptIds = Convert.toLongArray(ancestors);

        UpdateChain.of(SysDept.class)
            .set(SysDept::getStatus, "0")
            .where(SysDept::getDeptId).in(deptIds)
            .update();
    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    @Transactional
    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        //select * from sys_dept where find_in_set(#{deptId}, ancestors)
        QueryWrapper queryWrapper = QueryWrapper.create()
            .from(SYS_DEPT)
            .where(QueryMethods.findInSet(QueryMethods.number(deptId), SYS_DEPT.ANCESTORS).gt(0));

        List<SysDeptVo> children = this.listAs(queryWrapper, SysDeptVo.class);

        for (SysDeptVo child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));

            UpdateChain.of(SysDept.class)
                .set(SysDept::getAncestors, child.getAncestors())
                .where(SysDept::getDeptId).eq(child.getDeptId())
                .update();
        }
    }

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean deleteDeptById(Long deptId) {
        return this.removeById(deptId);
    }

}
