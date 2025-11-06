package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.FriendRequest;
import com.blog.mapper.FriendRequestMapper;
import com.blog.service.FriendRequestService;
import com.blog.service.FriendshipService;
import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import com.blog.dto.FriendRequestDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Friend Request Service Implementation - 好友申请服务实现类
 *
 * @author Blog System
 */
@Service
public class FriendRequestServiceImpl extends ServiceImpl<FriendRequestMapper, FriendRequest> implements FriendRequestService {

    @Autowired
    private FriendRequestMapper friendRequestMapper;

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public boolean sendFriendRequest(Integer senderId, Integer receiverId, String message) {
        // 检查是否已经发送过申请
        QueryWrapper<FriendRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sender_id", senderId)
                .eq("receiver_id", receiverId)
                .eq("status", 0); // 只检查待处理的申请

        if (friendRequestMapper.selectCount(queryWrapper) > 0) {
            // 已经有未处理的申请
            return false;
        }

        // 创建好友申请
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSenderId(senderId);
        friendRequest.setReceiverId(receiverId);
        friendRequest.setMessage(message);
        friendRequest.setStatus(0); // 待处理

        return friendRequestMapper.insert(friendRequest) > 0;
    }

    @Override
    @Transactional
    public boolean handleFriendRequest(Integer requestId, Integer userId, boolean accept) {
        // 获取申请信息
        FriendRequest friendRequest = friendRequestMapper.selectById(requestId);
        
        // 检查申请是否存在且是当前用户收到的
        if (friendRequest == null || !friendRequest.getReceiverId().equals(userId)) {
            return false;
        }

        // 更新申请状态
        friendRequest.setStatus(accept ? 1 : 2); // 1-接受, 2-拒绝
        friendRequestMapper.updateById(friendRequest);

        // 如果接受，则添加好友关系
        if (accept) {
            friendshipService.addFriend(Long.valueOf(friendRequest.getSenderId()), Long.valueOf(friendRequest.getReceiverId()));
        }

        return true;
    }

    @Override
    public IPage<FriendRequest> getReceivedFriendRequests(Integer userId, Integer current, Integer size) {
        QueryWrapper<FriendRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver_id", userId);
        queryWrapper.eq("status",0);
        queryWrapper.orderByDesc("create_time");

        Page<FriendRequest> page = new Page<>(current, size);
        return friendRequestMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<FriendRequest> getSentFriendRequests(Integer userId, Integer current, Integer size) {
        QueryWrapper<FriendRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sender_id", userId);
        queryWrapper.orderByDesc("create_time");

        Page<FriendRequest> page = new Page<>(current, size);
        return friendRequestMapper.selectPage(page, queryWrapper);
    }

    @Override
    public IPage<FriendRequestDTO> getReceivedFriendRequestsWithSenderInfo(Integer userId, Integer current, Integer size) {
        // 先获取原始的申请列表
        IPage<FriendRequest> friendRequestPage = getReceivedFriendRequests(userId, current, size);
        
        // 转换为DTO并填充发送者信息
        List<FriendRequestDTO> friendRequestDTOs = new ArrayList<>();
        for (FriendRequest friendRequest : friendRequestPage.getRecords()) {
            FriendRequestDTO dto = new FriendRequestDTO();
            BeanUtils.copyProperties(friendRequest, dto);
            
            // 获取发送者信息
            User sender = userMapper.selectById(friendRequest.getSenderId());
            if (sender != null) {
                dto.setSenderUsername(sender.getUsername());
                dto.setSenderAvatarUrl(sender.getAvatarUrl());
            }
            
            friendRequestDTOs.add(dto);
        }
        
        // 创建新的分页对象
        IPage<FriendRequestDTO> dtoPage = new Page<>(friendRequestPage.getCurrent(), friendRequestPage.getSize(), friendRequestPage.getTotal());
        dtoPage.setRecords(friendRequestDTOs);
        
        return dtoPage;
    }

    @Override
    public IPage<FriendRequestDTO> getSentFriendRequestsWithReceiverInfo(Integer userId, Integer current, Integer size) {
        // 先获取原始的申请列表
        IPage<FriendRequest> friendRequestPage = getSentFriendRequests(userId, current, size);
        
        // 转换为DTO并填充接收者信息
        List<FriendRequestDTO> friendRequestDTOs = new ArrayList<>();
        for (FriendRequest friendRequest : friendRequestPage.getRecords()) {
            FriendRequestDTO dto = new FriendRequestDTO();
            BeanUtils.copyProperties(friendRequest, dto);
            
            // 获取接收者信息
            User receiver = userMapper.selectById(friendRequest.getReceiverId());
            if (receiver != null) {
                // 注意：对于发送的申请，我们需要设置接收者信息
                // 但DTO中没有receiverUsername和receiverAvatarUrl字段
                // 所以我们暂时不填充这些信息
            }
            
            friendRequestDTOs.add(dto);
        }
        
        // 创建新的分页对象
        IPage<FriendRequestDTO> dtoPage = new Page<>(friendRequestPage.getCurrent(), friendRequestPage.getSize(), friendRequestPage.getTotal());
        dtoPage.setRecords(friendRequestDTOs);
        
        return dtoPage;
    }

    @Override
    public int getPendingFriendRequestCount(Integer userId) {
        QueryWrapper<FriendRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver_id", userId);
        queryWrapper.eq("status", 0); // 待处理

        return friendRequestMapper.selectCount(queryWrapper).intValue();
    }
}