package com.blog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.blog.entity.User;
import java.util.List;

/**
 * Friendship Service Interface - 用户好友关系服务接口
 *
 * @author Blog System
 */
public interface FriendshipService {

    /**
     * 添加好友（双方确认后调用）
     */
    boolean addFriend(Long userId, Long friendId);

    /**
     * 删除好友
     */
    boolean deleteFriend(Long userId, Long friendId);

    /**
     * 获取好友列表
     */
    IPage<User> getFriendList(Long userId, Integer current, Integer size);

    /**
     * 检查两个用户是否是好友
     */
    boolean areFriends(Long userId, Long friendId);

    /**
     * 获取用户的所有好友ID
     */
    List<Long> getFriendIds(Long userId);
}