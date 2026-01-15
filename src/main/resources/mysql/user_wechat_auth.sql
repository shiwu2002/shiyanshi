-- 用户微信授权绑定表
-- 用于存储用户与微信等第三方平台的绑定关系
-- 支持多平台/多账号绑定（如微信小程序/公众号/企业微信等）

CREATE TABLE `user_wechat_auth` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '业务用户ID（user表主键）',
    `platform` VARCHAR(50) NOT NULL COMMENT '平台标识：mini_program-微信小程序, mp-微信公众号, enterprise_wechat-企业微信',
    `openid` VARCHAR(128) NOT NULL COMMENT '微信openid（平台下的唯一用户标识）',
    `unionid` VARCHAR(128) DEFAULT NULL COMMENT '微信unionid（统一身份标识，某些情况下可能为空）',
    `session_key` VARCHAR(128) DEFAULT NULL COMMENT '最近一次会话密钥（可选，仅用于调试或校验，不建议长期存储）',
    `bind_status` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '绑定状态：0-未绑定, 1-已绑定',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '最近登录时间（通过第三方登录成功后更新）',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除, 1-已删除',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_platform_openid` (`platform`, `openid`) COMMENT '平台+openid唯一索引',
    KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
    KEY `idx_platform_user` (`platform`, `user_id`) COMMENT '平台+用户ID索引',
    KEY `idx_unionid` (`unionid`) COMMENT 'unionid索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户微信授权绑定表';

-- 索引说明：
-- 1. uk_platform_openid: 确保同一平台下的openid唯一，防止重复绑定
-- 2. idx_user_id: 快速查询某个用户绑定了哪些第三方账号
-- 3. idx_platform_user: 快速查询某个用户在特定平台的绑定情况
-- 4. idx_unionid: 快速通过unionid查询跨平台用户身份

-- 使用示例：
-- 1. 插入微信小程序绑定记录
-- INSERT INTO user_wechat_auth (user_id, platform, openid, unionid, bind_status, last_login_time)
-- VALUES (1, 'mini_program', 'oxxx123456', 'oyyy789012', 1, NOW());

-- 2. 查询用户的微信小程序绑定信息
-- SELECT * FROM user_wechat_auth 
-- WHERE user_id = 1 AND platform = 'mini_program' AND deleted = 0;

-- 3. 通过openid查询绑定关系
-- SELECT * FROM user_wechat_auth 
-- WHERE platform = 'mini_program' AND openid = 'oxxx123456' AND deleted = 0;

-- 4. 更新最近登录时间
-- UPDATE user_wechat_auth 
-- SET last_login_time = NOW() 
-- WHERE platform = 'mini_program' AND openid = 'oxxx123456' AND deleted = 0;
