package com.blog.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Friendship Entity - 用户好友关系表
 *
 * @author Blog System
 */
@Data
@TableName("user_friend")
public class Friendship implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（发起方）
     */
    @TableId(value = "user_id", type = IdType.NONE)
    private Long userId;

    /**
     * 好友ID（被添加方）
     */
    @TableField("friend_id")
    private Long friendId;

    /**
     * 关系状态：1-正常好友，2-单向关注，3-拉黑，4-待验证
     */
    private Integer status;

    /**
     * 关系建立时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 状态更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}