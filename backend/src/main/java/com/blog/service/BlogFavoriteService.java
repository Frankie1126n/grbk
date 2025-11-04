package com.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.entity.Blog;

/**
 * Blog Favorite Service - 博客收藏服务接口
 * 
 * @author Blog System
 */
public interface BlogFavoriteService {

    /**
     * 收藏博客
     * 
     * @param blogId 博客ID
     * @param userId 用户ID
     */
    void favoriteBlog(Long blogId, Integer userId);

    /**
     * 取消收藏
     * 
     * @param blogId 博客ID
     * @param userId 用户ID
     */
    void unfavoriteBlog(Long blogId, Integer userId);

    /**
     * 检查用户是否已收藏
     * 
     * @param blogId 博客ID
     * @param userId 用户ID
     * @return 是否已收藏
     */
    boolean hasFavorited(Long blogId, Integer userId);

    /**
     * 分页查询用户的收藏列表
     * 
     * @param userId 用户ID
     * @param current 当前页
     * @param size 每页大小
     * @return 收藏的博客列表
     */
    IPage<Blog> getUserFavorites(Integer userId, Integer current, Integer size);
}
