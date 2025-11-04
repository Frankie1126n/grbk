package com.blog.service;

import com.blog.entity.Tag;

import java.util.List;

/**
 * Tag Service
 * 
 * @author Blog System
 */
public interface TagService {

    /**
     * 获取所有标签列表（含文章数量）
     * 
     * @return 标签列表
     */
    List<Tag> getTagList();

    /**
     * 新增标签
     * 
     * @param name 标签名称
     */
    void addTag(String name);

    /**
     * 更新标签
     * 
     * @param id 标签ID
     * @param name 标签名称
     */
    void updateTag(Integer id, String name);

    /**
     * 删除标签（检查是否被使用）
     * 
     * @param id 标签ID
     */
    void deleteTag(Integer id);
}
