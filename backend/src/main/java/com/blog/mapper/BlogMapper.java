package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.entity.Blog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Blog Mapper
 * 
 * @author Blog System
 */
@Mapper
public interface BlogMapper extends BaseMapper<Blog> {

    /**
     * 分页查询博客列表（含作者、分类信息）
     * 
     * @param page 分页对象
     * @param title 标题关键词
     * @param categoryId 分类ID
     * @param tagId 标签ID
     * @param userId 筛选用户ID
     * @param currentUserId 当前登录用户ID
     * @param isAdmin 是否为管理员
     * @param publishStatus 发布状态
     * @return 博客分页列表
     */
    IPage<Blog> selectBlogPage(Page<Blog> page,
                               @Param("title") String title,
                               @Param("categoryId") Integer categoryId,
                               @Param("tagId") Integer tagId,
                               @Param("userId") Integer userId,
                               @Param("currentUserId") Integer currentUserId,
                               @Param("isAdmin") boolean isAdmin,
                               @Param("publishStatus") Integer publishStatus);

    /**
     * 查询用户的私密文章
     * 
     * @param page 分页对象
     * @param userId 用户ID
     * @return 私密文章列表
     */
    IPage<Blog> selectMyPrivateBlogs(Page<Blog> page, @Param("userId") Integer userId);

    /**
     * 根据ID查询博客详情（含作者、分类、标签信息）
     * 
     * @param id 博客ID
     * @return 博客详情
     */
    Blog selectBlogDetailById(@Param("id") Long id);

    /**
     * 分页查询已删除的博客列表
     * 
     * @param page 分页对象
     * @param userId 用户ID
     * @param filterUserId 过滤用户ID（非admin时使用）
     * @return 已删除的博客分页列表
     */
    IPage<Blog> selectDeletedBlogPage(Page<Blog> page,
                                      @Param("userId") Integer userId,
                                      @Param("filterUserId") Integer filterUserId);

    /**
     * 根据ID查询已删除的博客
     * 
     * @param id 博客ID
     * @return 博客信息
     */
    Blog selectDeletedBlogById(@Param("id") Long id);

    /**
     * 恢复博客
     * 
     * @param id 博客ID
     */
    void restoreBlog(@Param("id") Long id);

    /**
     * 物理删除博客
     * 
     * @param id 博客ID
     */
    void permanentDeleteBlog(@Param("id") Long id);

    /**
     * 分页查询用户收藏的博客列表
     * 
     * @param page 分页对象
     * @param userId 用户ID
     * @return 收藏的博客分页列表
     */
    IPage<Blog> selectUserFavoriteBlogs(Page<Blog> page, @Param("userId") Integer userId);
}
