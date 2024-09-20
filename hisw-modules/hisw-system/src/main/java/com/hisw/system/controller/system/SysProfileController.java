package com.hisw.system.controller.system;

import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import com.hisw.common.core.core.domain.R;
import com.hisw.common.core.core.domain.model.LoginUser;
import com.hisw.common.core.utils.StringUtils;
import com.hisw.common.core.utils.file.MimeTypeUtils;
import com.hisw.common.encrypt.annotation.ApiEncrypt;
import com.hisw.common.log.annotation.Log;
import com.hisw.common.log.enums.BusinessType;
import com.hisw.common.security.utils.LoginHelper;
import com.hisw.common.web.core.BaseController;
import com.hisw.system.domain.bo.SysUserBo;
import com.hisw.system.domain.bo.SysUserPasswordBo;
import com.hisw.system.domain.bo.SysUserProfileBo;
import com.hisw.system.domain.vo.AvatarVo;
import com.hisw.system.domain.vo.ProfileVo;
import com.hisw.system.domain.vo.SysOssVo;
import com.hisw.system.domain.vo.SysUserVo;
import com.hisw.system.service.ISysOssService;
import com.hisw.system.service.ISysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 个人信息 业务处理
 *
 * @author lejav
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController {

    @Resource
    private ISysUserService userService;
    @Resource
    private ISysOssService ossService;

    /**
     * 个人信息
     */
    @GetMapping
    public R<ProfileVo> profile() {
        LoginUser loginUser = LoginHelper.getLoginUser();
        SysUserVo user = userService.selectProfileUserById(loginUser.getUserId());
        ProfileVo profileVo = new ProfileVo();
        profileVo.setUser(user);
        profileVo.setRoleGroup(userService.selectUserRoleGroup(user.getUserName()));
        profileVo.setPostGroup(userService.selectUserPostGroup(user.getUserName()));
        return R.ok(profileVo);
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Void> updateProfile(@RequestBody SysUserProfileBo profile) {
        SysUserBo user = BeanUtil.toBean(profile, SysUserBo.class);
        String username = LoginHelper.getUsername();
        LoginUser loginUser = LoginHelper.getLoginUser();
        SysUserVo sysUser = userService.selectUserById(loginUser.getUserId());
        user.setUserName(sysUser.getUserName());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            return R.fail("修改用户'" + username + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            return R.fail("修改用户'" + username + "'失败，邮箱账号已存在");
        }
        user.setUserId(sysUser.getUserId());
        user.setVersion(sysUser.getVersion());
        user.setPassword(null);
        user.setDeptId(null);
        if (userService.updateUserProfile(user)) {
            return R.ok();
        }
        return R.fail("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码
     */
    @ApiEncrypt
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public R<Void> updatePwd(@Validated @RequestBody SysUserPasswordBo bo) {
        SysUserVo sysUser = userService.selectUserById(LoginHelper.getUserId());
        String password = sysUser.getPassword();
        if (!BCrypt.checkpw(bo.getOldPassword(), password)) {
            return R.fail("修改密码失败，旧密码错误");
        }
        if (BCrypt.checkpw(bo.getNewPassword(), password)) {
            return R.fail("新密码不能与旧密码相同");
        }
        SysUserBo sysUserBo = new SysUserBo();
        sysUserBo.setUserId(sysUser.getUserId());
        sysUserBo.setPassword(BCrypt.hashpw(bo.getNewPassword()));
        sysUserBo.setVersion(sysUser.getVersion());

        if (userService.resetPwd(sysUserBo)) {
            return R.ok();
        }
        return R.fail("修改密码异常，请联系管理员");
    }

    /**
     * 头像上传
     */
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<AvatarVo> avatar(@RequestPart("avatarfile") MultipartFile file) {
        if (!file.isEmpty()) {
            String extension = FileUtil.extName(file.getOriginalFilename());
            if (!StringUtils.equalsAnyIgnoreCase(extension, MimeTypeUtils.IMAGE_EXTENSION)) {
                return R.fail("文件格式不正确，请上传" + Arrays.toString(MimeTypeUtils.IMAGE_EXTENSION) + "格式");
            }
            SysOssVo oss = ossService.upload(file);
            String avatar = oss.getUrl();
            if (userService.updateUserAvatar(LoginHelper.getUserId(), oss.getOssId())) {
                AvatarVo avatarVo = new AvatarVo();
                avatarVo.setImgUrl(avatar);
                return R.ok(avatarVo);
            }
        }
        return R.fail("上传图片异常，请联系管理员");
    }
}
