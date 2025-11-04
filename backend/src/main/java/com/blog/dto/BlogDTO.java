package com.blog.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * Blog Save/Update Request DTO
 * 
 * @author Blog System
 */
@Data
public class BlogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文章ID（更新时必填）
     */
    private Long id;

    /**
     * 文章标题
     */
    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200个字符")
    private String title;

    /**
     * 文章内容
     */
    @NotBlank(message = "文章内容不能为空")
    private String content;

    /**
     * 文章摘要
     */
    @Size(max = 500, message = "摘要长度不能超过500个字符")
    private String summary;

    /**
     * 封面图片URL
     */
    private String coverImage;

    /**
     * 分类ID
     */
    @NotNull(message = "分类ID不能为空")
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
     * 标签ID列表
     */
    private List<Integer> tagIds;
}
