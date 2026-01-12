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
use lab_reservation;

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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='实验室表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `sender_id` bigint DEFAULT NULL COMMENT '发送者ID(系统消息为NULL)',
  `sender_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送者名称',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `receiver_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接收者名称',
  `message_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息类型: system/user/reservation/approval/reminder',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息标题',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID(预约ID/审批ID等)',
  `related_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联类型(reservation/approval等)',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内消息表';
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='预约表';
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='时间段表';
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

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

-- Dump completed on 2026-01-12 14:44:57
