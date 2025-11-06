package com.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Message DTO - 私信数据传输对象
 *
 * @author Blog System
 */
@Data
public class MessageDTO {

    /**
     * 消息ID
     */
    private Long id;

    /**
     * 发送者ID
     */
    private Integer senderId;

    /**
     * 发送者用户名
     */
    private String senderUsername;

    /**
     * 发送者头像URL
     */
    private String senderAvatarUrl;

    /**
     * 接收者ID
     */
    private Integer receiverId;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 是否已读（0-未读，1-已读）
     */
    private Integer isRead;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}