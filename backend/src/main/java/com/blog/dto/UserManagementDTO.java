package com.blog.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * User Management DTO
 * 
 * @author Blog System
 */
@Data
public class UserManagementDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Integer userId;

    /**
     * 用户角色（admin-管理员，user-普通用户）
     */
    private String role;

    /**
     * 用户状态（1-正常，0-禁用）
     */
    private Integer status;
}
