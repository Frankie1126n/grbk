package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.common.Result;
import com.blog.entity.Message;
import com.blog.service.FriendshipService;
import com.blog.service.MessageService;
import com.blog.dto.MessageDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Message Controller - 私信控制器
 *
 * @author Blog System
 */
@Api(tags = "私信模块")
@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private FriendshipService friendshipService;

    /**
     * 发送私信
     */
    @ApiOperation("发送私信")
    @PostMapping("/send")
    public Result<Void> sendMessage(@RequestParam Integer receiverId,
                                   @RequestParam String content,
                                   HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");

        // 检查是否是好友
        if (!friendshipService.areFriends(Long.valueOf(userId), Long.valueOf(receiverId))) {
            return Result.error(400, "只能给好友发送私信");
        }

        boolean success = messageService.sendMessage(userId, receiverId, content);
        if (success) {
            return Result.success("消息发送成功", null);
        } else {
            return Result.error(500, "消息发送失败");
        }
    }

    /**
     * 获取聊天记录
     */
    @ApiOperation("获取聊天记录")
    @GetMapping("/history/{friendId}")
    public Result<IPage<Message>> getChatHistory(@PathVariable Integer friendId,
                                                @RequestParam(defaultValue = "1") Integer current,
                                                @RequestParam(defaultValue = "10") Integer size,
                                                HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");

        // 检查是否是好友
        if (!friendshipService.areFriends(Long.valueOf(userId), Long.valueOf(friendId))) {
            return Result.error(400, "只能查看好友的聊天记录");
        }

        IPage<Message> page = messageService.getChatHistory(userId, friendId, current, size);
        return Result.success(page);
    }

    /**
     * 标记消息为已读
     */
    @ApiOperation("标记消息为已读")
    @PostMapping("/mark-read/{senderId}")
    public Result<Void> markAsRead(@PathVariable Integer senderId,
                                  HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        // 即使没有消息被标记为已读，也认为操作成功
        messageService.markAsRead(userId, senderId);
        return Result.success("消息已标记为已读", null);
    }

    /**
     * 获取未读消息数量
     */
    @ApiOperation("获取未读消息数量")
    @GetMapping("/unread/count")
    public Result<Integer> getUnreadMessageCount(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        int count = messageService.getUnreadMessageCount(userId);
        return Result.success(count);
    }
}