package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Message;
import com.blog.entity.User;
import com.blog.mapper.MessageMapper;
import com.blog.mapper.UserMapper;
import com.blog.service.MessageService;
import com.blog.dto.MessageDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Message Service Implementation - 私信服务实现类
 *
 * @author Blog System
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean sendMessage(Integer senderId, Integer receiverId, String content) {
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setIsRead(0); // 未读

        return messageMapper.insert(message) > 0;
    }

    @Override
    public IPage<Message> getChatHistory(Integer userId1, Integer userId2, Integer current, Integer size) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq("sender_id", userId1).eq("receiver_id", userId2)
                        .or().eq("sender_id", userId2).eq("receiver_id", userId1))
                .orderByAsc("create_time");

        Page<Message> page = new Page<>(current, size);
        return messageMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<MessageDTO> getUnreadMessages(Integer userId) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver_id", userId)
                .eq("is_read", 0)
                .orderByDesc("create_time");

        List<Message> messages = messageMapper.selectList(queryWrapper);

        // 转换为DTO并获取发送者信息
        List<MessageDTO> messageDTOs = new ArrayList<>();
        for (Message message : messages) {
            MessageDTO messageDTO = new MessageDTO();
            BeanUtils.copyProperties(message, messageDTO);

            // 获取发送者信息
            User sender = userMapper.selectById(message.getSenderId());
            if (sender != null) {
                messageDTO.setSenderUsername(sender.getUsername());
                messageDTO.setSenderAvatarUrl(sender.getAvatarUrl());
            }

            messageDTOs.add(messageDTO);
        }

        return messageDTOs;
    }

    @Override
    public boolean markAsRead(Integer userId, Integer senderId) {
        // 标记从特定发送者发送给当前用户的所有未读消息为已读
        int result = messageMapper.markMessagesAsRead(userId, senderId);
        // 即使没有消息被标记为已读，也返回true，因为这不应该是错误
        return true;
    }

    @Override
    public int getUnreadMessageCount(Integer userId) {
        return messageMapper.getUnreadMessageCount(userId);
    }
}