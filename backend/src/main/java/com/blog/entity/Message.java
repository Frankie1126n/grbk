package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Message Entity - 私信表
 *
 * @author Blog System
 */
@Data
@TableName("messages")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID（主键）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 发送者ID（外键关联user.id）
     */
    private Integer senderId;

    /**
     * 接收者ID（外键关联user.id）
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}