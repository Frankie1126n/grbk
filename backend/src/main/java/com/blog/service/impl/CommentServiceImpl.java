package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.dto.CommentDTO;
import com.blog.entity.Blog;
import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.exception.BusinessException;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.CommentMapper;
import com.blog.mapper.UserMapper;
import com.blog.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Comment Service Implementation
 * 
 * @author Blog System
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addComment(CommentDTO commentDTO, Integer userId) {
        // 验证文章是否存在
        Blog blog = blogMapper.selectById(commentDTO.getBlogId());
        if (blog == null || blog.getIsDeleted() == 1) {
            throw new BusinessException(404, "文章不存在");
        }

        // 如果是回复评论，验证父评论是否存在
        if (commentDTO.getParentId() != null) {
            Comment parentComment = commentMapper.selectById(commentDTO.getParentId());
            if (parentComment == null || parentComment.getIsDeleted() == 1) {
                throw new BusinessException(404, "父评论不存在");
            }
            // 验证父评论是否属于同一篇文章
            if (!parentComment.getBlogId().equals(commentDTO.getBlogId())) {
                throw new BusinessException(400, "评论不属于该文章");
            }
        }

        // 创建评论
        Comment comment = new Comment();
        comment.setBlogId(commentDTO.getBlogId());
        comment.setUserId(userId);
        comment.setParentId(commentDTO.getParentId());
        comment.setReplyToUserId(commentDTO.getReplyToUserId());
        comment.setContent(commentDTO.getContent());
        comment.setIsPinned(0);

        commentMapper.insert(comment);
        return comment.getId();
    }

    @Override
    public List<Comment> getCommentList(Long blogId) {
        // 获取所有评论
        List<Comment> allComments = commentMapper.selectCommentListByBlogId(blogId);
        
        // 构建树形结构
        return buildCommentTree(allComments);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(Long id, Integer userId) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new BusinessException(404, "评论不存在");
        }

        // 权限检查：文章作者可以删除文章下的所有评论，管理员可以删除任意评论，评论者可以删除自己的评论
        Blog blog = blogMapper.selectById(comment.getBlogId());
        if (blog == null) {
            throw new BusinessException(404, "文章不存在");
        }

        boolean isAdmin = isAdmin(userId);
        boolean isBlogAuthor = blog.getUserId().equals(userId);
        boolean isCommentAuthor = comment.getUserId().equals(userId);

        if (!isAdmin && !isBlogAuthor && !isCommentAuthor) {
            throw new BusinessException(403, "无权限删除此评论");
        }

        // 软删除评论（子评论也会被级联删除）
        commentMapper.deleteById(id);

        // 删除所有子评论
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, id);
        List<Comment> replies = commentMapper.selectList(wrapper);
        for (Comment reply : replies) {
            commentMapper.deleteById(reply.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pinComment(Long id, Integer userId, Integer isPinned) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null || comment.getIsDeleted() == 1) {
            throw new BusinessException(404, "评论不存在");
        }

        // 权限检查：只有文章作者可以置顶评论
        Blog blog = blogMapper.selectById(comment.getBlogId());
        if (blog == null) {
            throw new BusinessException(404, "文章不存在");
        }

        if (!blog.getUserId().equals(userId) && !isAdmin(userId)) {
            throw new BusinessException(403, "只有文章作者可以置顶评论");
        }

        // 只能置顶顶级评论
        if (comment.getParentId() != null) {
            throw new BusinessException(400, "只能置顶顶级评论");
        }

        comment.setIsPinned(isPinned);
        commentMapper.updateById(comment);
    }

    /**
     * 构建评论树形结构
     */
    private List<Comment> buildCommentTree(List<Comment> allComments) {
        // 分离顶级评论和回复评论
        List<Comment> topLevelComments = new ArrayList<>();
        Map<Long, List<Comment>> replyMap = new java.util.HashMap<>();
        
        // 手动构建 replyMap
        for (Comment comment : allComments) {
            if (comment.getParentId() != null) {
                replyMap.computeIfAbsent(comment.getParentId(), k -> new ArrayList<>()).add(comment);
            }
        }

        // 构建树形结构
        for (Comment comment : allComments) {
            if (comment.getParentId() == null) {
                // 顶级评论
                List<Comment> replies = replyMap.get(comment.getId());
                if (replies != null) {
                    comment.setReplies(replies);
                    comment.setReplyCount(replies.size());
                } else {
                    comment.setReplies(new ArrayList<>());
                    comment.setReplyCount(0);
                }
                topLevelComments.add(comment);
            }
        }

        return topLevelComments;
    }

    /**
     * 判断是否为管理员
     */
    private boolean isAdmin(Integer userId) {
        User user = userMapper.selectById(userId);
        return user != null && "admin".equals(user.getRole());
    }
}
