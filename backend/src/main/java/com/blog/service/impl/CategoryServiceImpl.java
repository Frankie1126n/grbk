package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.Category;
import com.blog.exception.BusinessException;
import com.blog.mapper.BlogMapper;
import com.blog.mapper.CategoryMapper;
import com.blog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Category Service Implementation
 * 
 * @author Blog System
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BlogMapper blogMapper;

    @Override
    public List<Category> getCategoryList() {
        return categoryMapper.selectCategoryWithCount();
    }

    @Override
    public void addCategory(String name) {
        // 检查分类名是否已存在
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, name);
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "分类名称已存在");
        }

        Category category = new Category();
        category.setName(name);
        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(Integer id, String name) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }

        // 检查新名称是否与其他分类重复
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, name);
        wrapper.ne(Category::getId, id);
        if (categoryMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "分类名称已存在");
        }

        category.setName(name);
        categoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Integer id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }

        // 检查是否有文章使用该分类（只统计未删除的文章）
        LambdaQueryWrapper<com.blog.entity.Blog> blogWrapper = new LambdaQueryWrapper<>();
        blogWrapper.eq(com.blog.entity.Blog::getCategoryId, id);
        blogWrapper.eq(com.blog.entity.Blog::getIsDeleted, 0);
        Long count = blogMapper.selectCount(blogWrapper);
        if (count > 0) {
            throw new BusinessException(400, "该分类下有" + count + "篇文章，无法删除");
        }

        categoryMapper.deleteById(id);
    }
}
