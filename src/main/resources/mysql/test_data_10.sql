-- 基于表结构的测试数据（每张表10条）
-- 说明：
-- 1) 显式写入主键ID，便于外键关联与测试的可控性
-- 2) 密码使用常见MD5("123456")：e10adc3949ba59abbe56e057f20f883e
-- 3) 预约时间段为字符串字段，示例采用"HH:mm-HH:mm"格式
-- 4) 注意唯一键约束：laboratory.lab_number、user.username

use lab_reservation;

-- 清理并重置自增（便于重复执行测试）
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE reservation;
TRUNCATE TABLE message;
TRUNCATE TABLE time_slot;
TRUNCATE TABLE laboratory;
TRUNCATE TABLE user;
SET FOREIGN_KEY_CHECKS = 1;

-- =========================
-- user（用户表）10条
-- =========================
INSERT INTO user (id, username, password, real_name, phone, email, user_type, college, major, student_id, teacher_id, status, avatar, email_verified)
VALUES
(1,  'admin', 'e10adc3949ba59abbe56e057f20f883e', '张三',  '13800010001', 'student01@example.com', 3, '信息工程学院', '计算机科学', '20260001', NULL, 1, NULL, 1),
(2,  'student02', 'e10adc3949ba59abbe56e057f20f883e', '李四',  '13800010002', 'student02@example.com', 0, '信息工程学院', '软件工程',   '20260002', NULL, 1, NULL, 0),
(3,  'student03', 'e10adc3949ba59abbe56e057f20f883e', '王五',  '13800010003', 'student03@example.com', 0, '理学院',       '物理学',     '20260003', NULL, 1, NULL, 0),
(4,  'student04', 'e10adc3949ba59abbe56e057f20f883e', '赵六',  '13800010004', 'student04@example.com', 0, '化学与材料',   '化学',       '20260004', NULL, 1, NULL, 1),
(5,  'student05', 'e10adc3949ba59abbe56e057f20f883e', '钱七',  '13800010005', 'student05@example.com', 0, '机械工程学院', '机械工程',   '20260005', NULL, 1, NULL, 0),
(6,  'teacher01', 'e10adc3949ba59abbe56e057f20f883e', '刘老师','13900020001', 'teacher01@example.com', 1, '信息工程学院', '计算机科学', NULL, 'T2026001', 1, NULL, 1),
(7,  'teacher02', 'e10adc3949ba59abbe56e057f20f883e', '孙老师','13900020002', 'teacher02@example.com', 1, '理学院',       '物理学',     NULL, 'T2026002', 1, NULL, 0),
(8,  'teacher03', 'e10adc3949ba59abbe56e057f20f883e', '周老师','13900020003', 'teacher03@example.com', 1, '化学与材料',   '化学',       NULL, 'T2026003', 1, NULL, 1),
(9,  'admin01',   'e10adc3949ba59abbe56e057f20f883e', '管理员甲','13900020009','admin01@example.com',   1, '信息工程学院', '计算机科学', NULL, 'A2026001', 1, NULL, 1),
(10, 'admin02',   'e10adc3949ba59abbe56e057f20f883e', '管理员乙','13900020010','admin02@example.com',   1, '理学院',       '物理学',     NULL, 'A2026002', 1, NULL, 1);

-- =========================
-- laboratory（实验室表）10条
-- =========================
INSERT INTO laboratory (id, lab_name, lab_number, location, capacity, equipment, description, image_url, building, floor, lab_type, manager, manager_phone, images, open_time, status)
VALUES
(1,  '计算机综合实验室A', 'LAB-COMP-001', '信息工程楼A区101', 30, '电脑,服务器,交换机', '用于计算机基础与网络实验', NULL, '信息工程楼', '1F', '计算机', '刘老师', '13900020001', NULL, '工作日 08:00-18:00', 1),
(2,  '软件工程实践室',   'LAB-SWE-002',  '信息工程楼A区102', 35, '电脑,投影仪',       '软件开发与项目实践',       NULL, '信息工程楼', '1F', '计算机', '刘老师', '13900020001', NULL, '工作日 08:00-18:00', 1),
(3,  '网络与安全实验室', 'LAB-NET-003',  '信息工程楼B区201', 25, '路由器,防火墙',       '网络攻防与安全实验',       NULL, '信息工程楼', '2F', '网络安全', '孙老师', '13900020002', NULL, '工作日 09:00-17:00', 1),
(4,  '物理基础实验室',   'LAB-PHY-004',  '理学院楼C区301',   40, '示波器,电源,传感器', '物理基础实验教学',         NULL, '理学院楼',   '3F', '物理',   '孙老师', '13900020002', NULL, '工作日 08:00-18:00', 1),
(5,  '化学分析实验室',   'LAB-CHE-005',  '化学楼D区401',     20, '光谱仪,离心机',       '化学分析与实验',           NULL, '化学楼',     '4F', '化学',   '周老师', '13900020003', NULL, '工作日 09:00-17:00', 1),
(6,  '材料力学实验室',   'LAB-MAT-006',  '材料楼E区501',     28, '万能试验机,硬度计',   '材料力学与测试',           NULL, '材料楼',     '5F', '材料',   '周老师', '13900020003', NULL, '工作日 08:00-18:00', 1),
(7,  '机械加工实验室',   'LAB-MECH-007', '机械楼F区101',     18, '车床,铣床',           '机械加工基础训练',         NULL, '机械楼',     '1F', '机械',   '管理员甲','13900020009', NULL,'工作日 08:00-18:00', 1),
(8,  '电子设计实验室',   'LAB-EE-008',   '信息工程楼B区202', 32, '电烙铁,示波器,电源',  '电子电路设计与调试',       NULL, '信息工程楼', '2F', '电子',   '刘老师', '13900020001', NULL, '工作日 08:00-18:00', 1),
(9,  '生物工程实验室',   'LAB-BIO-009',  '生物楼G区201',     22, '培养箱,显微镜',       '生物工程基础',             NULL, '生物楼',     '2F', '生物',   '管理员乙','13900020010', NULL,'工作日 09:00-17:00', 1),
(10, 'AI与数据实验室',   'LAB-AI-010',   '信息工程楼C区301', 50, 'GPU服务器,工作站',     'AI模型训练与数据分析',     NULL, '信息工程楼', '3F', '人工智能', '管理员甲','13900020009', NULL,'工作日 08:00-18:00', 1);

-- =========================
-- time_slot（时间段表）10条
-- =========================
INSERT INTO time_slot (id, slot_name, start_time, end_time, sort_order, status, description)
VALUES
(1,  '早晨一', '08:00', '10:00', 1, 1, '上午第一时段'),
(2,  '早晨二', '10:00', '12:00', 2, 1, '上午第二时段'),
(3,  '午后一', '13:00', '15:00', 3, 1, '下午第一时段'),
(4,  '午后二', '15:00', '17:00', 4, 1, '下午第二时段'),
(5,  '晚上一', '18:00', '20:00', 5, 1, '晚间第一时段'),
(6,  '晚上二', '20:00', '22:00', 6, 1, '晚间第二时段'),
(7,  '特批早', '07:00', '08:00', 7, 1, '特殊加开早间'),
(8,  '特批晚', '22:00', '23:00', 8, 1, '特殊加开晚间'),
(9,  '全天上', '09:00', '12:00', 9, 1, '半天上午'),
(10, '全天下', '14:00', '18:00', 10,1, '半天下午');

-- =========================
-- reservation（预约表）10条
-- 说明：time_slot字段存字符串，与time_slot表无外键
-- 日期区间：2026-01-13 ~ 2026-01-22
-- =========================
INSERT INTO reservation (id, user_id, lab_id, reserve_date, time_slot, people_num, purpose, experiment_name, equipment, status, approver, approve_comment, approve_time, cancel_reason, rating, comment)
VALUES
(1,  1, 1,  '2026-01-13', '08:00-10:00', 2,  '课程实验', '计算机基础实验',    '电脑,交换机', 1, '刘老师', '通过，注意设备保养', '2026-01-12 10:00:00', NULL, 5, '实验顺利'),
(2,  2, 2,  '2026-01-14', '10:00-12:00', 3,  '项目实践', '软件工程实践',      '电脑,投影仪', 1, '刘老师', '通过',            '2026-01-12 11:00:00', NULL, 4, '设备良好'),
(3,  3, 3,  '2026-01-15', '13:00-15:00', 2,  '课程实验', '网络攻防演练',      '路由器,防火墙',0, NULL,    NULL,              NULL,                  NULL, NULL, NULL),
(4,  4, 4,  '2026-01-16', '15:00-17:00', 4,  '课程实验', '力学测量',          '示波器,传感器',2, '孙老师','样品不足，驳回',   '2026-01-12 12:00:00', NULL, NULL, NULL),
(5,  5, 5,  '2026-01-17', '18:00-20:00', 3,  '科研试验', '化学分析',          '光谱仪,离心机',1, '周老师', '通过，注意安全',   '2026-01-12 13:00:00', NULL, 5, '安全到位'),
(6,  6, 6,  '2026-01-18', '20:00-22:00', 2,  '课程实验', '材料力学测试',      '万能试验机',   1, '周老师', '通过',            '2026-01-12 14:00:00', NULL, 4, '设备稳定'),
(7,  7, 7,  '2026-01-19', '07:00-08:00', 1,  '课程实验', '机械加工基础',      '车床,铣床',     3, '管理员甲','用户主动取消',     NULL,                '临时有事', NULL, NULL),
(8,  8, 8,  '2026-01-20', '22:00-23:00', 2,  '课程实验', '电子电路调试',      '示波器,电源',   1, '刘老师', '通过',            '2026-01-12 15:00:00', NULL, 4, '调试顺利'),
(9,  9, 9,  '2026-01-21', '09:00-12:00', 5,  '指导实验', '生物工程基础',      '培养箱,显微镜', 1, '管理员乙','通过',            '2026-01-12 16:00:00', NULL, 5, '教学效果好'),
(10, 10, 10, '2026-01-22', '14:00-18:00', 6, '科研试验', 'AI数据处理',        'GPU服务器',     1, '管理员甲','通过',            '2026-01-12 17:00:00', NULL, 5, '训练完成');

-- =========================
-- message（站内消息表）10条
-- 说明：system消息sender_id为NULL；reservation类消息related_id指向reservation.id
-- =========================
INSERT INTO message (id, sender_id, sender_name, receiver_id, receiver_name, message_type, title, content, related_id, related_type, is_read, priority)
VALUES
(1,  NULL,      NULL,      1,  '张三',     'system',     '系统通知',         '欢迎使用实验室预约系统',           NULL, NULL, 0, 0),
(2,  6,         '刘老师',  1,  '张三',     'approval',   '预约审核通过',     '您的预约(1)已通过，请准时到场',    1,   'reservation', 0, 1),
(3,  6,         '刘老师',  2,  '李四',     'approval',   '预约审核通过',     '您的预约(2)已通过，请准时到场',    2,   'reservation', 0, 1),
(4,  7,         '孙老师',  4,  '赵六',     'approval',   '预约被拒绝',       '预约(4)驳回：样品不足',            4,   'reservation', 1, 2),
(5,  8,         '周老师',  5,  '钱七',     'reminder',   '实验室使用提醒',   '请注意安全规范，实验(5)即将开始',  5,   'reservation', 0, 1),
(6,  8,         '周老师',  6,  '刘老师',   'user',       '设备维护通知',     '材料实验室设备将维护，请留意安排', NULL,'reservation', 0, 0),
(7,  9,         '管理员甲',7,  '孙老师',   'reservation','预约取消确认',     '预约(7)已取消，原因：临时有事',     7,   'reservation', 1, 1),
(8,  6,         '刘老师',  8,  '周老师',   'reminder',   '晚间使用提醒',     '预约(8)为晚间，请注意离场时间',     8,   'reservation', 0, 0),
(9,  10,        '管理员乙',9,  '管理员甲', 'system',     '系统维护通知',     '本周将进行系统升级，请关注邮件',    NULL, NULL, 0, 2),
(10, 9,         '管理员甲',10, '管理员乙', 'system',     '新功能上线',       '新增AI训练任务看板，欢迎试用',      NULL, NULL, 0, 1);

-- 重置自增到最大ID+1，避免后续插入冲突
SET @max_user_id := (SELECT MAX(id) FROM user);
SET @max_lab_id := (SELECT MAX(id) FROM laboratory);
SET @max_slot_id := (SELECT MAX(id) FROM time_slot);
SET @max_resv_id := (SELECT MAX(id) FROM reservation);
SET @max_msg_id := (SELECT MAX(id) FROM message);

SET @sql1 := CONCAT('ALTER TABLE user AUTO_INCREMENT = ', @max_user_id + 1);
SET @sql2 := CONCAT('ALTER TABLE laboratory AUTO_INCREMENT = ', @max_lab_id + 1);
SET @sql3 := CONCAT('ALTER TABLE time_slot AUTO_INCREMENT = ', @max_slot_id + 1);
SET @sql4 := CONCAT('ALTER TABLE reservation AUTO_INCREMENT = ', @max_resv_id + 1);
SET @sql5 := CONCAT('ALTER TABLE message AUTO_INCREMENT = ', @max_msg_id + 1);

PREPARE stmt1 FROM @sql1; EXECUTE stmt1; DEALLOCATE PREPARE stmt1;
PREPARE stmt2 FROM @sql2; EXECUTE stmt2; DEALLOCATE PREPARE stmt2;
PREPARE stmt3 FROM @sql3; EXECUTE stmt3; DEALLOCATE PREPARE stmt3;
PREPARE stmt4 FROM @sql4; EXECUTE stmt4; DEALLOCATE PREPARE stmt4;
PREPARE stmt5 FROM @sql5; EXECUTE stmt5; DEALLOCATE PREPARE stmt5;

-- 导入提示：
-- 使用MySQL客户端或IDE执行本文件即可生成测试数据。
-- 若需命令行导入，请调整为正确的账户与连接参数：
-- mysql -h127.0.0.1 -uroot -p lab_reservation < src/main/resources/mysql/test_data_10.sql
