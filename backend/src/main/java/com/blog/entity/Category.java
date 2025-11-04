package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Category Entity - 分类表
 * 
 * @author Blog System
 */
@Data
@TableName("category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 文章数量（非数据库字段）
     */
    @TableField(exist = false)
    private Integer blogCount;

    /**
     * 文章数量（前端展示用，与blogCount保持同步）
     */
    @TableField(exist = false)
    private Integer articleCount;
}
