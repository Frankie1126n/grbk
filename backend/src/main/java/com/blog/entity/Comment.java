package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Comment Entity - 评论表
 * 
 * @author Blog System
 */
@Data
@TableName("comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 评论ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文章ID
     */
    private Long blogId;

    /**
     * 评论用户ID
     */
    private Integer userId;

    /**
     * 父评论ID（回复评论时使用，顶级评论为null）
     */
    private Long parentId;

    /**
     * 回复的目标用户ID
     */
    private Integer replyToUserId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 是否置顶（0-否，1-是）
     */
    private Integer isPinned;

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

    /**
     * 是否删除（0-否，1-是）
     */
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;

    /**
     * 评论用户名（非数据库字段）
     */
    @TableField(exist = false)
    private String username;

    /**
     * 评论用户头像（非数据库字段）
     */
    @TableField(exist = false)
    private String avatarUrl;

    /**
     * 回复目标用户名（非数据库字段）
     */
    @TableField(exist = false)
    private String replyToUsername;

    /**
     * 子评论列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<Comment> replies;

    /**
     * 回复数量（非数据库字段）
     */
    @TableField(exist = false)
    private Integer replyCount;
}
