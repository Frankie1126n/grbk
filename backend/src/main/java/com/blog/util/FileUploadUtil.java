package com.blog.util;

import com.blog.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/**
 * 文件上传工具类
 */
public class FileUploadUtil {
    
    /**
     * 上传文件
     */
    public static String uploadFile(MultipartFile file, String uploadPath, String[] allowedFormats, long maxSize) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        
        // 检查文件大小
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小不能超过 " + (maxSize / 1024 / 1024) + "MB");
        }
        
        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException("文件名不能为空");
        }
        
        // 获取文件扩展名
        String extension = getFileExtension(originalFilename);
        
        // 检查文件格式
        if (!isAllowedFormat(extension, allowedFormats)) {
            throw new BusinessException("不支持的文件格式，仅支持: " + Arrays.toString(allowedFormats));
        }
        
        // 生成新文件名
        String newFileName = generateFileName(extension);
        
        // 创建上传目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        // 保存文件
        File destFile = new File(uploadPath + newFileName);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }
        
        return newFileName;
    }
    
    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }
    
    /**
     * 检查文件格式是否允许
     */
    public static boolean isAllowedFormat(String extension, String[] allowedFormats) {
        return Arrays.asList(allowedFormats).contains(extension.toLowerCase());
    }
    
    /**
     * 生成唯一文件名
     */
    public static String generateFileName(String extension) {
        return UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }
    
    /**
     * 删除文件
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }
}
