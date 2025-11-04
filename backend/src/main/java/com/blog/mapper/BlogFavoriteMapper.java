package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.BlogFavorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * Blog Favorite Mapper - 博客收藏数据层
 * 
 * @author Blog System
 */
@Mapper
public interface BlogFavoriteMapper extends BaseMapper<BlogFavorite> {
}
