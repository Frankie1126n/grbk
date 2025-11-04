package com.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.BlogTag;
import com.blog.entity.Tag;
import com.blog.exception.BusinessException;
import com.blog.mapper.BlogTagMapper;
import com.blog.mapper.TagMapper;
import com.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Tag Service Implementation
 * 
 * @author Blog System
 */
@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private BlogTagMapper blogTagMapper;

    @Autowired
    private com.blog.mapper.BlogMapper blogMapper;

    @Override
    public List<Tag> getTagList() {
        return tagMapper.selectTagWithCount();
    }

    @Override
    public void addTag(String name) {
        // 检查标签名是否已存在
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, name);
        if (tagMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "标签名称已存在");
        }

        Tag tag = new Tag();
        tag.setName(name);
        tagMapper.insert(tag);
    }

    @Override
    public void updateTag(Integer id, String name) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException(404, "标签不存在");
        }

        // 检查新名称是否与其他标签重复
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Tag::getName, name);
        wrapper.ne(Tag::getId, id);
        if (tagMapper.selectCount(wrapper) > 0) {
            throw new BusinessException(400, "标签名称已存在");
        }

        tag.setName(name);
        tagMapper.updateById(tag);
    }

    @Override
    public void deleteTag(Integer id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException(404, "标签不存在");
        }

        // 检查是否有未删除的文章使用该标签
        // 获取使用该标签的所有blog_id
        LambdaQueryWrapper<BlogTag> blogTagWrapper = new LambdaQueryWrapper<>();
        blogTagWrapper.eq(BlogTag::getTagId, id);
        List<BlogTag> blogTags = blogTagMapper.selectList(blogTagWrapper);
        
        if (!blogTags.isEmpty()) {
            // 检查这些blog中有多少是未删除的
            List<Long> blogIds = blogTags.stream()
                .map(BlogTag::getBlogId)
                .collect(java.util.stream.Collectors.toList());
            
            LambdaQueryWrapper<com.blog.entity.Blog> blogWrapper = new LambdaQueryWrapper<>();
            blogWrapper.in(com.blog.entity.Blog::getId, blogIds);
            blogWrapper.eq(com.blog.entity.Blog::getIsDeleted, 0);
            Long count = blogMapper.selectCount(blogWrapper);
            
            if (count > 0) {
                throw new BusinessException(400, "该标签被" + count + "篇文章使用，无法删除");
            }
        }

        tagMapper.deleteById(id);
    }
}
