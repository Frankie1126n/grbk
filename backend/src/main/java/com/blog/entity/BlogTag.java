package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;

/**
 * BlogTag Entity - 文章-标签关联表
 * 
 * @author Blog System
 */
@Data
@TableName("blog_tag")
public class BlogTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 文章ID（外键关联blog.id）
     */
    private Long blogId;

    /**
     * 标签ID（外键关联tag.id）
     */
    private Integer tagId;
}
