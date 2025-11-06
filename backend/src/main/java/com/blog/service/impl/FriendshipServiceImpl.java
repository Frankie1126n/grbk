package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.Friendship;
import com.blog.entity.User;
import com.blog.mapper.FriendshipMapper;
import com.blog.mapper.UserMapper;
import com.blog.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Friendship Service Implementation - 用户好友关系服务实现类
 *
 * @author Blog System
 */
@Service
public class FriendshipServiceImpl extends ServiceImpl<FriendshipMapper, Friendship> implements FriendshipService {

    @Autowired
    private FriendshipMapper friendshipMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean addFriend(Long userId, Long friendId) {
        // 检查是否已经是好友
        if (areFriends(userId, friendId)) {
            return false;
        }

        // 添加好友关系（双向）
        try {
            friendshipMapper.addFriendship(userId, friendId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        // 检查是否是好友
        if (!areFriends(userId, friendId)) {
            return false;
        }

        // 删除好友关系（双向删除）
        return friendshipMapper.deleteFriendship(userId, friendId) > 0;
    }

    @Override
    public IPage<User> getFriendList(Long userId, Integer current, Integer size) {
        // 获取好友ID列表
        List<Long> friendIds = friendshipMapper.getFriendIds(userId);

        if (friendIds.isEmpty()) {
            // 返回空页面
            return new Page<>(current, size);
        }

        // 查询好友信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", friendIds);
        queryWrapper.select("id", "username", "avatar_url", "role", "create_time");

        Page<User> page = new Page<>(current, size);
        return userMapper.selectPage(page, queryWrapper);
    }

    @Override
    public boolean areFriends(Long userId, Long friendId) {
        return friendshipMapper.checkFriendship(userId, friendId) > 0;
    }

    @Override
    public List<Long> getFriendIds(Long userId) {
        return friendshipMapper.getFriendIds(userId);
    }
}