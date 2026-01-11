-- 为user表添加邮箱验证状态字段
ALTER TABLE `user` ADD COLUMN `email_verified` INT DEFAULT 0 COMMENT '邮箱验证状态：0-未验证 1-已验证';

-- 为已有用户设置默认值（如果邮箱不为空，则设为未验证状态）
UPDATE `user` SET `email_verified` = 0 WHERE `email` IS NOT NULL AND `email` != '';

-- 添加索引以提高查询性能
CREATE INDEX idx_email ON `user`(`email`);
CREATE INDEX idx_email_verified ON `user`(`email_verified`);
