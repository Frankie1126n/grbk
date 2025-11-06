package com.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.entity.FriendRequest;
import com.blog.dto.FriendRequestDTO;

/**
 * Friend Request Service Interface - 好友申请服务接口
 *
 * @author Blog System
 */
public interface FriendRequestService {

    /**
     * 发送好友申请
     */
    boolean sendFriendRequest(Integer senderId, Integer receiverId, String message);

    /**
     * 处理好友申请
     */
    boolean handleFriendRequest(Integer requestId, Integer userId, boolean accept);

    /**
     * 获取用户收到的好友申请列表
     */
    IPage<FriendRequest> getReceivedFriendRequests(Integer userId, Integer current, Integer size);

    /**
     * 获取用户发送的好友申请列表
     */
    IPage<FriendRequest> getSentFriendRequests(Integer userId, Integer current, Integer size);

    /**
     * 获取用户收到的好友申请列表（包含发送者信息）
     */
    IPage<FriendRequestDTO> getReceivedFriendRequestsWithSenderInfo(Integer userId, Integer current, Integer size);

    /**
     * 获取用户发送的好友申请列表（包含接收者信息）
     */
    IPage<FriendRequestDTO> getSentFriendRequestsWithReceiverInfo(Integer userId, Integer current, Integer size);

    /**
     * 获取用户未处理的好友申请数量
     */
    int getPendingFriendRequestCount(Integer userId);
}