package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.Blog;
import com.blog.entity.BlogLike;
import com.blog.exception.BusinessException;
import com.blog.mapper.BlogLikeMapper;
import com.blog.mapper.BlogMapper;
import com.blog.service.BlogLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Blog Like Service Implementation - 博客点赞服务实现
 * 
 * @author Blog System
 */
@Service
public class BlogLikeServiceImpl implements BlogLikeService {

    @Autowired
    private BlogLikeMapper blogLikeMapper;

    @Autowired
    private BlogMapper blogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void likeBlog(Long blogId, Integer userId) {
        // 检查博客是否存在
        Blog blog = blogMapper.selectById(blogId);
        if (blog == null) {
            throw new BusinessException(404, "博客不存在");
        }

        // 检查是否已点赞
        LambdaQueryWrapper<BlogLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogLike::getBlogId, blogId);
        wrapper.eq(BlogLike::getUserId, userId);
        
        if (blogLikeMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "您已经点赞过了");
        }

        // 添加点赞记录
        BlogLike blogLike = new BlogLike();
        blogLike.setBlogId(blogId);
        blogLike.setUserId(userId);
        blogLikeMapper.insert(blogLike);

        // 更新博客点赞数
        blog.setLikeCount((blog.getLikeCount() == null ? 0 : blog.getLikeCount()) + 1);
        blogMapper.updateById(blog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unlikeBlog(Long blogId, Integer userId) {
        // 检查是否已点赞
        LambdaQueryWrapper<BlogLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogLike::getBlogId, blogId);
        wrapper.eq(BlogLike::getUserId, userId);
        
        BlogLike blogLike = blogLikeMapper.selectOne(wrapper);
        if (blogLike == null) {
            throw new BusinessException(400, "您还未点赞");
        }

        // 删除点赞记录
        blogLikeMapper.deleteById(blogLike.getId());

        // 更新博客点赞数
        Blog blog = blogMapper.selectById(blogId);
        if (blog != null && blog.getLikeCount() != null && blog.getLikeCount() > 0) {
            blog.setLikeCount(blog.getLikeCount() - 1);
            blogMapper.updateById(blog);
        }
    }

    @Override
    public boolean hasLiked(Long blogId, Integer userId) {
        LambdaQueryWrapper<BlogLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogLike::getBlogId, blogId);
        wrapper.eq(BlogLike::getUserId, userId);
        return blogLikeMapper.selectCount(wrapper) > 0;
    }
}
