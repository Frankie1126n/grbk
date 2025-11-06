package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.config.JwtUtils;
import com.blog.dto.LoginDTO;
import com.blog.dto.RegisterDTO;
import com.blog.dto.UserInfoDTO;
import com.blog.dto.UserProfileDTO;
import com.blog.dto.UserManagementDTO;
import com.blog.entity.User;
import com.blog.exception.BusinessException;
import com.blog.mapper.UserMapper;
import com.blog.service.FriendshipService;
import com.blog.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * User Service Implementation
 * 
 * @author Blog System
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private FriendshipService friendshipService;

    @Override
    public UserInfoDTO login(LoginDTO loginDTO) {
        // 查询用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginDTO.getUsername());
        User user = userMapper.selectOne(wrapper);

        // 验证用户是否存在
        if (user == null) {
            throw new BusinessException(400, "用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(400, "用户名或密码错误");
        }

        // 验证用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException(400, "账号已被禁用");
        }

        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());

        // 构造返回对象
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(user, userInfoDTO);
        userInfoDTO.setToken(token);

        return userInfoDTO;
    }

    @Override
    public void register(RegisterDTO registerDTO) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, registerDTO.getUsername());
        if (userMapper.selectCount(usernameWrapper) > 0) {
            throw new BusinessException(400, "用户名已存在");
        }

        // 检查邮箱是否已存在
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, registerDTO.getEmail());
        if (userMapper.selectCount(emailWrapper) > 0) {
            throw new BusinessException(400, "邮箱已被注册");
        }

        // 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setStatus(1);
        user.setRole("user"); // 默认普通用户
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);
        
        // 自动添加管理员为好友
        LambdaQueryWrapper<User> adminWrapper = new LambdaQueryWrapper<>();
        adminWrapper.eq(User::getRole, "admin");
        List<User> adminUsers = userMapper.selectList(adminWrapper);
        
        for (User adminUser : adminUsers) {
            // 添加双向好友关系（用户 <-> 管理员）
            friendshipService.addFriend(Long.valueOf(user.getId()), Long.valueOf(adminUser.getId()));
        }
        
        // 同时让所有已存在的用户都添加这个新用户为好友（如果他们是管理员）
        LambdaQueryWrapper<User> allUsersWrapper = new LambdaQueryWrapper<>();
        allUsersWrapper.eq(User::getRole, "admin");
        List<User> allAdminUsers = userMapper.selectList(allUsersWrapper);
        
        for (User adminUser : allAdminUsers) {
            // 确保不是同一个用户
            if (!adminUser.getId().equals(user.getId())) {
                // 添加好友关系（管理员 -> 新用户）
                friendshipService.addFriend(Long.valueOf(adminUser.getId()), Long.valueOf(user.getId()));
            }
        }
    }

    @Override
    public UserInfoDTO getCurrentUser(Integer userId) {
        User user = getUserById(userId);
        
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(user, userInfoDTO);
        
        return userInfoDTO;
    }

    @Override
    public User getUserById(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return user;
    }

    @Override
    public UserInfoDTO getUserPublicInfo(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setId(user.getId());
        userInfoDTO.setUsername(user.getUsername());
        userInfoDTO.setEmail(user.getEmail());
        userInfoDTO.setAvatarUrl(user.getAvatarUrl());
        userInfoDTO.setBackgroundImageUrl(user.getBackgroundImageUrl());
        userInfoDTO.setRole(user.getRole());
        
        return userInfoDTO;
    }

    @Override
    public void updateProfile(Integer userId, UserProfileDTO profileDTO) {
        User user = getUserById(userId);
        
        // 更新用户名
        if (StringUtils.hasText(profileDTO.getUsername()) && !profileDTO.getUsername().equals(user.getUsername())) {
            // 检查用户名是否已被使用
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUsername, profileDTO.getUsername());
            wrapper.ne(User::getId, userId);
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(400, "用户名已存在");
            }
            user.setUsername(profileDTO.getUsername());
        }
        
        // 更新邮箱
        if (StringUtils.hasText(profileDTO.getEmail()) && !profileDTO.getEmail().equals(user.getEmail())) {
            // 检查邮箱是否已被使用
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getEmail, profileDTO.getEmail());
            wrapper.ne(User::getId, userId);
            if (userMapper.selectCount(wrapper) > 0) {
                throw new BusinessException(400, "邮箱已被注册");
            }
            user.setEmail(profileDTO.getEmail());
        }
        
        // 更新头像
        if (StringUtils.hasText(profileDTO.getAvatarUrl())) {
            user.setAvatarUrl(profileDTO.getAvatarUrl());
        }
        
        // 更新背景图片
        if (StringUtils.hasText(profileDTO.getBackgroundImageUrl())) {
            user.setBackgroundImageUrl(profileDTO.getBackgroundImageUrl());
        }
        
        // 更新密码
        if (StringUtils.hasText(profileDTO.getOldPassword()) && StringUtils.hasText(profileDTO.getNewPassword())) {
            // 验证旧密码
            if (!passwordEncoder.matches(profileDTO.getOldPassword(), user.getPassword())) {
                throw new BusinessException(400, "旧密码错误");
            }
            user.setPassword(passwordEncoder.encode(profileDTO.getNewPassword()));
        }
        
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public IPage<User> getUserList(Integer current, Integer size, String username) {
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        return userMapper.selectPage(page, wrapper);
    }

    @Override
    public void updateUserRoleAndStatus(UserManagementDTO dto) {
        User user = getUserById(dto.getUserId());
        
        if (dto.getRole() != null) {
            if (!"admin".equals(dto.getRole()) && !"user".equals(dto.getRole())) {
                throw new BusinessException(400, "无效的角色");
            }
            user.setRole(dto.getRole());
        }
        
        if (dto.getStatus() != null) {
            if (dto.getStatus() != 0 && dto.getStatus() != 1) {
                throw new BusinessException(400, "无效的状态");
            }
            user.setStatus(dto.getStatus());
        }
        
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    @Override
    public void resetPassword(String email, String newPassword) {
        // 根据邮箱查找用户
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        User user = userMapper.selectOne(wrapper);
        
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        // 验证密码强度
        if (newPassword == null || newPassword.length() < 6 || newPassword.length() > 20) {
            throw new BusinessException(400, "密码长度必须在6-20个字符之间");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
}