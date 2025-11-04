package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Comment Mapper
 * 
 * @author Blog System
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 查询文章的评论列表（含用户信息）
     * 
     * @param blogId 文章ID
     * @return 评论列表
     */
    List<Comment> selectCommentListByBlogId(@Param("blogId") Long blogId);

    /**
     * 查询评论详情（含用户信息）
     * 
     * @param id 评论ID
     * @return 评论详情
     */
    Comment selectCommentDetailById(@Param("id") Long id);
}
