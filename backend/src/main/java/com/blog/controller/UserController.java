package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.common.Result;
import com.blog.dto.LoginDTO;
import com.blog.dto.RegisterDTO;
import com.blog.dto.UserInfoDTO;
import com.blog.dto.UserProfileDTO;
import com.blog.dto.UserManagementDTO;
import com.blog.entity.User;
import com.blog.service.UserService;
import com.blog.util.VerificationCodeStorage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Random;

/**
 * User Controller - 用户控制器
 * 
 * @author Blog System
 */
@Api(tags = "用户模块")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    /**
     * 用户登录
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<UserInfoDTO> login(@Validated @RequestBody LoginDTO loginDTO) {
        UserInfoDTO userInfo = userService.login(loginDTO);
        return Result.success("登录成功", userInfo);
    }

    /**
     * 用户注册
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Validated @RequestBody RegisterDTO registerDTO) {
        userService.register(registerDTO);
        return Result.success("注册成功，请登录",null);
    }

    /**
     * 获取当前登录用户信息
     */
    @ApiOperation("获取当前用户信息")
    @GetMapping("/current")
    public Result<UserInfoDTO> getCurrentUser(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        UserInfoDTO userInfo = userService.getCurrentUser(userId);
        return Result.success(userInfo);
    }

    /**
     * 根据ID获取用户公开信息
     */
    @ApiOperation("获取用户公开信息")
    @GetMapping("/{userId}")
    public Result<UserInfoDTO> getUserById(@PathVariable Integer userId) {
        UserInfoDTO userInfo = userService.getUserPublicInfo(userId);
        return Result.success(userInfo);
    }

    /**
     * 忘记密码（发送验证码）
     * 注: 实际项目需要集成邮件服务，这里仅做接口预留
     */
    @ApiOperation("忘记密码-发送验证码")
    @PostMapping("/forgot-password")
    public Result<Void> forgotPassword(@RequestParam String email, HttpServletRequest request) {
        // 生成6位随机验证码
        String code = String.format("%06d", new Random().nextInt(999999));
        // 发送邮件
        SimpleMailMessage mess = new SimpleMailMessage();
        mess.setFrom(username);
        mess.setTo(email);
        mess.setSubject("【个人博客重置密码】");
        mess.setText("您的验证码为：" + code + "，请在5分钟内使用。");
        mailSender.send(mess);
        // 将验证码存储到内存中
        VerificationCodeStorage.storeCode(email, code);
        return Result.success("验证码已发送至邮箱", null);
    }

    /**
     * 重置密码
     * 注: 实际项目需要验证验证码，这里仅做接口预留
     */
    @ApiOperation("重置密码")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@RequestParam String email,
                                      @RequestParam String code,
                                      @RequestParam String newPassword,
                                      HttpServletRequest request) {
        // 验证验证码是否正确
        if (!VerificationCodeStorage.validateCode(email, code)) {
            System.out.println("Code validation failed for email: " + email);
            return Result.error(400, "验证码错误或已过期");
        }
        
        // 验证密码强度
        if (newPassword.length() < 6 || newPassword.length() > 20) {
            return Result.error(400, "密码长度必须在6-20个字符之间");
        }
        // 重置密码
        try {
            userService.resetPassword(email, newPassword);
            // 清除已使用的验证码
            VerificationCodeStorage.removeCode(email);
            return Result.success("密码重置成功", null);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "密码重置失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户资料
     */
    @ApiOperation("更新用户资料")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@Validated @RequestBody UserProfileDTO profileDTO,
                                      HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        userService.updateProfile(userId, profileDTO);
        return Result.success("更新成功", null);
    }

    /**
     * 获取用户列表（仅管理员）
     */
    @ApiOperation("获取用户列表")
    @GetMapping("/list")
    public Result<IPage<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username) {
        IPage<User> userPage = userService.getUserList(current, size, username);
        return Result.success(userPage);
    }

    /**
     * 更新用户角色和状态（仅管理员）
     */
    @ApiOperation("更新用户角色和状态")
    @PutMapping("/management")
    public Result<Void> updateUserRoleAndStatus(@Validated @RequestBody UserManagementDTO dto) {
        userService.updateUserRoleAndStatus(dto);
        return Result.success("更新成功", null);
    }
}
