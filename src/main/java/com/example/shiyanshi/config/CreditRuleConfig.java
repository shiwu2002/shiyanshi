package com.example.shiyanshi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 信誉分规则配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "credit.rule")
public class CreditRuleConfig {
    
    /**
     * 初始分数（默认 100）
     */
    private Integer initialScore = 100;
    
    /**
     * 最低分数（默认 0）
     */
    private Integer minScore = 0;
    
    /**
     * 最高分数（默认 150）
     */
    private Integer maxScore = 150;
    
    /**
     * 加分规则
     */
    private AddRules add;
    
    /**
     * 扣分规则
     */
    private SubtractRules subtract;
    
    /**
     * 信用等级限制
     */
    private LevelLimits levelLimits;
    
    @Data
    public static class AddRules {
        /**
         * 预约成功并通过审核（+1 分）
         */
        private Integer approveReservation = 1;
        
        /**
         * 准时使用实验室（+2 分）
         */
        private Integer onTimeUse = 2;
        
        /**
         * 连续 3 次准时使用额外奖励（+3 分）
         */
        private Integer continuous3Times = 3;
        
        /**
         * 连续 5 次准时使用额外奖励（+5 分）
         */
        private Integer continuous5Times = 5;
        
        /**
         * 管理员手动奖励（+10 分）
         */
        private Integer adminReward = 10;
    }
    
    @Data
    public static class SubtractRules {
        /**
         * 取消预约（开馆前 24 小时内）（-1 分）
         */
        private Integer cancelWithin24Hours = 1;
        
        /**
         * 临时取消（开馆前 1 小时内）（-5 分）
         */
        private Integer cancelWithin1Hour = 5;
        
        /**
         * 爽约（未按时使用且未取消）（-10 分）
         */
        private Integer noShow = 10;
        
        /**
         * 恶意破坏设备或违规操作（-20 分）
         */
        private Integer violation = 20;
    }
    
    @Data
    public static class LevelLimits {
        /**
         * 等级 0（差，0-59 分）：禁止预约
         */
        private Integer level0MaxWeeklyReservations = 0;
        
        /**
         * 等级 1（中，60-79 分）：每周最多 2 次
         */
        private Integer level1MaxWeeklyReservations = 2;
        
        /**
         * 等级 2（良，80-99 分）：每周最多 5 次
         */
        private Integer level2MaxWeeklyReservations = 5;
        
        /**
         * 等级 3（优，100-119 分）：每周最多 10 次
         */
        private Integer level3MaxWeeklyReservations = 10;
        
        /**
         * 等级 4（极好，120+ 分）：免审核
         */
        private Integer level4MaxWeeklyReservations = 999;
    }
}
