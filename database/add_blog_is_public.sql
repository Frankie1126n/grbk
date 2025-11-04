-- 为博客表添加是否公开字段
ALTER TABLE `blog` 
ADD COLUMN `is_public` TINYINT NOT NULL DEFAULT 1 COMMENT '是否公开（1-公开，0-私密）' AFTER `publish_status`;

-- 更新现有数据，默认全部公开
UPDATE `blog` SET `is_public` = 1 WHERE `is_public` IS NULL;
