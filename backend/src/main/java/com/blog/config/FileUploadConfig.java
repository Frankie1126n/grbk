package com.blog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import lombok.Data;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * 文件上传配置
 */
@Configuration
@ConfigurationProperties(prefix = "file.upload")
@Data
public class FileUploadConfig {
    
    /**
     * 文件上传路径
     */
    private String path = "D:/javadm/grbk/uploads/";
    
    /**
     * 文件访问前缀
     */
    private String prefix = "/uploads/";
    
    /**
     * 允许的图片格式
     */
    private String[] imageFormats = {"jpg", "jpeg", "png", "gif", "bmp", "webp"};
    
    /**
     * 最大文件大小 (字节)
     */
    private long maxSize = 10 * 1024 * 1024; // 10MB
    
    @PostConstruct
    public void init() {
        // 确保上传目录存在
        File uploadDir = new File(path);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        // 创建子目录
        new File(path + "avatars/").mkdirs();
        new File(path + "covers/").mkdirs();
        new File(path + "content/").mkdirs();
        new File(path + "backgrounds/").mkdirs();
    }
    
    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
