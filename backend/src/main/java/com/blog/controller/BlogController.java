package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.common.Result;
import com.blog.config.JwtUtils;
import com.blog.dto.BlogDTO;
import com.blog.entity.Blog;
import com.blog.exception.BusinessException;
import com.blog.service.BlogService;
import com.blog.util.UserTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Blog Controller - 博客控制器
 * 
 * @author Blog System
 */
@Api(tags = "博客模块")
@RestController
@RequestMapping("/blog")
public class BlogController {


    @Autowired
    private BlogService blogService;

    @Autowired
    private UserTokenUtil userTokenUtil;

    /**
     * 分页查询博客列表
     */
    @ApiOperation("分页查询博客列表")
    @GetMapping("/list")
    public Result<IPage<Blog>> getBlogList(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("标题关键词") @RequestParam(required = false) String title,
            @ApiParam("分类ID") @RequestParam(required = false) Integer categoryId,
            @ApiParam("标签ID") @RequestParam(required = false) Integer tagId,
            @ApiParam("用户ID") @RequestParam(required = false) Integer userId,
            HttpServletRequest request) {
        // 获取当前登录用户ID（用于权限判断）
        Integer currentUserId = null;
        String token = request.getHeader("Authorization");
        if (token != null && !token.isEmpty()) {
            try {
                // 去除Bearer前缀
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
                currentUserId = userTokenUtil.getUserId(token);
            } catch (Exception e) {
                // Token无效或过期，忽略，按未登录处理
            }
        }
        IPage<Blog> blogPage = blogService.getBlogList(current, size, title, categoryId, tagId, userId, currentUserId);
//        IPage<Blog> info = blogService.getMyPrivateBlogs(current, size, userId);
//        // 合并两个分页结果
//        List<Blog> combinedRecords = new ArrayList<>();
//        combinedRecords.addAll(blogPage.getRecords());
//        combinedRecords.addAll(info.getRecords());
//
//        // 创建新的分页对象
//        Page<Blog> mergedPage = new Page<>();
//        mergedPage.setRecords(combinedRecords);
//        mergedPage.setTotal(blogPage.getTotal() + info.getTotal());
//        mergedPage.setSize(blogPage.getSize());
//        mergedPage.setCurrent(blogPage.getCurrent());

        return Result.success(blogPage);
    }

    /**
     * 获取当前用户的私密文章
     */
    @ApiOperation("获取当前用户的私密文章")
    @GetMapping("/my-private")
    public Result<IPage<Blog>> getMyPrivateBlogs(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Integer userId = userTokenUtil.getUserId(authorization);
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        IPage<Blog> blogPage = blogService.getMyPrivateBlogs(current, size, userId);
        return Result.success(blogPage);
    }
    
    /**
     * 根据ID查询博客详情
     */
    @ApiOperation("查询博客详情")
    @GetMapping("/{id}")
    public Result<Blog> getBlogById(@PathVariable Long id, HttpServletRequest request) {
        // 获取当前登录用户ID（用于权限判断）
        Integer currentUserId = null;
        String token = request.getHeader("Authorization");
        if (token != null && !token.isEmpty()) {
            try {
                // 去除Bearer前缀
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
                currentUserId = userTokenUtil.getUserId(token);
            } catch (Exception e) {
                // Token无效或过期，忽略，按未登录处理
            }
        }
        Blog blog = blogService.getBlogById(id, currentUserId);
        return Result.success(blog);
    }

    /**
     * 根据ID查询博客详情（不增加阅读量）
     */
    @ApiOperation("查询博客详情（不增加阅读量）")
    @GetMapping("/{id}/detail")
    public Result<Blog> getBlogDetailById(@PathVariable Long id, HttpServletRequest request) {
        // 获取当前登录用户ID（用于权限判断）
        Integer currentUserId = null;
        String token = request.getHeader("Authorization");
        if (token != null && !token.isEmpty()) {
            try {
                // 去除Bearer前缀
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
                currentUserId = userTokenUtil.getUserId(token);
            } catch (Exception e) {
                // Token无效或过期，忽略，按未登录处理
            }
        }
        Blog blog = blogService.getBlogByIdWithoutIncrementingViewCount(id, currentUserId);
        return Result.success(blog);
    }

    /**
     * 保存博客（新增或更新）
     */
    @ApiOperation("保存博客")
    @PostMapping("/save")
    public Result<Void> saveBlog(@Validated @RequestBody BlogDTO blogDTO,
                                 HttpServletRequest request) {
        String authorization = request.getHeader("Authorization").toString();
        System.out.println("Authorization: " + authorization);
        Integer userId = userTokenUtil.getUserId( authorization);
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        blogService.saveBlog(blogDTO, userId);
        return Result.success(blogDTO.getId() == null ? "发布成功" : "更新成功", null);
    }

    /**
     * 删除博客
     */
    @ApiOperation("删除博客")
    @DeleteMapping("/{id}")
    public Result<Void> deleteBlog(@PathVariable Long id,
                                   HttpServletRequest request) {
        String authorization = request.getHeader("Authorization").toString();
        System.out.println("Authorization: " + authorization);
        Integer userId = userTokenUtil.getUserId( authorization);
        blogService.deleteBlog(id, userId);
        return Result.success("删除成功", null);
    }

    /**
     * 分页查询已删除的博客列表
     */
    @ApiOperation("查询已删除的博客列表")
    @GetMapping("/deleted/list")
    public Result<IPage<Blog>> getDeletedBlogList(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        String authorization = request.getHeader("Authorization").toString();
        Integer userId = userTokenUtil.getUserId(authorization);
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        IPage<Blog> blogPage = blogService.getDeletedBlogList(current, size, userId);
        return Result.success(blogPage);
    }

    /**
     * 恢复已删除的博客
     */
    @ApiOperation("恢复博客")
    @PutMapping("/restore/{id}")
    public Result<Void> restoreBlog(@PathVariable Long id,
                                    HttpServletRequest request) {
        String authorization = request.getHeader("Authorization").toString();
        Integer userId = userTokenUtil.getUserId(authorization);
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        blogService.restoreBlog(id, userId);
        return Result.success("恢复成功", null);
    }

    /**
     * 彻底删除博客（物理删除）
     */
    @ApiOperation("彻底删除博客")
    @DeleteMapping("/permanent/{id}")
    public Result<Void> permanentDeleteBlog(@PathVariable Long id,
                                            HttpServletRequest request) {
        String authorization = request.getHeader("Authorization").toString();
        Integer userId = userTokenUtil.getUserId(authorization);
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        blogService.permanentDeleteBlog(id, userId);
        return Result.success("彻底删除成功", null);
    }
}
