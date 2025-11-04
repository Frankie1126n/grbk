package com.blog.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Comment DTO - 评论请求DTO
 * 
 * @author Blog System
 */
@Data
public class CommentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    @NotNull(message = "文章ID不能为空")
    private Long blogId;

    /**
     * 父评论ID（回复评论时使用，顶级评论传null）
     */
    private Long parentId;

    /**
     * 回复的目标用户ID
     */
    private Integer replyToUserId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    private String content;
}
