-- 实验室预约系统数据库初始化脚本

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS lab_reservation DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE lab_reservation;

-- 1. 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码（MD5加密）',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `user_type` INT NOT NULL DEFAULT 0 COMMENT '用户类型：0-普通用户，1-管理员',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_user_type` (`user_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 2. 实验室表
DROP TABLE IF EXISTS `laboratory`;
CREATE TABLE `laboratory` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '实验室ID',
  `lab_name` VARCHAR(100) NOT NULL COMMENT '实验室名称',
  `lab_number` VARCHAR(50) NOT NULL COMMENT '实验室编号',
  `location` VARCHAR(200) DEFAULT NULL COMMENT '位置',
  `capacity` INT NOT NULL DEFAULT 0 COMMENT '容纳人数',
  `equipment` TEXT DEFAULT NULL COMMENT '设备信息',
  `description` TEXT DEFAULT NULL COMMENT '描述',
  `image_url` VARCHAR(255) DEFAULT NULL COMMENT '图片URL',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态：0-维护中，1-可预约',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lab_number` (`lab_number`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实验室表';

-- 3. 时间段表
DROP TABLE IF EXISTS `time_slot`;
CREATE TABLE `time_slot` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '时间段ID',
  `slot_name` VARCHAR(50) NOT NULL COMMENT '时间段名称',
  `start_time` VARCHAR(10) NOT NULL COMMENT '开始时间（HH:mm格式）',
  `end_time` VARCHAR(10) NOT NULL COMMENT '结束时间（HH:mm格式）',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序号',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间段表';

-- 4. 预约表
DROP TABLE IF EXISTS `reservation`;
CREATE TABLE `reservation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `lab_id` BIGINT NOT NULL COMMENT '实验室ID',
  `reserve_date` DATE NOT NULL COMMENT '预约日期',
  `time_slot` VARCHAR(50) NOT NULL COMMENT '时间段',
  `purpose` TEXT DEFAULT NULL COMMENT '预约目的',
  `status` INT NOT NULL DEFAULT 0 COMMENT '状态：0-待审核，1-已通过，2-已拒绝，3-已取消，4-已完成',
  `reject_reason` VARCHAR(255) DEFAULT NULL COMMENT '拒绝原因',
  `rating` INT DEFAULT NULL COMMENT '评分（1-5）',
  `comment` TEXT DEFAULT NULL COMMENT '评价',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_lab_id` (`lab_id`),
  KEY `idx_reserve_date` (`reserve_date`),
  KEY `idx_status` (`status`),
  KEY `idx_lab_date` (`lab_id`, `reserve_date`),
  CONSTRAINT `fk_reservation_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reservation_lab` FOREIGN KEY (`lab_id`) REFERENCES `laboratory` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约表';

-- 插入初始数据

-- 插入管理员账号（密码：admin123，MD5加密后）
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `user_type`, `status`) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '系统管理员', '13800138000', 'admin@lab.com', 1, 1);

-- 插入测试用户（密码：123456，MD5加密后）
INSERT INTO `user` (`username`, `password`, `real_name`, `phone`, `email`, `user_type`, `status`) VALUES
('user001', 'e10adc3949ba59abbe56e057f20f883e', '张三', '13800138001', 'zhangsan@example.com', 0, 1),
('user002', 'e10adc3949ba59abbe56e057f20f883e', '李四', '13800138002', 'lisi@example.com', 0, 1),
('user003', 'e10adc3949ba59abbe56e057f20f883e', '王五', '13800138003', 'wangwu@example.com', 0, 1);

-- 插入实验室数据
INSERT INTO `laboratory` (`lab_name`, `lab_number`, `location`, `capacity`, `equipment`, `description`, `status`) VALUES
('计算机实验室A', 'LAB-CS-A01', '教学楼3楼301', 50, '高性能计算机50台，投影仪1台，空调2台', '配备最新硬件设备，适合编程实验', 1),
('计算机实验室B', 'LAB-CS-B01', '教学楼3楼302', 40, '计算机40台，投影仪1台', '标准配置计算机实验室', 1),
('物理实验室', 'LAB-PHY-01', '实验楼1楼101', 30, '力学实验设备，电磁学实验设备，光学实验台', '综合物理实验室', 1),
('化学实验室', 'LAB-CHEM-01', '实验楼2楼201', 25, '通风橱，实验台，试剂柜，洗眼器', '化学分析实验室', 1),
('生物实验室', 'LAB-BIO-01', '实验楼2楼202', 30, '显微镜30台，培养箱，离心机，超净工作台', '生物技术实验室', 1);

-- 插入时间段数据
INSERT INTO `time_slot` (`slot_name`, `start_time`, `end_time`, `sort_order`, `status`, `description`) VALUES
('第1-2节', '08:00', '09:40', 1, 1, '上午第一时段'),
('第3-4节', '10:00', '11:40', 2, 1, '上午第二时段'),
('第5-6节', '14:00', '15:40', 3, 1, '下午第一时段'),
('第7-8节', '16:00', '17:40', 4, 1, '下午第二时段'),
('第9-10节', '19:00', '20:40', 5, 1, '晚上时段');

-- 插入一些示例预约记录
INSERT INTO `reservation` (`user_id`, `lab_id`, `reserve_date`, `time_slot`, `purpose`, `status`) VALUES
(2, 1, CURDATE() + INTERVAL 1 DAY, '第3-4节', 'Java编程实验', 0),
(3, 2, CURDATE() + INTERVAL 1 DAY, '第5-6节', 'Python数据分析实验', 0),
(4, 3, CURDATE() + INTERVAL 2 DAY, '第3-4节', '力学实验', 1);
