package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.BlogLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * Blog Like Mapper - 博客点赞数据层
 * 
 * @author Blog System
 */
@Mapper
public interface BlogLikeMapper extends BaseMapper<BlogLike> {
}
