package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Friend Request Entity - 好友申请表
 *
 * @author Blog System
 */
@Data
@TableName("friend_requests")
public class FriendRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 申请ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 发送者ID（外键关联user.id）
     */
    private Integer senderId;

    /**
     * 接收者ID（外键关联user.id）
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}