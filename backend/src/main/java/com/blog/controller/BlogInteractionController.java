package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.common.Result;
import com.blog.entity.Blog;
import com.blog.service.BlogLikeService;
import com.blog.service.BlogFavoriteService;
import com.blog.util.UserTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Blog Interaction Controller - 博客互动控制器（点赞、收藏）
 * 
 * @author Blog System
 */
@Api(tags = "博客互动模块")
@RestController
@RequestMapping("/blog")
public class BlogInteractionController {
    @Autowired
    private UserTokenUtil userTokenUtil;
    @Autowired
    private BlogLikeService blogLikeService;

    @Autowired
    private BlogFavoriteService blogFavoriteService;

    /**
     * 点赞博客
     */
    @ApiOperation("点赞博客")
    @PostMapping("/{blogId}/like")
    public Result<Void> likeBlog(@PathVariable Long blogId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        blogLikeService.likeBlog(blogId, userId);
        return Result.success("点赞成功", null);
    }

    /**
     * 取消点赞
     */
    @ApiOperation("取消点赞")
    @DeleteMapping("/{blogId}/like")
    public Result<Void> unlikeBlog(@PathVariable Long blogId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        blogLikeService.unlikeBlog(blogId, userId);
        return Result.success("取消点赞成功", null);
    }

    /**
     * 检查是否已点赞
     */
    @ApiOperation("检查是否已点赞")
    @GetMapping("/{blogId}/like/status")
    public Result<Boolean> checkLikeStatus(@PathVariable Long blogId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        boolean hasLiked = blogLikeService.hasLiked(blogId, userId);
        return Result.success(hasLiked);
    }

    /**
     * 收藏博客
     */
    @ApiOperation("收藏博客")
    @PostMapping("/{blogId}/favorite")
    public Result<Void> favoriteBlog(@PathVariable Long blogId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        blogFavoriteService.favoriteBlog(blogId, userId);
        return Result.success("收藏成功", null);
    }

    /**
     * 取消收藏
     */
    @ApiOperation("取消收藏")
    @DeleteMapping("/{blogId}/favorite")
    public Result<Void> unfavoriteBlog(@PathVariable Long blogId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        blogFavoriteService.unfavoriteBlog(blogId, userId);
        return Result.success("取消收藏成功", null);
    }

    /**
     * 检查是否已收藏
     */
    @ApiOperation("检查是否已收藏")
    @GetMapping("/{blogId}/favorite/status")
    public Result<Boolean> checkFavoriteStatus(@PathVariable Long blogId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        boolean hasFavorited = blogFavoriteService.hasFavorited(blogId, userId);
        return Result.success(hasFavorited);
    }

    /**
     * 获取用户的收藏列表
     */
    @ApiOperation("获取用户的收藏列表")
    @GetMapping("/favorites")
    public Result<IPage<Blog>> getUserFavorites(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Integer userId = userTokenUtil.getUserId(token);;
        IPage<Blog> favoritePage = blogFavoriteService.getUserFavorites(userId, current, size);
        return Result.success(favoritePage);
    }
}
