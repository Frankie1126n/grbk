package com.blog.service;

import com.blog.dto.CommentDTO;
import com.blog.entity.Comment;

import java.util.List;

/**
 * Comment Service
 * 
 * @author Blog System
 */
public interface CommentService {

    /**
     * 发布评论
     * 
     * @param commentDTO 评论数据
     * @param userId 当前用户ID
     * @return 评论ID
     */
    Long addComment(CommentDTO commentDTO, Integer userId);

    /**
     * 获取文章的评论列表（树形结构）
     * 
     * @param blogId 文章ID
     * @return 评论列表
     */
    List<Comment> getCommentList(Long blogId);

    /**
     * 删除评论
     * 
     * @param id 评论ID
     * @param userId 当前用户ID
     */
    void deleteComment(Long id, Integer userId);

    /**
     * 置顶/取消置顶评论
     * 
     * @param id 评论ID
     * @param userId 当前用户ID
     * @param isPinned 是否置顶（1-置顶，0-取消置顶）
     */
    void pinComment(Long id, Integer userId, Integer isPinned);
}
