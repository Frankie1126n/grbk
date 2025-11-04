package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Blog Like Entity - 博客点赞表
 * 
 * @author Blog System
 */
@Data
@TableName("blog_like")
public class BlogLike implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 点赞ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 博客ID
     */
    private Long blogId;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 点赞时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
