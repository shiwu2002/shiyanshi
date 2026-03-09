package com.example.shiyanshi.service;

import com.example.shiyanshi.entity.UserCredit;
import com.example.shiyanshi.entity.UserCreditLog;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 信誉分系统测试
 */
@Slf4j
@SpringBootTest
public class CreditServiceTest {
    
    @Autowired
    private CreditService creditService;
    
    /**
     * 测试初始化用户信誉分
     */
    @Test
    public void testInitCredit() {
        log.info("========== 测试初始化信誉分 ==========");
        
        Long userId = 1L;
        creditService.initCredit(userId);
        
        UserCredit credit = creditService.getUserCredit(userId);
        
        assertNotNull(credit, "信誉分对象不应为空");
        assertEquals(100, credit.getScore(), "初始分数应为 100");
        assertEquals(3, credit.getLevel(), "初始等级应为优");
        
        log.info("用户 {} 初始信誉分：{}, 等级：{}", userId, credit.getScore(), credit.getLevel());
        log.info("✅ 初始化测试通过");
    }
    
    /**
     * 测试预约成功加分
     */
    @Test
    public void testApproveReservation() {
        log.info("========== 测试预约成功加分 ==========");
        
        Long userId = 2L;
        Long reservationId = 999L;
        
        // 获取加分前的分数
        UserCredit beforeCredit = creditService.getUserCredit(userId);
        Integer scoreBefore = beforeCredit.getScore();
        
        log.info("加分前分数：{}", scoreBefore);
        
        // 模拟预约成功
        creditService.approveReservation(userId, reservationId);
        
        // 验证加分
        UserCredit afterCredit = creditService.getUserCredit(userId);
        log.info("加分后分数：{}", afterCredit.getScore());
        
        assertEquals(scoreBefore + 1, afterCredit.getScore(), "预约成功应加 1 分");
        assertTrue(afterCredit.getTotalAddTimes() > beforeCredit.getTotalAddTimes(), "加分次数应增加");
        
        log.info("✅ 预约成功加分测试通过");
    }
    
    /**
     * 测试准时使用加分（含连续奖励）
     */
    @Test
    public void testOnTimeUse() {
        log.info("========== 测试准时使用加分 ==========");
        
        Long userId = 3L;
        Long reservationId = 888L;
        
        // 连续使用 5 次
        for (int i = 1; i <= 5; i++) {
            creditService.onTimeUse(userId, reservationId + i);
            
            UserCredit credit = creditService.getUserCredit(userId);
            log.info("第 {} 次准时使用后：分数={}, 连续次数={}", 
                i, credit.getScore(), credit.getContinuousOnTimeCount());
        }
        
        UserCredit finalCredit = creditService.getUserCredit(userId);
        log.info("最终分数：{}, 等级：{}", finalCredit.getScore(), finalCredit.getLevel());
        
        // 验证有额外奖励
        assertTrue(finalCredit.getScore() > 100 + 5 * 2, "应该有连续使用额外奖励");
        
        log.info("✅ 准时使用加分测试通过");
    }
    
    /**
     * 测试取消预约扣分
     */
    @Test
    public void testCancelReservation() {
        log.info("========== 测试取消预约扣分 ==========");
        
        Long userId = 4L;
        Long reservationId = 777L;
        
        // 获取扣分前的分数
        UserCredit beforeCredit = creditService.getUserCredit(userId);
        Integer scoreBefore = beforeCredit.getScore();
        
        log.info("扣分前分数：{}", scoreBefore);
        
        // 模拟 24 小时内取消
        LocalDateTime cancelTime = LocalDateTime.now();
        LocalDateTime reserveTime = LocalDateTime.now().plusHours(12); // 12 小时后
        
        creditService.cancelReservation(userId, reservationId, cancelTime, reserveTime);
        
        // 验证扣分
        UserCredit afterCredit = creditService.getUserCredit(userId);
        log.info("扣分后分数：{}", afterCredit.getScore());
        
        assertTrue(afterCredit.getScore() < scoreBefore, "取消预约应该扣分");
        assertEquals(0, afterCredit.getContinuousOnTimeCount(), "连续使用次数应重置为 0");
        
        log.info("✅ 取消预约扣分测试通过");
    }
    
    /**
     * 测试爽约扣分
     */
    @Test
    public void testNoShow() {
        log.info("========== 测试爽约扣分 ==========");
        
        Long userId = 5L;
        Long reservationId = 666L;
        
        // 获取扣分前的分数
        UserCredit beforeCredit = creditService.getUserCredit(userId);
        Integer scoreBefore = beforeCredit.getScore();
        
        log.info("扣分前分数：{}", scoreBefore);
        
        // 模拟爽约
        creditService.noShow(userId, reservationId);
        
        // 验证扣分
        UserCredit afterCredit = creditService.getUserCredit(userId);
        log.info("扣分后分数：{}", afterCredit.getScore());
        
        assertEquals(scoreBefore - 10, afterCredit.getScore(), "爽约应扣 10 分");
        
        log.info("✅ 爽约扣分测试通过");
    }
    
    /**
     * 测试信誉分等级变化
     */
    @Test
    public void testLevelChange() {
        log.info("========== 测试信誉分等级变化 ==========");
        
        Long userId = 6L;
        
        // 先扣分到等级 2（80-99 分）
        creditService.adjustByAdmin(userId, -15, "测试降级", "TEST");
        UserCredit credit1 = creditService.getUserCredit(userId);
        log.info("扣分后：分数={}, 等级={}", credit1.getScore(), credit1.getLevel());
        assertEquals(2, credit1.getLevel(), "等级应为良");
        
        // 再扣分到等级 1（60-79 分）
        creditService.adjustByAdmin(userId, -20, "测试降级", "TEST");
        UserCredit credit2 = creditService.getUserCredit(userId);
        log.info("再扣分后：分数={}, 等级={}", credit2.getScore(), credit2.getLevel());
        assertEquals(1, credit2.getLevel(), "等级应为中");
        
        // 再扣分到等级 0（0-59 分）
        creditService.adjustByAdmin(userId, -20, "测试降级", "TEST");
        UserCredit credit3 = creditService.getUserCredit(userId);
        log.info("再扣分后：分数={}, 等级={}", credit3.getScore(), credit3.getLevel());
        assertEquals(0, credit3.getLevel(), "等级应为差");
        
        // 检查是否被禁止预约
        int canReserve = creditService.checkCanReserve(userId);
        assertEquals(1, canReserve, "等级 0 应被禁止预约");
        
        log.info("✅ 等级变化测试通过");
    }
    
    /**
     * 测试管理员手动调整分数
     */
    @Test
    public void testAdminAdjust() {
        log.info("========== 测试管理员手动调整分数 ==========");
        
        Long userId = 7L;
        
        // 获取调整前的分数
        UserCredit beforeCredit = creditService.getUserCredit(userId);
        log.info("调整前分数：{}", beforeCredit.getScore());
        
        // 管理员奖励 20 分
        creditService.adjustByAdmin(userId, 20, "表现优秀，奖励 20 分", "ADMIN_001");
        
        UserCredit afterCredit1 = creditService.getUserCredit(userId);
        log.info("奖励后分数：{}", afterCredit1.getScore());
        assertEquals(beforeCredit.getScore() + 20, afterCredit1.getScore());
        
        // 管理员惩罚 -15 分
        creditService.adjustByAdmin(userId, -15, "违规操作，扣除 15 分", "ADMIN_002");
        
        UserCredit afterCredit2 = creditService.getUserCredit(userId);
        log.info("惩罚后分数：{}", afterCredit2.getScore());
        assertEquals(afterCredit1.getScore() - 15, afterCredit2.getScore());
        
        log.info("✅ 管理员调整测试通过");
    }
    
    /**
     * 测试查询信誉分记录
     */
    @Test
    public void testGetCreditLogs() {
        log.info("========== 测试查询信誉分记录 ==========");
        
        Long userId = 8L;
        
        // 先产生一些记录
        creditService.approveReservation(userId, 1001L);
        creditService.onTimeUse(userId, 1002L);
        creditService.noShow(userId, 1003L);
        
        // 查询记录
        List<UserCreditLog> logs = creditService.getCreditLogs(userId, 1, 10);
        
        log.info("用户 {} 的信誉分记录数：{}", userId, logs.size());
        assertTrue(logs.size() >= 3, "至少应有 3 条记录");
        
        // 打印最新 5 条记录
        for (int i = 0; i < Math.min(5, logs.size()); i++) {
            UserCreditLog logEntry = logs.get(i);
            log.info("[{}] 变动：{:+}, 说明：{}, 时间：{}", 
                i + 1, 
                logEntry.getChangeScore(),
                logEntry.getDescription(),
                logEntry.getCreateTime());
        }
        
        log.info("✅ 记录查询测试通过");
    }
    
    /**
     * 测试获取统计信息
     */
    @Test
    public void testGetCreditStats() {
        log.info("========== 测试获取统计信息 ==========");
        
        Long userId = 9L;
        
        Map<String, Object> stats = creditService.getCreditStats(userId);
        
        log.info("用户 {} 的信誉分统计：", userId);
        log.info("当前分数：{}", stats.get("score"));
        log.info("信用等级：{} ({})", stats.get("level"), stats.get("levelText"));
        log.info("历史最高分：{}", stats.get("maxScore"));
        log.info("累计加分次数：{}", stats.get("totalAddTimes"));
        log.info("累计扣分次数：{}", stats.get("totalSubtractTimes"));
        log.info("连续准时使用次数：{}", stats.get("continuousOnTimeCount"));
        
        assertNotNull(stats.get("score"), "分数不应为空");
        assertNotNull(stats.get("level"), "等级不应为空");
        
        log.info("✅ 统计信息查询测试通过");
    }
    
    /**
     * 测试预约限制检查
     */
    @Test
    public void testCheckCanReserve() {
        log.info("========== 测试预约限制检查 ==========");
        
        // 测试正常用户（等级 3）
        Long normalUserId = 10L;
        int result1 = creditService.checkCanReserve(normalUserId);
        assertEquals(0, result1, "正常用户应可以预约");
        log.info("正常用户检查结果：{}", result1 == 0 ? "允许预约" : "禁止预约");
        
        // 测试低分用户（需要手动降级）
        Long lowScoreUserId = 11L;
        creditService.adjustByAdmin(lowScoreUserId, -50, "测试降级", "TEST");
        
        int result2 = creditService.checkCanReserve(lowScoreUserId);
        assertEquals(1, result2, "低分用户应被禁止预约");
        log.info("低分用户检查结果：{}", result2 == 1 ? "禁止预约" : "允许预约");
        
        log.info("✅ 预约限制检查测试通过");
    }
}
