package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Message Mapper - 私信Mapper接口
 *
 * @author Blog System
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 将用户之间的所有消息标记为已读
     */
    @Update("UPDATE messages SET is_read = 1 WHERE receiver_id = #{receiverId} AND sender_id = #{senderId} AND is_read = 0")
    int markMessagesAsRead(@Param("receiverId") Integer receiverId, @Param("senderId") Integer senderId);

    /**
     * 获取用户未读消息数量
     */
    @Select("SELECT COUNT(*) FROM messages WHERE receiver_id = #{receiverId} AND is_read = 0")
    int getUnreadMessageCount(@Param("receiverId") Integer receiverId);
}