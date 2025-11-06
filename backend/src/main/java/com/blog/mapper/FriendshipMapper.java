package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Friendship;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

/**
 * Friendship Mapper - 用户好友关系Mapper接口
 *
 * @author Blog System
 */
@Mapper
public interface FriendshipMapper extends BaseMapper<Friendship> {

    /**
     * 检查两个用户是否是好友（正常好友状态）
     */
    @Select("SELECT COUNT(*) FROM user_friend WHERE ((user_id = #{userId} AND friend_id = #{friendId}) OR (user_id = #{friendId} AND friend_id = #{userId})) AND status = 1")
    int checkFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);

    /**
     * 获取用户的好友ID列表（正常好友状态）
     */
    @Select("SELECT friend_id FROM user_friend WHERE user_id = #{userId} AND status = 1")
    List<Long> getFriendIds(@Param("userId") Long userId);

    /**
     * 删除好友关系（双向删除）
     */
    @Delete("DELETE FROM user_friend WHERE (user_id = #{userId} AND friend_id = #{friendId}) OR (user_id = #{friendId} AND friend_id = #{userId})")
    int deleteFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);
    
    /**
     * 添加好友关系（双向添加）
     */
    @Select("INSERT INTO user_friend (user_id, friend_id, status) VALUES (#{userId}, #{friendId}, 1), (#{friendId}, #{userId}, 1)")
    int addFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);
}