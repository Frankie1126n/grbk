package com.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.dto.BlogDTO;
import com.blog.entity.Blog;

/**
 * Blog Service
 * 
 * @author Blog System
 */
public interface BlogService {

    /**
     * 分页查询博客列表
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param title 标题关键词
     * @param categoryId 分类ID
     * @param tagId 标签ID
     * @param userId 筛选用户ID
     * @param currentUserId 当前登录用户ID
     * @return 博客分页列表
     */
    IPage<Blog> getBlogList(Integer current, Integer size, String title, Integer categoryId, Integer tagId, Integer userId, Integer currentUserId);

    /**
     * 获取当前用户的私密文章
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param userId 用户ID
     * @return 私密文章分页列表
     */
    IPage<Blog> getMyPrivateBlogs(Integer current, Integer size, Integer userId);


    /**
     * 根据ID查询博客详情
     * 
     * @param id 博客ID
     * @param currentUserId 当前登录用户ID
     * @return 博客详情
     */
    Blog getBlogById(Long id, Integer currentUserId);

    /**
     * 保存博客（新增或更新）
     * 
     * @param blogDTO 博客数据
     * @param userId 当前用户ID
     */
    void saveBlog(BlogDTO blogDTO, Integer userId);

    /**
     * 删除博客
     * 
     * @param id 博客ID
     * @param userId 当前用户ID
     */
    void deleteBlog(Long id, Integer userId);

    /**
     * 增加阅读量
     * 
     * @param id 博客ID
     */
    void increaseViewCount(Long id);

    /**
     * 分页查询已删除的博客列表
     * 
     * @param current 当前页
     * @param size 每页大小
     * @param userId 当前用户ID（非admin只能看自己的）
     * @return 已删除的博客分页列表
     */
    IPage<Blog> getDeletedBlogList(Integer current, Integer size, Integer userId);

    /**
     * 恢复已删除的博客
     * 
     * @param id 博客ID
     * @param userId 当前用户ID
     */
    void restoreBlog(Long id, Integer userId);

    /**
     * 彻底删除博客（物理删除）
     * 
     * @param id 博客ID
     * @param userId 当前用户ID
     */
    void permanentDeleteBlog(Long id, Integer userId);
}
