-- MySQL dump 10.13  Distrib 9.4.0, for macos15 (arm64)
--
-- Host: 127.0.0.1    Database: lab_reservation
-- ------------------------------------------------------
-- Server version	9.4.0-commercial

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `laboratory`
--

DROP TABLE IF EXISTS `laboratory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `laboratory` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '实验室ID',
  `lab_name` varchar(100) NOT NULL COMMENT '实验室名称',
  `lab_number` varchar(50) NOT NULL COMMENT '实验室编号',
  `location` varchar(200) DEFAULT NULL COMMENT '位置',
  `capacity` int NOT NULL DEFAULT '0' COMMENT '容纳人数',
  `equipment` text COMMENT '设备信息',
  `description` text COMMENT '描述',
  `image_url` varchar(255) DEFAULT NULL COMMENT '图片URL',
  `building` varchar(100) DEFAULT NULL COMMENT '楼栋',
  `floor` varchar(50) DEFAULT NULL COMMENT '楼层',
  `lab_type` varchar(50) DEFAULT NULL COMMENT '实验室类型',
  `manager` varchar(100) DEFAULT NULL COMMENT '负责人',
  `manager_phone` varchar(20) DEFAULT NULL COMMENT '负责人电话',
  `images` text COMMENT '实验室图片（多张，逗号分隔）',
  `open_time` varchar(100) DEFAULT NULL COMMENT '开放时间说明',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：0-维护中，1-可预约',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lab_number` (`lab_number`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='实验室表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `laboratory`
--

/*!40000 ALTER TABLE `laboratory` DISABLE KEYS */;
INSERT INTO `laboratory` VALUES (1,'计算机综合实验室A','LAB-COMP-001','信息工程楼A区101',28,'电脑,服务器,交换机','用于计算机基础与网络实验',NULL,'信息工程楼','2F','计算机','刘老师','13900020001',NULL,'工作日 08:00-18:00',1,'2026-01-13 11:05:42',NULL),(2,'软件工程实践室','LAB-SWE-002','信息工程楼A区102',35,'电脑,投影仪','软件开发与项目实践',NULL,'信息工程楼','1F','计算机','刘老师','13900020001',NULL,'工作日 08:00-18:00',1,'2026-01-13 11:05:42','2026-01-13 11:05:42'),(3,'网络与安全实验室','LAB-NET-003','信息工程楼B区201',25,'路由器,防火墙','网络攻防与安全实验',NULL,'信息工程楼','2F','网络安全','孙老师','13900020002',NULL,'工作日 09:00-17:00',1,'2026-01-13 11:05:42','2026-01-13 11:05:42'),(4,'物理基础实验室','LAB-PHY-004','理学院楼C区301',40,'示波器,电源,传感器','物理基础实验教学',NULL,'理学院楼','3F','物理','孙老师','13900020002',NULL,'工作日 08:00-18:00',1,'2026-01-13 11:05:42','2026-01-13 11:05:42'),(5,'化学分析实验室','LAB-CHE-005','化学楼D区401',20,'光谱仪,离心机','化学分析与实验',NULL,'化学楼','4F','化学','周老师','13900020003',NULL,'工作日 09:00-17:00',1,'2026-01-13 11:05:42','2026-01-13 11:05:42'),(6,'材料力学实验室','LAB-MAT-006','材料楼E区501',28,'万能试验机,硬度计','材料力学与测试',NULL,'材料楼','5F','材料','周老师','13900020003',NULL,'工作日 08:00-18:00',1,'2026-01-13 11:05:42','2026-01-13 11:05:42'),(7,'机械加工实验室','LAB-MECH-007','机械楼F区101',18,'车床,铣床','机械加工基础训练',NULL,'机械楼','1F','机械','管理员甲','13900020009',NULL,'工作日 08:00-18:00',1,'2026-01-13 11:05:42','2026-01-13 11:05:42'),(8,'电子设计实验室','LAB-EE-008','信息工程楼B区202',32,'电烙铁,示波器,电源','电子电路设计与调试',NULL,'信息工程楼','2F','电子','刘老师','13900020001',NULL,'工作日 08:00-18:00',1,'2026-01-13 11:05:42','2026-01-13 11:05:42'),(9,'生物工程实验室','LAB-BIO-009','生物楼G区201',22,'培养箱,显微镜','生物工程基础',NULL,'生物楼','2F','生物','管理员乙','13900020010',NULL,'工作日 09:00-17:00',1,'2026-01-13 11:05:42','2026-01-13 11:05:42'),(10,'AI与数据实验室','LAB-AI-010','信息工程楼C区301',50,'GPU服务器,工作站','AI模型训练与数据分析',NULL,'信息工程楼','3F','人工智能','管理员甲','13900020009',NULL,'工作日 08:00-18:00',1,'2026-01-13 11:05:42','2026-01-13 11:05:42');
/*!40000 ALTER TABLE `laboratory` ENABLE KEYS */;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint DEFAULT NULL COMMENT '发送者ID(系统消息为NULL)',
  `sender_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送者名称',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `receiver_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接收者名称',
  `message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息类型: system/user/reservation/approval/reminder',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID(预约ID/审批ID等)',
  `related_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联类型(reservation/approval等)',
  `is_read` tinyint DEFAULT '0' COMMENT '是否已读: 0-未读 1-已读',
  `priority` tinyint DEFAULT '0' COMMENT '优先级: 0-普通 1-重要 2-紧急',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `deleted` tinyint DEFAULT '0' COMMENT '删除标记: 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_message_type` (`message_type`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_priority` (`priority`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_deleted` (`deleted`),
  KEY `idx_receiver_read` (`receiver_id`,`is_read`,`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (1,NULL,NULL,1,'张三','system','系统通知','欢迎使用实验室预约系统',NULL,NULL,1,0,'2026-01-13 11:05:42','2026-01-13 12:46:06',0),(2,6,'刘老师',1,'张三','approval','预约审核通过','您的预约(1)已通过，请准时到场',1,'reservation',1,1,'2026-01-13 11:05:42','2026-01-13 12:46:06',0),(3,6,'刘老师',2,'李四','approval','预约审核通过','您的预约(2)已通过，请准时到场',2,'reservation',0,1,'2026-01-13 11:05:42',NULL,0),(4,7,'孙老师',4,'赵六','approval','预约被拒绝','预约(4)驳回：样品不足',4,'reservation',1,2,'2026-01-13 11:05:42',NULL,0),(5,8,'周老师',5,'钱七','reminder','实验室使用提醒','请注意安全规范，实验(5)即将开始',5,'reservation',0,1,'2026-01-13 11:05:42',NULL,0),(6,8,'周老师',6,'刘老师','user','设备维护通知','材料实验室设备将维护，请留意安排',NULL,'reservation',0,0,'2026-01-13 11:05:42',NULL,0),(7,9,'管理员甲',7,'孙老师','reservation','预约取消确认','预约(7)已取消，原因：临时有事',7,'reservation',1,1,'2026-01-13 11:05:42',NULL,0),(8,6,'刘老师',8,'周老师','reminder','晚间使用提醒','预约(8)为晚间，请注意离场时间',8,'reservation',0,0,'2026-01-13 11:05:42',NULL,0),(9,10,'管理员乙',9,'管理员甲','system','系统维护通知','本周将进行系统升级，请关注邮件',NULL,NULL,0,2,'2026-01-13 11:05:42',NULL,0),(10,9,'管理员甲',10,'管理员乙','system','新功能上线','新增AI训练任务看板，欢迎试用',NULL,NULL,0,1,'2026-01-13 11:05:42',NULL,0),(11,0,'系统',2,'student02','reminder','预约提醒（明天）','您预约的实验室 null 将于 24小时 后开始使用（2026-01-14 10:00-12:00），请做好准备并准时到达。',2,'reservation',0,2,'2026-01-13 12:00:00',NULL,0),(12,0,'系统',6,'teacher01','system','预约取消通知','您的预约[材料力学实验室 - 2026-01-18 20:00-22:00]已被取消。',NULL,NULL,0,1,'2026-01-13 12:03:23',NULL,0),(13,0,'系统',1,'admin','system','警告','系统停运，后续等待消息',NULL,NULL,1,0,'2026-01-13 12:39:33','2026-01-13 12:46:06',0),(14,0,'系统',2,'student02','system','警告','系统停运，后续等待消息',NULL,NULL,0,0,'2026-01-13 12:39:33',NULL,0),(15,0,'系统',3,'student03','system','警告','系统停运，后续等待消息',NULL,NULL,0,0,'2026-01-13 12:39:33',NULL,0),(16,0,'系统',4,'student04','system','警告','系统停运，后续等待消息',NULL,NULL,0,0,'2026-01-13 12:39:33',NULL,0),(17,0,'系统',5,'student05','system','警告','系统停运，后续等待消息',NULL,NULL,0,0,'2026-01-13 12:39:33',NULL,0),(18,0,'系统',6,'teacher01','system','警告','系统停运，后续等待消息',NULL,NULL,0,0,'2026-01-13 12:39:33',NULL,0),(19,0,'系统',7,'teacher02','system','警告','系统停运，后续等待消息',NULL,NULL,0,0,'2026-01-13 12:39:33',NULL,0),(20,0,'系统',8,'teacher03','system','警告','系统停运，后续等待消息',NULL,NULL,0,0,'2026-01-13 12:39:33',NULL,0),(21,0,'系统',9,'admin01','system','警告','系统停运，后续等待消息',NULL,NULL,0,0,'2026-01-13 12:39:33',NULL,0),(22,0,'系统',10,'admin02','system','警告','系统停运，后续等待消息',NULL,NULL,0,0,'2026-01-13 12:39:33',NULL,0),(23,0,'系统',1,'admin','system','测试','删除阿斯顿发',NULL,NULL,1,0,'2026-01-13 12:46:59','2026-01-13 12:46:58',1),(24,0,'系统',2,'student02','system','测试','删除阿斯顿发',NULL,NULL,0,0,'2026-01-13 12:46:59',NULL,0),(25,0,'系统',3,'student03','system','测试','删除阿斯顿发',NULL,NULL,0,0,'2026-01-13 12:46:59',NULL,0),(26,0,'系统',4,'student04','system','测试','删除阿斯顿发',NULL,NULL,0,0,'2026-01-13 12:46:59',NULL,0),(27,0,'系统',5,'student05','system','测试','删除阿斯顿发',NULL,NULL,0,0,'2026-01-13 12:46:59',NULL,0),(28,0,'系统',6,'teacher01','system','测试','删除阿斯顿发',NULL,NULL,0,0,'2026-01-13 12:46:59',NULL,0),(29,0,'系统',7,'teacher02','system','测试','删除阿斯顿发',NULL,NULL,0,0,'2026-01-13 12:46:59',NULL,0),(30,0,'系统',8,'teacher03','system','测试','删除阿斯顿发',NULL,NULL,0,0,'2026-01-13 12:46:59',NULL,0),(31,0,'系统',9,'admin01','system','测试','删除阿斯顿发',NULL,NULL,0,0,'2026-01-13 12:46:59',NULL,0),(32,0,'系统',10,'admin02','system','测试','删除阿斯顿发',NULL,NULL,0,0,'2026-01-13 12:46:59',NULL,0),(33,0,'系统',1,'admin','system','阿斯顿发','阿斯顿发苏打粉啊',NULL,NULL,1,0,'2026-01-13 12:47:42','2026-01-13 12:49:31',1),(34,0,'系统',2,'student02','system','阿斯顿发','阿斯顿发苏打粉啊',NULL,NULL,0,0,'2026-01-13 12:47:42',NULL,0),(35,0,'系统',3,'student03','system','阿斯顿发','阿斯顿发苏打粉啊',NULL,NULL,0,0,'2026-01-13 12:47:42',NULL,0),(36,0,'系统',4,'student04','system','阿斯顿发','阿斯顿发苏打粉啊',NULL,NULL,0,0,'2026-01-13 12:47:42',NULL,0),(37,0,'系统',5,'student05','system','阿斯顿发','阿斯顿发苏打粉啊',NULL,NULL,0,0,'2026-01-13 12:47:42',NULL,0),(38,0,'系统',6,'teacher01','system','阿斯顿发','阿斯顿发苏打粉啊',NULL,NULL,0,0,'2026-01-13 12:47:42',NULL,0),(39,0,'系统',7,'teacher02','system','阿斯顿发','阿斯顿发苏打粉啊',NULL,NULL,0,0,'2026-01-13 12:47:42',NULL,0),(40,0,'系统',8,'teacher03','system','阿斯顿发','阿斯顿发苏打粉啊',NULL,NULL,0,0,'2026-01-13 12:47:42',NULL,0),(41,0,'系统',9,'admin01','system','阿斯顿发','阿斯顿发苏打粉啊',NULL,NULL,0,0,'2026-01-13 12:47:42',NULL,0),(42,0,'系统',10,'admin02','system','阿斯顿发','阿斯顿发苏打粉啊',NULL,NULL,0,0,'2026-01-13 12:47:42',NULL,0),(43,0,'系统',2,'student02','reminder','预约提醒（明天）','您预约的实验室 null 将于 24小时 后开始使用（2026-01-14 10:00-12:00），请做好准备并准时到达。',2,'reservation',0,2,'2026-01-13 13:00:00',NULL,0),(44,0,'系统',2,'student02','reminder','预约提醒（明天）','您预约的实验室 null 将于 24小时 后开始使用（2026-01-14 10:00-12:00），请做好准备并准时到达。',2,'reservation',0,2,'2026-01-13 14:00:00',NULL,0),(45,0,'系统',2,'student02','reminder','预约提醒（明天）','您预约的实验室 null 将于 24小时 后开始使用（2026-01-14 10:00-12:00），请做好准备并准时到达。',2,'reservation',0,2,'2026-01-13 15:00:00',NULL,0),(46,0,'系统',2,'student02','reminder','预约提醒（明天）','您预约的实验室 null 将于 24小时 后开始使用（2026-01-14 10:00-12:00），请做好准备并准时到达。',2,'reservation',0,2,'2026-01-13 16:00:00',NULL,0),(47,0,'系统',3,'student03','approval','预约审核通知','您的预约[网络与安全实验室 - 2026-01-15 13:00-15:00]审核结果：已通过。',3,'reservation',0,1,'2026-01-13 16:37:49',NULL,0),(48,0,'系统',2,'student02','reminder','预约提醒（明天）','您预约的实验室 null 将于 24小时 后开始使用（2026-01-14 10:00-12:00），请做好准备并准时到达。',2,'reservation',0,2,'2026-01-13 17:00:00',NULL,0),(49,0,'系统',3,'student03','reminder','预约提醒（明天）','您预约的实验室 null 将于 24小时 后开始使用（2026-01-15 13:00-15:00），请做好准备并准时到达。',3,'reservation',0,2,'2026-01-14 12:00:00',NULL,0),(50,0,'系统',3,'student03','reminder','预约提醒（明天）','您预约的实验室 null 将于 24小时 后开始使用（2026-01-15 13:00-15:00），请做好准备并准时到达。',3,'reservation',0,2,'2026-01-14 13:00:00',NULL,0),(51,0,'系统',3,'student03','reminder','预约提醒（明天）','您预约的实验室 null 将于 24小时 后开始使用（2026-01-15 13:00-15:00），请做好准备并准时到达。',3,'reservation',0,2,'2026-01-14 14:00:00',NULL,0),(52,0,'系统',1,'admin','system','预约完成通知','您的预约[计算机综合实验室A - 2026-01-13 08:00-10:00]已完成。评分：5星。',NULL,NULL,1,0,'2026-01-20 09:55:31','2026-01-20 15:17:36',0),(53,0,'系统',1,'admin','approval','预约审核通知','您的预约[计算机综合实验室A - 2026-01-20 20:00-22:00]审核结果：已通过。',11,'reservation',1,1,'2026-01-20 12:10:29','2026-01-20 17:02:00',0),(54,0,'系统',1,'admin','system','预约完成通知','您的预约[计算机综合实验室A - 2026-01-20 20:00-22:00]已完成。评分：5星。',NULL,NULL,1,0,'2026-01-20 15:58:10','2026-01-20 16:30:15',0),(55,0,'系统',1,'admin','system','预约已过期','您的预约（null，2026-01-20 13:00-15:00）因超时未审核已自动取消',NULL,NULL,1,0,'2026-01-21 02:00:00','2026-01-22 09:30:40',0),(56,0,'系统',1,'admin','system','预约取消通知','您的预约[网络与安全实验室 - 2026-01-22 13:00-15:00]已被取消。',NULL,NULL,0,1,'2026-01-22 11:33:32',NULL,0),(57,0,'系统',1,'admin','system','预约取消通知','您的预约[网络与安全实验室 - 2026-01-22 10:00-12:00]已被取消。',NULL,NULL,0,1,'2026-01-22 11:33:38',NULL,0),(58,0,'系统',1,'admin','system','预约取消通知','您的预约[软件工程实践室 - 2026-01-22 13:00-15:00]已被取消。',NULL,NULL,0,1,'2026-01-22 11:33:44',NULL,0);
/*!40000 ALTER TABLE `message` ENABLE KEYS */;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `lab_id` bigint NOT NULL COMMENT '实验室ID',
  `reserve_date` date NOT NULL COMMENT '预约日期',
  `time_slot` varchar(50) NOT NULL COMMENT '时间段',
  `people_num` int DEFAULT '1' COMMENT '使用人数',
  `purpose` text COMMENT '预约目的',
  `experiment_name` varchar(200) DEFAULT NULL COMMENT '实验名称',
  `equipment` text COMMENT '需要的设备',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态：0-待审核，1-已通过，2-已拒绝，3-已取消，4-已完成',
  `approver` varchar(50) DEFAULT NULL COMMENT '审核人',
  `approve_comment` text COMMENT '审核意见',
  `approve_time` datetime DEFAULT NULL COMMENT '审核时间',
  `cancel_reason` varchar(255) DEFAULT NULL COMMENT '取消原因',
  `rating` int DEFAULT NULL COMMENT '评分（1-5）',
  `comment` text COMMENT '评价',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_lab_id` (`lab_id`),
  KEY `idx_reserve_date` (`reserve_date`),
  KEY `idx_status` (`status`),
  KEY `idx_lab_date` (`lab_id`,`reserve_date`),
  CONSTRAINT `fk_reservation_lab` FOREIGN KEY (`lab_id`) REFERENCES `laboratory` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reservation_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预约表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (1,1,1,'2026-01-13','08:00-10:00',2,'课程实验','计算机基础实验','电脑,交换机',4,'刘老师','通过，注意设备保养','2026-01-12 10:00:00',NULL,5,'实验顺利','2026-01-13 11:05:42','2026-01-20 09:55:31'),(11,1,1,'2026-01-20','20:00-22:00',1,'阿斯顿发',NULL,NULL,4,NULL,NULL,'2026-01-20 12:10:29',NULL,5,NULL,'2026-01-20 11:39:51','2026-01-20 15:58:10'),(12,1,1,'2026-01-20','13:00-15:00',2,'阿斯顿发',NULL,NULL,3,NULL,NULL,NULL,'预约超时未审核，系统自动取消',NULL,NULL,'2026-01-20 12:27:11','2026-01-20 12:27:11'),(13,1,2,'2026-01-22','13:00-15:00',5,'期望的飒风',NULL,NULL,3,NULL,NULL,NULL,NULL,NULL,NULL,'2026-01-22 10:35:11','2026-01-22 11:33:44'),(14,1,3,'2026-01-22','10:00-12:00',2,'亲亲','亲亲',NULL,3,NULL,NULL,NULL,NULL,NULL,NULL,'2026-01-22 11:22:49','2026-01-22 11:33:38'),(15,1,3,'2026-01-22','13:00-15:00',1,'阿斯顿发','网络与安全实验室','',3,NULL,NULL,NULL,NULL,NULL,NULL,'2026-01-22 11:32:53','2026-01-22 11:33:32');
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;

--
-- Table structure for table `time_slot`
--

DROP TABLE IF EXISTS `time_slot`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `time_slot` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '时间段ID',
  `slot_name` varchar(50) NOT NULL COMMENT '时间段名称',
  `start_time` varchar(10) NOT NULL COMMENT '开始时间（HH:mm格式）',
  `end_time` varchar(10) NOT NULL COMMENT '结束时间（HH:mm格式）',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='时间段表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `time_slot`
--

/*!40000 ALTER TABLE `time_slot` DISABLE KEYS */;
INSERT INTO `time_slot` VALUES (1,'早晨一','08:00','22:11',1,1,'上午第一时段','2026-01-13 11:05:42',NULL),(2,'早晨二','10:00','12:00',2,1,'上午第二时段','2026-01-13 11:05:42','2026-01-13 11:05:42'),(3,'午后一','13:00','15:00',3,1,'下午第一时段','2026-01-13 11:05:42',NULL),(4,'午后二','15:00','17:00',4,1,'下午第二时段','2026-01-13 11:05:42','2026-01-13 11:05:42'),(5,'晚上一','18:00','20:00',5,1,'晚间第一时段','2026-01-13 11:05:42','2026-01-13 11:05:42'),(6,'晚上二','20:00','22:00',6,1,'晚间第二时段','2026-01-13 11:05:42','2026-01-13 11:05:42'),(7,'特批早','07:00','08:00',7,1,'特殊加开早间','2026-01-13 11:05:42','2026-01-13 11:05:42'),(8,'特批晚','22:00','23:00',8,1,'特殊加开晚间','2026-01-13 11:05:42','2026-01-13 11:05:42'),(9,'全天上','09:00','12:00',9,1,'半天上午','2026-01-13 11:05:42','2026-01-13 11:05:42'),(10,'全天下','14:00','18:00',10,1,'半天下午','2026-01-13 11:05:42','2026-01-13 11:05:42');
/*!40000 ALTER TABLE `time_slot` ENABLE KEYS */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码（MD5加密）',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `user_type` int NOT NULL DEFAULT '0' COMMENT '用户类型：0-普通用户，1-管理员',
  `college` varchar(100) DEFAULT NULL COMMENT '学院',
  `major` varchar(100) DEFAULT NULL COMMENT '专业',
  `student_id` varchar(50) DEFAULT NULL COMMENT '学号（学生）',
  `teacher_id` varchar(50) DEFAULT NULL COMMENT '工号（教师）',
  `status` int NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `email_verified` int DEFAULT '0' COMMENT '邮箱验证状态：0-未验证 1-已验证',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_user_type` (`user_type`),
  KEY `idx_status` (`status`),
  KEY `idx_email` (`email`),
  KEY `idx_email_verified` (`email_verified`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_teacher_id` (`teacher_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','25d55ad283aa400af464c76d713c07ad','张三','13800010001','8485723206@qq.com',3,'信息工程学院','计算机科学','20260001',NULL,1,'http://localhost:8080/uploads/avatar/2026/01/20/93fb0b2f-ef6e-4a25-a3a7-811cfb59f0f7.png','2026-01-13 11:05:42','2026-01-20 17:44:12',1),(12,'admin1','e10adc3949ba59abbe56e057f20f883e','asd','19185786585','848572306@qq.com',1,NULL,NULL,NULL,NULL,1,NULL,'2026-01-14 15:19:53','2026-01-14 16:15:53',0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;

--
-- Table structure for table `user_wechat_auth`
--

DROP TABLE IF EXISTS `user_wechat_auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_wechat_auth` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '业务用户ID（user表主键）',
  `platform` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '平台标识：mini_program-微信小程序, mp-微信公众号, enterprise_wechat-企业微信',
  `openid` varchar(128) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '微信openid（平台下的唯一用户标识）',
  `unionid` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信unionid（统一身份标识，某些情况下可能为空）',
  `session_key` varchar(128) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '最近一次会话密钥（可选，仅用于调试或校验，不建议长期存储）',
  `bind_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '绑定状态：0-未绑定, 1-已绑定',
  `last_login_time` datetime DEFAULT NULL COMMENT '最近登录时间（通过第三方登录成功后更新）',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除, 1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_platform_openid` (`platform`,`openid`) COMMENT '平台+openid唯一索引',
  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
  KEY `idx_platform_user` (`platform`,`user_id`) COMMENT '平台+用户ID索引',
  KEY `idx_unionid` (`unionid`) COMMENT 'unionid索引'
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户微信授权绑定表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_wechat_auth`
--

/*!40000 ALTER TABLE `user_wechat_auth` DISABLE KEYS */;
INSERT INTO `user_wechat_auth` VALUES (7,1,'mini_program','oIHwF7gRcXtNgvaU20hfsfy922rI','','jRcneNSpfj1Ncdpm41Ex7Q==',1,'2026-03-03 10:01:43',0,'2026-01-15 16:23:02','2026-03-03 10:01:43');
/*!40000 ALTER TABLE `user_wechat_auth` ENABLE KEYS */;

--
-- Dumping routines for database 'lab_reservation'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-03 10:04:31
