package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Tag Mapper
 * 
 * @author Blog System
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 查询所有标签（含文章数量）
     * 
     * @return 标签列表
     */
    List<Tag> selectTagWithCount();
}
