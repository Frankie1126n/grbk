package com.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * Friend Request DTO - 好友申请数据传输对象
 *
 * @author Blog System
 */
@Data
public class FriendRequestDTO {

    /**
     * 申请ID
     */
    private Integer id;

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
     * 申请状态（0-待处理，1-已接受，2-已拒绝）
     */
    private Integer status;

    /**
     * 申请消息
     */
    private String message;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}