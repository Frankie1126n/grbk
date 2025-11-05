package com.blog.controller;

import com.blog.common.Result;
import com.blog.config.FileUploadConfig;
import com.blog.util.FileUploadUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@Api(tags = "文件管理")
@RestController
@RequestMapping("/file")
public class FileController {
    
    @Autowired
    private FileUploadConfig fileUploadConfig;
    
    /**
     * 上传图片
     */
    @ApiOperation("上传图片")
    @PostMapping("/upload/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String fileName = FileUploadUtil.uploadFile(
            file, 
            fileUploadConfig.getPath(), 
            fileUploadConfig.getImageFormats(), 
            fileUploadConfig.getMaxSize()
        );
        
        String fileUrl = fileUploadConfig.getPrefix() + fileName;
        return Result.success("上传成功", fileUrl);
    }
    
    /**
     * 上传头像
     */
    @ApiOperation("上传头像")
    @PostMapping("/upload/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String fileName = FileUploadUtil.uploadFile(
            file, 
            fileUploadConfig.getPath() + "avatars/", 
            fileUploadConfig.getImageFormats(), 
            fileUploadConfig.getMaxSize()
        );
        
        String fileUrl = fileUploadConfig.getPrefix() + "avatars/" + fileName;
        return Result.success("上传成功", fileUrl);
    }
    
    /**
     * 上传文章封面
     */
    @ApiOperation("上传文章封面")
    @PostMapping("/upload/cover")
    public Result<String> uploadCover(@RequestParam("file") MultipartFile file) {
        String fileName = FileUploadUtil.uploadFile(
            file, 
            fileUploadConfig.getPath() + "covers/", 
            fileUploadConfig.getImageFormats(), 
            fileUploadConfig.getMaxSize()
        );
        
        String fileUrl = fileUploadConfig.getPrefix() + "covers/" + fileName;
        return Result.success("上传成功", fileUrl);
    }
    
    /**
     * 上传文章内容图片
     */
    @ApiOperation("上传文章内容图片")
    @PostMapping("/upload/content")
    public Result<String> uploadContentImage(@RequestParam("file") MultipartFile file) {
        String fileName = FileUploadUtil.uploadFile(
            file, 
            fileUploadConfig.getPath() + "content/", 
            fileUploadConfig.getImageFormats(), 
            fileUploadConfig.getMaxSize()
        );
        
        String fileUrl = fileUploadConfig.getPrefix() + "content/" + fileName;
        return Result.success("上传成功", fileUrl);
    }
    
    /**
     * 上传背景图片
     */
    @ApiOperation("上传背景图片")
    @PostMapping("/upload/background")
    public Result<String> uploadBackgroundImage(@RequestParam("file") MultipartFile file) {
        String fileName = FileUploadUtil.uploadFile(
            file, 
            fileUploadConfig.getPath() + "backgrounds/", 
            fileUploadConfig.getImageFormats(), 
            fileUploadConfig.getMaxSize()
        );
        
        String fileUrl = fileUploadConfig.getPrefix() + "backgrounds/" + fileName;
        return Result.success("上传成功", fileUrl);
    }
}
