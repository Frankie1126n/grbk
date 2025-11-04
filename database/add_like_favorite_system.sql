-- 博客点赞收藏功能 SQL 脚本

-- 1. 为 blog 表添加点赞数和收藏数字段
ALTER TABLE blog
ADD COLUMN like_count INT DEFAULT 0 COMMENT '点赞数' AFTER view_count,
ADD COLUMN favorite_count INT DEFAULT 0 COMMENT '收藏数' AFTER like_count;

-- 2. 创建博客点赞表
CREATE TABLE IF NOT EXISTS blog_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '点赞ID',
    blog_id BIGINT NOT NULL COMMENT '博客ID',
    user_id INT NOT NULL COMMENT '用户ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    UNIQUE KEY uk_blog_user (blog_id, user_id) COMMENT '同一用户只能点赞一次',
    KEY idx_blog_id (blog_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客点赞表';

-- 3. 创建博客收藏表
CREATE TABLE IF NOT EXISTS blog_favorite (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '收藏ID',
    blog_id BIGINT NOT NULL COMMENT '博客ID',
    user_id INT NOT NULL COMMENT '用户ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    UNIQUE KEY uk_blog_user (blog_id, user_id) COMMENT '同一用户只能收藏一次',
    KEY idx_blog_id (blog_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='博客收藏表';
