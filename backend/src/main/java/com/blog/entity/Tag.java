package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Tag Entity - 标签表
 * 
 * @author Blog System
 */
@Data
@TableName("tag")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 标签ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 标签名称
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
