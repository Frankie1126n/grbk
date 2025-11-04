package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Category;
import com.blog.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Category Controller - 分类控制器
 * 
 * @author Blog System
 */
@Api(tags = "分类模块")
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类列表
     */
    @ApiOperation("获取分类列表")
    @GetMapping("/list")
    public Result<List<Category>> getCategoryList() {
        List<Category> categoryList = categoryService.getCategoryList();
        return Result.success(categoryList);
    }

    /**
     * 新增分类
     */
    @ApiOperation("新增分类")
    @PostMapping("/add")
    public Result<Void> addCategory(@RequestParam String name) {
        categoryService.addCategory(name);
        return Result.success("添加成功", null);
    }

    /**
     * 更新分类
     */
    @ApiOperation("更新分类")
    @PutMapping("/{id}")
    public Result<Void> updateCategory(@PathVariable Integer id, @RequestParam String name) {
        categoryService.updateCategory(id, name);
        return Result.success("更新成功", null);
    }

    /**
     * 删除分类
     */
    @ApiOperation("删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return Result.success("删除成功", null);
    }
}
