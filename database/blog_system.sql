-- Personal Blog System Database Design
-- Database: blog_system
-- Character Set: utf8mb4 (supports emoji)
-- Engine: InnoDB

-- Create database
CREATE DATABASE IF NOT EXISTS `blog_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `blog_system`;

-- =====================================================
-- Table 1: user (用户表)
-- =====================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键）',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名（登录用）',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `email` VARCHAR(100) NOT NULL COMMENT '邮箱（密码重置）',
  `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL（扩展字段）',
  `status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '用户状态（1-正常，0-禁用）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`) COMMENT '用户名唯一索引',
  UNIQUE KEY `uk_email` (`email`) COMMENT '邮箱唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- Table 2: category (分类表)
-- =====================================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '分类ID（主键）',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`) COMMENT '分类名称唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- =====================================================
-- Table 3: tag (标签表)
-- =====================================================
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '标签ID（主键）',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`) COMMENT '标签名称唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- =====================================================
-- Table 4: blog (博客文章表)
-- =====================================================
DROP TABLE IF EXISTS `blog`;
CREATE TABLE `blog` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '文章ID（主键）',
  `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
  `content` TEXT NOT NULL COMMENT '文章内容（HTML/Markdown）',
  `summary` VARCHAR(500) DEFAULT NULL COMMENT '文章摘要（列表页展示）',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片URL（扩展字段）',
  `user_id` INT(11) NOT NULL COMMENT '作者ID（外键关联user.id）',
  `category_id` INT(11) NOT NULL COMMENT '分类ID（外键关联category.id）',
  `publish_status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '发布状态（1-已发布，0-草稿）',
  `view_count` INT(11) NOT NULL DEFAULT 0 COMMENT '阅读量',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`) COMMENT '作者ID索引',
  KEY `idx_category_id` (`category_id`) COMMENT '分类ID索引',
  KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引（用于排序）',
  KEY `idx_publish_status` (`publish_status`) COMMENT '发布状态索引',
  CONSTRAINT `fk_blog_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_blog_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='博客文章表';

-- =====================================================
-- Table 5: blog_tag (文章-标签关联表)
-- =====================================================
DROP TABLE IF EXISTS `blog_tag`;
CREATE TABLE `blog_tag` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '关联ID（主键）',
  `blog_id` BIGINT(20) NOT NULL COMMENT '文章ID（外键关联blog.id）',
  `tag_id` INT(11) NOT NULL COMMENT '标签ID（外键关联tag.id）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_blog_tag` (`blog_id`, `tag_id`) COMMENT '文章-标签联合唯一索引',
  KEY `idx_blog_id` (`blog_id`) COMMENT '文章ID索引',
  KEY `idx_tag_id` (`tag_id`) COMMENT '标签ID索引',
  CONSTRAINT `fk_blog_tag_blog` FOREIGN KEY (`blog_id`) REFERENCES `blog` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_blog_tag_tag` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章-标签关联表';

-- =====================================================
-- Insert Sample Data
-- =====================================================

-- Insert sample categories
INSERT INTO `category` (`name`) VALUES 
('技术分享'),
('生活随笔'),
('读书笔记'),
('项目实战');

-- Insert sample tags
INSERT INTO `tag` (`name`) VALUES 
('Java'),
('Spring Boot'),
('Vue'),
('MySQL'),
('前端'),
('后端'),
('数据库'),
('架构设计');

-- Insert sample user (password: 123456 encrypted with BCrypt)
-- BCrypt hash for "123456": $2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH
INSERT INTO `user` (`username`, `password`, `email`, `status`) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'admin@blog.com', 1),
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 'test@blog.com', 1);

-- Insert sample blogs
INSERT INTO `blog` (`title`, `content`, `summary`, `user_id`, `category_id`, `publish_status`, `view_count`) VALUES 
('Spring Boot 快速入门指南', '# Spring Boot 快速入门\n\n这是一篇关于Spring Boot的入门教程...', 'Spring Boot是一个优秀的Java开发框架，本文介绍如何快速上手。', 1, 1, 1, 120),
('Vue2 组件开发最佳实践', '# Vue2 组件开发\n\n本文介绍Vue2组件开发的最佳实践...', '深入探讨Vue2组件开发的技巧和注意事项。', 1, 1, 1, 85),
('我的2024年读书清单', '# 2024年读书清单\n\n今年读了很多好书...', '分享2024年阅读的优质书籍和心得体会。', 2, 3, 1, 42);

-- Insert blog-tag associations
INSERT INTO `blog_tag` (`blog_id`, `tag_id`) VALUES 
(1, 1), -- Spring Boot快速入门 - Java
(1, 2), -- Spring Boot快速入门 - Spring Boot
(1, 6), -- Spring Boot快速入门 - 后端
(2, 3), -- Vue2组件开发 - Vue
(2, 5), -- Vue2组件开发 - 前端
(3, 1); -- 读书清单 - (no specific tech tag)

-- =====================================================
-- Show table structures
-- =====================================================
SHOW TABLES;
