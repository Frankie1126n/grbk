package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * User Entity - 用户表
 * 
 * @author Blog System
 */
@Data
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名（登录用）
     */
    private String username;

    /**
     * 密码（BCrypt加密）
     */
    private String password;

    /**
     * 邮箱（密码重置）
     */
    private String email;

    /**
     * 头像URL（扩展字段）
     */
    private String avatarUrl;

    /**
     * 用户状态（1-正常，0-禁用）
     */
    private Integer status;

    /**
     * 用户角色（admin-管理员，user-普通用户）
     */
    private String role;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
