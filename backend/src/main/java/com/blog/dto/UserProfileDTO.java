package com.blog.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * User Profile Update Request DTO
 * 
 * @author Blog System
 */
@Data
public class UserProfileDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    private String username;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 旧密码（修改密码时必填）
     */
    private String oldPassword;

    /**
     * 新密码（修改密码时必填）
     */
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String newPassword;
}
