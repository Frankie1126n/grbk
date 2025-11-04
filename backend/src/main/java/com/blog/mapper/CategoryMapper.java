package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * Category Mapper
 * 
 * @author Blog System
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 查询所有分类（含文章数量）
     * 
     * @return 分类列表
     */
    List<Category> selectCategoryWithCount();
}
