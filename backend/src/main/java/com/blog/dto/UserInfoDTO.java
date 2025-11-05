package com.blog.dto;

import lombok.Data;
import java.io.Serializable;

/**
 * User Info Response DTO
 * 
 * @author Blog System
 */
@Data
public class UserInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 背景图片URL
     */
    private String backgroundImageUrl;

    /**
     * 用户角色（admin-管理员，user-普通用户）
     */
    private String role;

    /**
     * JWT Token
     */
    private String token;
}