package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.CommentDTO;
import com.blog.entity.Comment;
import com.blog.exception.BusinessException;
import com.blog.service.CommentService;
import com.blog.util.UserTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Comment Controller - 评论控制器
 * 
 * @author Blog System
 */
@Api(tags = "评论模块")
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserTokenUtil userTokenUtil;

    /**
     * 发布评论
     */
    @ApiOperation("发布评论")
    @PostMapping("/add")
    public Result<Long> addComment(@Validated @RequestBody CommentDTO commentDTO,
                                    HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Integer userId = userTokenUtil.getUserId(authorization);
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        Long commentId = commentService.addComment(commentDTO, userId);
        return Result.success("评论成功", commentId);
    }

    /**
     * 获取文章的评论列表
     */
    @ApiOperation("获取文章评论列表")
    @GetMapping("/list/{blogId}")
    public Result<List<Comment>> getCommentList(@PathVariable Long blogId) {
        List<Comment> comments = commentService.getCommentList(blogId);
        return Result.success(comments);
    }

    /**
     * 删除评论
     */
    @ApiOperation("删除评论")
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id,
                                      HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Integer userId = userTokenUtil.getUserId(authorization);
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        commentService.deleteComment(id, userId);
        return Result.success("删除成功", null);
    }

    /**
     * 置顶/取消置顶评论
     */
    @ApiOperation("置顶/取消置顶评论")
    @PutMapping("/pin/{id}")
    public Result<Void> pinComment(@PathVariable Long id,
                                   @ApiParam("是否置顶") @RequestParam Integer isPinned,
                                   HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        Integer userId = userTokenUtil.getUserId(authorization);
        if (userId == null) {
            throw new BusinessException(401, "未登录或登录已过期");
        }
        commentService.pinComment(id, userId, isPinned);
        return Result.success(isPinned == 1 ? "置顶成功" : "取消置顶成功", null);
    }
}
