package com.hisw.system.service;

import com.hisw.common.orm.core.page.TableDataInfo;
import com.hisw.common.orm.core.service.IBaseService;
import com.hisw.system.domain.SysUser;
import com.hisw.system.domain.bo.SysUserBo;
import com.hisw.system.domain.vo.SysUserVo;

import java.util.List;

/**
 * 用户 业务层
 *
 * @author lejav
 */
public interface ISysUserService extends IBaseService<SysUser> {
    /**
     * 根据条件分页查询用户列表
     *
     * @param userBo 用户信息
     * @return 用户信息集合信息
     */
    List<SysUserVo> selectUserList(SysUserBo userBo);

    /**
     * 分页查询用户信息
     *
     * @param userBo 用户信息
     * @return 用户信息集合信息
     */
    TableDataInfo<SysUserVo> selectPage(SysUserBo userBo);

    /**
     * 根据条件分页查询已分配用户角色分页列表
     *
     * @param userBo 用户信息
     * @return 用户信息集合信息
     */
    TableDataInfo<SysUserVo> selectAllocatedPage(SysUserBo userBo);

    /**
     * 根据条件分页查询未分配用户角色分页列表
     *
     * @param userBo 用户信息
     * @return 用户信息集合信息
     */
    TableDataInfo<SysUserVo> selectUnallocatedPage(SysUserBo userBo);

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    SysUserVo selectUserByUserName(String userName);

    /**
     * 登录时通过租户编号、用户名查询用户（不走Mybatis-Flex租户插件）
     *
     * @param userName 用户名
     * @param tenantId 租户编号
     * @return 用户对象信息
     */
    SysUserVo selectTenantUserByUserName(Long tenantId, String userName);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    SysUserVo selectUserById(Long userId);

    /**
     * 通过用户ID串查询用户
     *
     * @param userIds 用户ID串
     * @param deptId  部门id
     * @return 用户列表信息
     */
    List<SysUserVo> selectUserByIds(List<Long> userIds, Long deptId);

    /**
     * 个人中心模块通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    SysUserVo selectProfileUserById(Long userId);


    /**
     * 根据用户ID查询用户所属角色组
     *
     * @param userName 用户名
     * @return 结果
     */
    String selectUserRoleGroup(String userName);

    /**
     * 根据用户ID查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    String selectUserPostGroup(String userName);

    /**
     * 校验用户名称是否唯一
     *
     * @param userBo 用户信息
     * @return 结果
     */
    boolean checkUserNameUnique(SysUserBo userBo);

    /**
     * 校验手机号码是否唯一
     *
     * @param userBo 用户信息
     * @return 结果
     */
    boolean checkPhoneUnique(SysUserBo userBo);

    /**
     * 校验email是否唯一
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean checkEmailUnique(SysUserBo user);

    /**
     * 校验用户是否允许操作
     *
     * @param userId 用户Id
     */
    void checkUserAllowed(Long userId);

    /**
     * 校验用户是否有数据权限
     *
     * @param userId 用户id
     */
    void checkUserDataScope(Long userId);

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    boolean checkDeptExistUser(Long deptId);

    /**
     * 新增用户信息
     *
     * @param userBo 用户信息
     * @return 结果:true 保存成功，false 保存失败
     */
    boolean insertUser(SysUserBo userBo);

    /**
     * 注册用户信息
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean registerUser(SysUserBo user, Long tenantId);

    /**
     * 修改用户信息
     *
     * @param userBo 用户信息
     * @return 结果：true 更新成功，false 更新失败
     */
    boolean updateUser(SysUserBo userBo);

    /**
     * 用户授权角色
     *
     * @param userId  用户ID
     * @param roleIds 角色组
     */
    void insertUserAuth(Long userId, Long[] roleIds);

    /**
     * 修改用户状态
     *
     * @param user 用户信息
     * @return 结果：true 操作成功，false 操作失败。
     */
    boolean updateUserStatus(SysUserBo user);

    /**
     * 修改用户基本信息
     *
     * @param user 用户信息
     * @return 结果
     */
    boolean updateUserProfile(SysUserBo user);

    /**
     * 修改用户头像
     *
     * @param userId 用户ID
     * @param avatar 头像地址
     * @return 结果
     */
    boolean updateUserAvatar(Long userId, Long avatar);

    /**
     * 重置用户密码
     *
     * @param user 用户信息
     * @return 结果：true 操作成功，false 操作失败。
     */
    boolean resetPwd(SysUserBo user);

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户ID
     * @return 结果：true 更新成功，false 更新失败。
     */
    boolean deleteUserByIds(Long[] userIds);

    /**
     * 通过部门id查询当前部门所有用户
     *
     * @param deptId 部门id
     * @return 用户vo列表
     */
    List<SysUserVo> selectUserListByDept(Long deptId);
}
