package com.blog.service;

import com.blog.entity.Category;

import java.util.List;

/**
 * Category Service
 * 
 * @author Blog System
 */
public interface CategoryService {

    /**
     * 获取所有分类列表（含文章数量）
     * 
     * @return 分类列表
     */
    List<Category> getCategoryList();

    /**
     * 新增分类
     * 
     * @param name 分类名称
     */
    void addCategory(String name);

    /**
     * 更新分类
     * 
     * @param id 分类ID
     * @param name 分类名称
     */
    void updateCategory(Integer id, String name);

    /**
     * 删除分类（检查是否被使用）
     * 
     * @param id 分类ID
     */
    void deleteCategory(Integer id);
}
