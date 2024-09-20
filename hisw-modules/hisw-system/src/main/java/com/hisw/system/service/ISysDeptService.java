package com.hisw.system.service;

import cn.hutool.core.lang.tree.Tree;
import com.hisw.common.orm.core.service.IBaseService;
import com.hisw.system.domain.SysDept;
import com.hisw.system.domain.bo.SysDeptBo;
import com.hisw.system.domain.vo.SysDeptVo;

import java.util.List;

/**
 * 部门管理 服务层
 *
 * @author lejav
 */
public interface ISysDeptService extends IBaseService<SysDept> {
    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    List<SysDeptVo> selectDeptList(SysDeptBo dept);

    /**
     * 查询部门树结构信息
     *
     * @param dept 部门信息
     * @return 部门树信息集合
     */
    List<Tree<Long>> selectDeptTreeList(SysDeptBo dept);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    List<Tree<Long>> buildDeptTreeSelect(List<SysDeptVo> depts);

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    List<Long> selectDeptListByRoleId(Long roleId);

    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    SysDeptVo selectDeptById(Long deptId);

    /**
     * 通过部门ID串查询部门
     *
     * @param deptIds 部门id串
     * @return 部门列表信息
     */
    List<SysDeptVo> selectDeptByIds(List<Long> deptIds);

    /**
     * 根据 部门名称 查询记录数量
     * @param deptName 部门名称
     * @return 记录数量
     */
    long selectDeptCountByName(String deptName);

    /**
     * 根据 部门名称 查询部门信息
     * @param deptName 部门名称
     * @return 部门信息
     */
    SysDeptVo selectDeptByName(String deptName);

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    int selectNormalChildrenDeptById(Long deptId);

    /**
     * 是否存在部门子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    boolean hasChildByDeptId(Long deptId);

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    boolean checkDeptNameUnique(SysDeptBo dept);

    /**
     * 校验部门是否有数据权限
     *
     * @param deptId 部门id
     */
    void checkDeptDataScope(Long deptId);

    /**
     * 新增保存部门信息
     *
     * @param deptBo 部门信息
     * @return true 操作成功，false 操作失败
     */
    boolean insertDept(SysDeptBo deptBo);

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    boolean updateDept(SysDeptBo dept);

    /**
     * 删除部门管理信息
     *
     * @param deptId 部门ID
     * @return 结果
     */
    boolean deleteDeptById(Long deptId);
}
