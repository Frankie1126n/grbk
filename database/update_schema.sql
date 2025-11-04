-- Add role column to user table
ALTER TABLE `user` ADD COLUMN `role` VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '用户角色（admin-管理员，user-普通用户）' AFTER `status`;

-- Update existing admin user
UPDATE `user` SET `role` = 'admin' WHERE `username` = 'admin';

-- Add soft delete columns to blog table
ALTER TABLE `blog` ADD COLUMN `delete_time` DATETIME NULL COMMENT '删除时间' AFTER `update_time`;
ALTER TABLE `blog` ADD COLUMN `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除（0-未删除，1-已删除）' AFTER `delete_time`;

-- Create index for soft delete queries
CREATE INDEX `idx_is_deleted` ON `blog`(`is_deleted`);
