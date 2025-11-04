package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Blog;
import com.blog.entity.BlogFavorite;
import com.blog.exception.BusinessException;
import com.blog.mapper.BlogFavoriteMapper;
import com.blog.mapper.BlogMapper;
import com.blog.service.BlogFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Blog Favorite Service Implementation - 博客收藏服务实现
 * 
 * @author Blog System
 */
@Service
public class BlogFavoriteServiceImpl implements BlogFavoriteService {

    @Autowired
    private BlogFavoriteMapper blogFavoriteMapper;

    @Autowired
    private BlogMapper blogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void favoriteBlog(Long blogId, Integer userId) {
        // 检查博客是否存在
        Blog blog = blogMapper.selectById(blogId);
        if (blog == null) {
            throw new BusinessException(404, "博客不存在");
        }

        // 检查是否已收藏
        LambdaQueryWrapper<BlogFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogFavorite::getBlogId, blogId);
        wrapper.eq(BlogFavorite::getUserId, userId);
        
        if (blogFavoriteMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "您已经收藏过了");
        }

        // 添加收藏记录
        BlogFavorite blogFavorite = new BlogFavorite();
        blogFavorite.setBlogId(blogId);
        blogFavorite.setUserId(userId);
        blogFavoriteMapper.insert(blogFavorite);

        // 更新博客收藏数
        blog.setFavoriteCount((blog.getFavoriteCount() == null ? 0 : blog.getFavoriteCount()) + 1);
        blogMapper.updateById(blog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfavoriteBlog(Long blogId, Integer userId) {
        // 检查是否已收藏
        LambdaQueryWrapper<BlogFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogFavorite::getBlogId, blogId);
        wrapper.eq(BlogFavorite::getUserId, userId);
        
        BlogFavorite blogFavorite = blogFavoriteMapper.selectOne(wrapper);
        if (blogFavorite == null) {
            throw new BusinessException(400, "您还未收藏");
        }

        // 删除收藏记录
        blogFavoriteMapper.deleteById(blogFavorite.getId());

        // 更新博客收藏数
        Blog blog = blogMapper.selectById(blogId);
        if (blog != null && blog.getFavoriteCount() != null && blog.getFavoriteCount() > 0) {
            blog.setFavoriteCount(blog.getFavoriteCount() - 1);
            blogMapper.updateById(blog);
        }
    }

    @Override
    public boolean hasFavorited(Long blogId, Integer userId) {
        LambdaQueryWrapper<BlogFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogFavorite::getBlogId, blogId);
        wrapper.eq(BlogFavorite::getUserId, userId);
        return blogFavoriteMapper.selectCount(wrapper) > 0;
    }

    @Override
    public IPage<Blog> getUserFavorites(Integer userId, Integer current, Integer size) {
        Page<Blog> page = new Page<>(current, size);
        return blogMapper.selectUserFavoriteBlogs(page, userId);
    }
}
