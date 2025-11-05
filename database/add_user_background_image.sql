-- 为用户表添加背景图片字段
ALTER TABLE `user` 
ADD COLUMN `background_image_url` VARCHAR(255) DEFAULT NULL COMMENT '背景图片URL（扩展字段）' AFTER `avatar_url`;

-- 更新现有数据，默认为空
UPDATE `user` SET `background_image_url` = NULL WHERE `background_image_url` IS NULL;