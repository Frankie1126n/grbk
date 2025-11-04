package com.blog.service;

/**
 * Blog Like Service - 博客点赞服务接口
 * 
 * @author Blog System
 */
public interface BlogLikeService {

    /**
     * 点赞博客
     * 
     * @param blogId 博客ID
     * @param userId 用户ID
     */
    void likeBlog(Long blogId, Integer userId);

    /**
     * 取消点赞
     * 
     * @param blogId 博客ID
     * @param userId 用户ID
     */
    void unlikeBlog(Long blogId, Integer userId);

    /**
     * 检查用户是否已点赞
     * 
     * @param blogId 博客ID
     * @param userId 用户ID
     * @return 是否已点赞
     */
    boolean hasLiked(Long blogId, Integer userId);
}
