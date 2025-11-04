package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Blog Entity - 博客文章表
 * 
 * @author Blog System
 */
@Data
@TableName("blog")
public class Blog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容（HTML/Markdown）
     */
    private String content;

    /**
     * 文章摘要（列表页展示）
     */
    private String summary;

    /**
     * 封面图片URL（扩展字段）
     */
    private String coverImage;

    /**
     * 作者ID（外键关联user.id）
     */
    private Integer userId;

    /**
     * 分类ID（外键关联category.id）
     */
    private Integer categoryId;

    /**
     * 发布状态（1-已发布，0-草稿）
     */
    private Integer publishStatus;

    /**
     * 是否公开（1-公开，0-私密）
     */
    private Integer isPublic;

    /**
     * 阅读量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 收藏数
     */
    private Integer favoriteCount;

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
     * 删除时间（软删除）
     */
    private LocalDateTime deleteTime;

    /**
     * 是否删除（0-未删除，1-已删除）
     */
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;

    /**
     * 作者用户名（非数据库字段）
     */
    @TableField(exist = false)
    private String username;

    /**
     * 分类名称（非数据库字段）
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 标签列表（非数据库字段）
     */
    @TableField(exist = false)
    private java.util.List<Tag> tags;
}
