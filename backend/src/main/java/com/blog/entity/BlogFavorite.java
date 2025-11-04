package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Blog Favorite Entity - 博客收藏表
 * 
 * @author Blog System
 */
@Data
@TableName("blog_favorite")
public class BlogFavorite implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 收藏ID（主键）
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
     * 收藏时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
