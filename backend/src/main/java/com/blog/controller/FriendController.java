package com.blog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.common.Result;
import com.blog.entity.FriendRequest;
import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import com.blog.service.FriendRequestService;
import com.blog.service.FriendshipService;
import com.blog.dto.FriendRequestDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Friend Controller - 好友控制器
 *
 * @author Blog System
 */
@Api(tags = "好友模块")
@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private UserMapper userMapper;

    /**
     * 发送好友申请
     */
    @ApiOperation("发送好友申请")
    @PostMapping("/request")
    public Result<Void> sendFriendRequest(@RequestParam Integer receiverId,
                                          @RequestParam(required = false) String message,
                                          HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        
        // 不能给自己发送好友申请
        if (userId.equals(receiverId)) {
            return Result.error(400, "不能给自己发送好友申请");
        }

        boolean success = friendRequestService.sendFriendRequest(userId, receiverId, message);
        if (success) {
            return Result.success("好友申请发送成功", null);
        } else {
            return Result.error(400, "好友申请发送失败，可能已存在未处理的申请");
        }
    }

    /**
     * 处理好友申请
     */
    @ApiOperation("处理好友申请")
    @PostMapping("/request/{requestId}")
    public Result<Void> handleFriendRequest(@PathVariable Integer requestId,
                                            @RequestParam boolean accept,
                                            HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        
        boolean success = friendRequestService.handleFriendRequest(requestId, userId, accept);
        if (success) {
            if (accept) {
                return Result.success("已接受好友申请", null);
            } else {
                return Result.success("已拒绝好友申请", null);
            }
        } else {
            return Result.error(400, "处理好友申请失败");
        }
    }

    /**
     * 获取收到的好友申请列表
     */
    @ApiOperation("获取收到的好友申请列表")
    @GetMapping("/requests/received")
    public Result<IPage<FriendRequestDTO>> getReceivedFriendRequests(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        IPage<FriendRequestDTO> page = friendRequestService.getReceivedFriendRequestsWithSenderInfo(userId, current, size);
        return Result.success(page);
    }

    /**
     * 获取发送的好友申请列表
     */
    @ApiOperation("获取发送的好友申请列表")
    @GetMapping("/requests/sent")
    public Result<IPage<FriendRequest>> getSentFriendRequests(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        IPage<FriendRequest> page = friendRequestService.getSentFriendRequests(userId, current, size);
        return Result.success(page);
    }

    /**
     * 获取好友列表
     */
    @ApiOperation("获取好友列表")
    @GetMapping("/list")
    public Result<IPage<User>> getFriendList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        IPage<User> page = friendshipService.getFriendList(Long.valueOf(userId), current, size);
        return Result.success(page);
    }

    /**
     * 删除好友
     */
    @ApiOperation("删除好友")
    @DeleteMapping("/{friendId}")
    public Result<Void> deleteFriend(@PathVariable Integer friendId,
                                     HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        
        // 检查是否是管理员好友（不能删除）
        User friend = userMapper.selectById(friendId);
        if (friend != null && "admin".equals(friend.getRole())) {
            return Result.error(400, "不能删除管理员好友");
        }

        // 检查是否是管理员自己在操作（管理员不能删除任何人）
        User currentUser = userMapper.selectById(userId);
        if (currentUser != null && "admin".equals(currentUser.getRole())) {
            return Result.error(400, "管理员不能删除好友");
        }

        boolean success = friendshipService.deleteFriend(Long.valueOf(userId), Long.valueOf(friendId));
        if (success) {
            return Result.success("好友删除成功", null);
        } else {
            return Result.error(400, "好友删除失败");
        }
    }

    /**
     * 获取未处理的好友申请数量
     */
    @ApiOperation("获取未处理的好友申请数量")
    @GetMapping("/requests/pending/count")
    public Result<Integer> getPendingFriendRequestCount(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        int count = friendRequestService.getPendingFriendRequestCount(userId);
        return Result.success(count);
    }
}