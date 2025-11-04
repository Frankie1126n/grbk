package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Tag;
import com.blog.service.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Tag Controller - 标签控制器
 * 
 * @author Blog System
 */
@Api(tags = "标签模块")
@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 获取所有标签列表
     */
    @ApiOperation("获取标签列表")
    @GetMapping("/list")
    public Result<List<Tag>> getTagList() {
        List<Tag> tagList = tagService.getTagList();
        return Result.success(tagList);
    }

    /**
     * 新增标签
     */
    @ApiOperation("新增标签")
    @PostMapping("/add")
    public Result<Void> addTag(@RequestParam String name) {
        tagService.addTag(name);
        return Result.success("添加成功",null);
    }

    /**
     * 更新标签
     */
    @ApiOperation("更新标签")
    @PutMapping("/{id}")
    public Result<Void> updateTag(@PathVariable Integer id, @RequestParam String name) {
        tagService.updateTag(id, name);
        return Result.success("更新成功", null);
    }

    /**
     * 删除标签
     */
    @ApiOperation("删除标签")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTag(@PathVariable Integer id) {
        tagService.deleteTag(id);
        return Result.success("删除成功", null);
    }
}
