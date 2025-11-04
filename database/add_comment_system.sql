-- Create comment table
CREATE TABLE IF NOT EXISTS `comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `blog_id` BIGINT NOT NULL COMMENT '文章ID',
  `user_id` INT NOT NULL COMMENT '评论用户ID',
  `parent_id` BIGINT NULL COMMENT '父评论ID（回复评论时使用）',
  `reply_to_user_id` INT NULL COMMENT '回复的目标用户ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `is_pinned` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶（0-否，1-是）',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除（0-否，1-是）',
  PRIMARY KEY (`id`),
  KEY `idx_blog_id` (`blog_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';
