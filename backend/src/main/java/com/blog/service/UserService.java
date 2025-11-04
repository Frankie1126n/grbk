package com.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.dto.LoginDTO;
import com.blog.dto.RegisterDTO;
import com.blog.dto.UserInfoDTO;
import com.blog.dto.UserProfileDTO;
import com.blog.dto.UserManagementDTO;
import com.blog.entity.User;

/**
 * User Service
 * 
 * @author Blog System
 */
public interface UserService {

    /**
     * 用户登录
     * 
     * @param loginDTO 登录请求
     * @return 用户信息（含Token）
     */
    UserInfoDTO login(LoginDTO loginDTO);

    /**
     * 用户注册
     * 
     * @param registerDTO 注册请求
     */
    void register(RegisterDTO registerDTO);

    /**
     * 获取当前用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfoDTO getCurrentUser(Integer userId);

    /**
     * 根据ID查询用户
     * 
     * @param userId 用户ID
     * @return 用户实体
     */
    User getUserById(Integer userId);

    /**
     * 获取用户公开信息
     * 
     * @param userId 用户ID
     * @return 用户公开信息
     */
    UserInfoDTO getUserPublicInfo(Integer userId);

    /**
     * 更新用户资料
     * 
     * @param userId 用户ID
     * @param profileDTO 用户资料
     */
    void updateProfile(Integer userId, UserProfileDTO profileDTO);

    /**
     * 获取所有用户列表（分页）
     */
    IPage<User> getUserList(Integer current, Integer size, String username);

    /**
     * 更新用户角色和状态
     */
    void updateUserRoleAndStatus(UserManagementDTO dto);
}
