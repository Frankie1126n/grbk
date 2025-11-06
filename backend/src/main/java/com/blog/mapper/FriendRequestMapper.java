package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.FriendRequest;
import org.apache.ibatis.annotations.Mapper;

/**
 * Friend Request Mapper - 好友申请Mapper接口
 *
 * @author Blog System
 */
@Mapper
public interface FriendRequestMapper extends BaseMapper<FriendRequest> {
}