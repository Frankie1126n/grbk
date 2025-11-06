package com.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.entity.Message;
import com.blog.dto.MessageDTO;
import java.util.List;

/**
 * Message Service Interface - 私信服务接口
 *
 * @author Blog System
 */
public interface MessageService {

    /**
     * 发送私信
     */
    boolean sendMessage(Integer senderId, Integer receiverId, String content);

    /**
     * 获取用户之间的聊天记录
     */
    IPage<Message> getChatHistory(Integer userId1, Integer userId2, Integer current, Integer size);

    /**
     * 获取用户的未读消息列表
     */
    List<MessageDTO> getUnreadMessages(Integer userId);

    /**
     * 标记消息为已读
     */
    boolean markAsRead(Integer userId, Integer senderId);

    /**
     * 获取用户未读消息数量
     */
    int getUnreadMessageCount(Integer userId);
}