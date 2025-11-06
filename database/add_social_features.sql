-- Add Social Features Tables for Personal Blog System
-- Tables: friend_requests, user_friend, messages

USE `blog_system`;

-- =====================================================
-- Table 6: friend_requests (好友申请表)
-- =====================================================
DROP TABLE IF EXISTS `friend_requests`;
CREATE TABLE `friend_requests` (
  `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '申请ID（主键）',
  `sender_id` INT(11) NOT NULL COMMENT '发送者ID（外键关联user.id）',
  `receiver_id` INT(11) NOT NULL COMMENT '接收者ID（外键关联user.id）',
  `status` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '申请状态（0-待处理，1-已接受，2-已拒绝）',
  `message` VARCHAR(255) DEFAULT NULL COMMENT '申请消息',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_sender_id` (`sender_id`) COMMENT '发送者ID索引',
  KEY `idx_receiver_id` (`receiver_id`) COMMENT '接收者ID索引',
  KEY `idx_status` (`status`) COMMENT '状态索引',
  CONSTRAINT `fk_friend_requests_sender` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_friend_requests_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='好友申请表';

-- =====================================================
-- Table 7: user_friend (用户好友关系表)
-- =====================================================
DROP TABLE IF EXISTS `user_friend`;
CREATE TABLE `user_friend` (
  `user_id` BIGINT NOT NULL COMMENT '用户ID（发起方）',
  `friend_id` BIGINT NOT NULL COMMENT '好友ID（被添加方）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '关系状态：1-正常好友，2-单向关注，3-拉黑，4-待验证',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关系建立时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '状态更新时间',
  PRIMARY KEY (`user_id`, `friend_id`),
  KEY `idx_friend_id` (`friend_id`),
  KEY `idx_user_status` (`user_id`, `status`),
  KEY `idx_friend_status` (`friend_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户好友关系表';

-- =====================================================
-- Table 8: messages (私信表)
-- =====================================================
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID（主键）',
  `sender_id` INT(11) NOT NULL COMMENT '发送者ID（外键关联user.id）',
  `receiver_id` INT(11) NOT NULL COMMENT '接收者ID（外键关联user.id）',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `is_read` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读（0-未读，1-已读）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_sender_id` (`sender_id`) COMMENT '发送者ID索引',
  KEY `idx_receiver_id` (`receiver_id`) COMMENT '接收者ID索引',
  KEY `idx_receiver_read` (`receiver_id`, `is_read`) COMMENT '接收者-已读状态索引',
  KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引',
  CONSTRAINT `fk_messages_sender` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_messages_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私信表';

-- =====================================================
-- Insert Default Admin Friendships for Existing Users
-- =====================================================
-- This will be handled in the application logic when users are created

-- =====================================================
-- Show table structures
-- =====================================================
SHOW TABLES;