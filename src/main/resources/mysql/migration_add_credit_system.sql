-- ============================================
-- 信誉分系统 - 数据库迁移脚本
-- 版本：v1.0
-- 日期：2026-03-09
-- 说明：添加用户信誉分体系，防止恶意预约、爽约
-- ============================================

-- 使用数据库
USE lab_reservation;

-- ============================================
-- 1. 创建用户信誉分表
-- ============================================
DROP TABLE IF EXISTS `user_credit`;
CREATE TABLE `user_credit` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '信誉 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `score` int NOT NULL DEFAULT '100' COMMENT '当前信誉分（初始 100 分，范围 0-150）',
  `max_score` int NOT NULL DEFAULT '100' COMMENT '历史最高分',
  `total_add_times` int NOT NULL DEFAULT '0' COMMENT '累计加分次数',
  `total_subtract_times` int NOT NULL DEFAULT '0' COMMENT '累计扣分次数',
  `continuous_on_time_count` int NOT NULL DEFAULT '0' COMMENT '连续准时使用次数',
  `last_change_time` datetime DEFAULT NULL COMMENT '最后一次变动时间',
  `level` int NOT NULL DEFAULT '3' COMMENT '信誉等级：0-差 (0-59), 1-中 (60-79), 2-良 (80-99), 3-优 (100-119), 4-极好 (120+)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注说明',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_score` (`score`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信誉分表';

-- ============================================
-- 2. 创建用户信誉分变动记录表
-- ============================================
DROP TABLE IF EXISTS `user_credit_log`;
CREATE TABLE `user_credit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录 ID',
  `user_id` bigint NOT NULL COMMENT '用户 ID',
  `score_before` int NOT NULL COMMENT '变动前分数',
  `score_after` int NOT NULL COMMENT '变动后分数',
  `change_score` int NOT NULL COMMENT '变动分数（正数为加分，负数为扣分）',
  `change_type` int NOT NULL COMMENT '变动类型：1-预约成功 2-准时使用 3-取消预约 4-爽约 5-管理员调整 6-其他',
  `related_id` bigint DEFAULT NULL COMMENT '关联业务 ID（如预约 ID）',
  `description` varchar(500) DEFAULT NULL COMMENT '变动说明',
  `operator` varchar(100) DEFAULT 'SYSTEM' COMMENT '操作人（系统自动或管理员手动）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_change_type` (`change_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信誉分变动记录表';

-- ============================================
-- 3. 初始化现有用户的信誉分数据
-- ============================================
-- 为所有现有用户初始化信誉分（默认 100 分）
INSERT INTO `user_credit` (`user_id`, `score`, `max_score`, `level`)
SELECT 
    id as user_id,
    100 as score,
    100 as max_score,
    3 as level
FROM `user`
ON DUPLICATE KEY UPDATE score = VALUES(score);

-- ============================================
-- 4. 查看信誉分规则说明
-- ============================================
SELECT '
====================================
信誉分规则说明
====================================

【基础规则】
- 初始分数：100 分
- 分数范围：0-150 分
- 信用等级：
  * 0-差 (0-59 分)：禁止预约
  * 1-中 (60-79 分)：限制预约（每周最多 2 次）
  * 2-良 (80-99 分)：正常预约（每周最多 5 次）
  * 3-优 (100-119 分)：正常预约（每周最多 10 次）
  * 4-极好 (120+ 分)：免审核快速通道

【加分规则】
+1 分：预约成功并通过审核
+2 分：准时使用实验室（完成签到）
+3 分：连续 3 次准时使用（额外奖励）
+5 分：连续 5 次准时使用（额外奖励）
+10 分：管理员手动奖励

【扣分规则】
-1 分：取消预约（开馆前 24 小时内取消）
-5 分：临时取消（开馆前 1 小时内取消）
-10 分：爽约（未按时使用且未取消）
-20 分：恶意破坏设备或违规操作

【恢复机制】
- 信誉分低于 60 分的用户，可通过参加实验室安全培训恢复 20 分（每月限 1 次）
====================================
' AS credit_rules;

-- ============================================
-- 5. 回滚脚本（如果需要删除信誉分表）
-- ============================================
-- DROP TABLE IF EXISTS `user_credit_log`;
-- DROP TABLE IF EXISTS `user_credit`;

-- ============================================
-- 执行说明：
-- 1. 先在测试环境验证
-- 2. 生产环境执行前务必备份数据
-- 3. 执行完成后，重启应用使配置生效
-- ============================================
