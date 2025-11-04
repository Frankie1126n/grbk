package com.blog.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.dto.BlogDTO;
import com.blog.entity.Blog;
import com.blog.entity.BlogTag;
import com.blog.entity.User;
import com.blog.exception.BusinessException;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.BlogTagMapper;
import com.blog.mapper.UserMapper;
import com.blog.service.BlogService;
import com.blog.util.UserTokenUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Blog Service Implementation
 * 
 * @author Blog System
 */
@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private BlogTagMapper blogTagMapper;

    @Autowired
    private UserMapper userMapper;


    @Override
    public IPage<Blog> getBlogList(Integer current, Integer size, String title, Integer categoryId, Integer tagId, Integer userId, Integer currentUserId) {
        Page<Blog> page = new Page<>(current, size);
        // 检查是否是admin
        boolean isAdmin = currentUserId != null && isAdmin(currentUserId);
        // 只查询已发布的文章（管理员也不能看到别人的私密文章）
        return blogMapper.selectBlogPage(page, title, categoryId, tagId, userId, currentUserId, false, 1);
    }

    @Override
    public IPage<Blog> getMyPrivateBlogs(Integer current, Integer size, Integer userId) {
        Page<Blog> page = new Page<>(current, size);
        // 查询当前用户的私密文章（isPublic=0）
        return blogMapper.selectMyPrivateBlogs(page, userId);
    }
    @Override
    public Blog getBlogById(Long id, Integer currentUserId) {
        Blog blog = blogMapper.selectBlogDetailById(id);
        if (blog == null) {
            throw new BusinessException(404, "文章不存在");
        }
        
        // 隐私权限检查：私密文章只有作者可以查看（管理员也不能查看别人的私密文章）
        if (blog.getIsPublic() != null && blog.getIsPublic() == 0) {
            // 私密文章
            if (currentUserId == null) {
                // 未登录用户不能查看
                throw new BusinessException(403, "该文章为私密文章，请先登录");
            }
            // 检查是否为作者（仅作者可查看）
            boolean isOwner = blog.getUserId().equals(currentUserId);
            if (!isOwner) {
                throw new BusinessException(403, "该文章为私密文章，无权查看");
            }
        }
        
        // 增加阅读量
        increaseViewCount(id);
        
        return blog;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveBlog(BlogDTO blogDTO, Integer userId) {
        if (blogDTO.getId() == null) {
            // 新增
            Blog blog = new Blog();
            BeanUtils.copyProperties(blogDTO, blog);
            blog.setUserId(userId);
            blog.setViewCount(0);
            // 默认公开
            if (blog.getIsPublic() == null) {
                blog.setIsPublic(1);
            }
            blogMapper.insert(blog);
            
            // 保存标签关联
            if (blogDTO.getTagIds() != null && !blogDTO.getTagIds().isEmpty()) {
                for (Integer tagId : blogDTO.getTagIds()) {
                    BlogTag blogTag = new BlogTag();
                    blogTag.setBlogId(blog.getId());
                    blogTag.setTagId(tagId);
                    blogTagMapper.insert(blogTag);
                }
            }
        } else {
            // 更新
            Blog existBlog = blogMapper.selectById(blogDTO.getId());
            if (existBlog == null) {
                throw new BusinessException(404, "文章不存在");
            }
            
            // 权限检查：admin可以修改所有文章，其他用户只能修改自己的文章
            if (!isAdmin(userId) && !existBlog.getUserId().equals(userId)) {
                throw new BusinessException(403, "无权限修改此文章");
            }
            
            Blog blog = new Blog();
            BeanUtils.copyProperties(blogDTO, blog);
            blog.setUserId(userId);
            blogMapper.updateById(blog);
            
            // 删除旧的标签关联
            LambdaQueryWrapper<BlogTag> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(BlogTag::getBlogId, blog.getId());
            blogTagMapper.delete(wrapper);
            
            // 保存标签关联
            if (blogDTO.getTagIds() != null && !blogDTO.getTagIds().isEmpty()) {
                for (Integer tagId : blogDTO.getTagIds()) {
                    BlogTag blogTag = new BlogTag();
                    blogTag.setBlogId(blog.getId());
                    blogTag.setTagId(tagId);
                    blogTagMapper.insert(blogTag);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBlog(Long id, Integer userId) {
        Blog blog = blogMapper.selectById(id);
        if (blog == null) {
            throw new BusinessException(404, "文章不存在");
        }
        // 权限检查：admin可以删除所有文章，其他用户只能删除自己的文章
        if (!isAdmin(userId) && !blog.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权限删除此文章");
        }

        // 软删除：设置删除时间，MyBatis-Plus会自动将isDeleted设置为1
        blog.setDeleteTime(java.time.LocalDateTime.now());
        blogMapper.updateById(blog);
        
        // MyBatis-Plus的@TableLogic会自动处理软删除，不需要手动调用deleteById
        blogMapper.deleteById(id);
    }

    @Override
    public void increaseViewCount(Long id) {
        Blog blog = blogMapper.selectById(id);
        if (blog != null) {
            blog.setViewCount(blog.getViewCount() + 1);
            blogMapper.updateById(blog);
        }
    }

    /**
     * 判断是否为管理员
     */
    private boolean isAdmin(Integer userId) {
        User user = userMapper.selectById(userId);
        return user != null && "admin".equals(user.getRole());
    }

    @Override
    public IPage<Blog> getDeletedBlogList(Integer current, Integer size, Integer userId) {
        Page<Blog> page = new Page<>(current, size);
        
        // 使用自定义SQL查询已删除的数据，绕过@TableLogic
        return blogMapper.selectDeletedBlogPage(page, userId, isAdmin(userId) ? null : userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreBlog(Long id, Integer userId) {
        // 使用自定义SQL查询已删除的数据
        Blog blog = blogMapper.selectDeletedBlogById(id);
        if (blog == null) {
            throw new BusinessException(404, "文章不存在或未被删除");
        }
        
        // 权限检查
        if (!isAdmin(userId) && !blog.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权限恢复此文章");
        }
        
        // 恢复文章：直接更新is_deleted和delete_time
        blogMapper.restoreBlog(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void permanentDeleteBlog(Long id, Integer userId) {
        Blog blog = blogMapper.selectDeletedBlogById(id);
        if (blog == null) {
            throw new BusinessException(404, "文章不存在");
        }
        
        // 权限检查
        if (!isAdmin(userId) && !blog.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权限删除此文章");
        }
        
        // 物理删除文章
        blogMapper.permanentDeleteBlog(id);
        
        // 删除标签关联
        LambdaQueryWrapper<BlogTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BlogTag::getBlogId, id);
        blogTagMapper.delete(wrapper);
    }
}
