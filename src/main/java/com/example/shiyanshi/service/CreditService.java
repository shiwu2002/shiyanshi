package com.example.shiyanshi.service;

import com.example.shiyanshi.config.CreditRuleConfig;
import com.example.shiyanshi.entity.UserCredit;
import com.example.shiyanshi.entity.UserCreditLog;
import com.example.shiyanshi.mapper.UserCreditLogMapper;
import com.example.shiyanshi.mapper.UserCreditMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户信誉分服务
 */
@Slf4j
@Service
public class CreditService {
    
    @Autowired
    private UserCreditMapper userCreditMapper;
    
    @Autowired
    private UserCreditLogMapper userCreditLogMapper;
    
    @Autowired
    private CreditRuleConfig creditRuleConfig;
    
    /**
     * 初始化用户信誉分（如果不存在）
     */
    public void initCredit(Long userId) {
        userCreditMapper.initCreditIfNotExists(userId);
    }
    
    /**
     * 获取用户信誉分
     */
    public UserCredit getUserCredit(Long userId) {
        // 如果不存在则初始化
        initCredit(userId);
        return userCreditMapper.findByUserId(userId);
    }
    
    /**
     * 获取用户信用等级
     */
    public Integer getUserLevel(Long userId) {
        UserCredit credit = getUserCredit(userId);
        return credit != null ? credit.getLevel() : 3;
    }
    
    /**
     * 检查用户是否可以预约
     * @return 0-可以预约，1-禁止预约，2-超过每周限制
     */
    public int checkCanReserve(Long userId) {
        UserCredit credit = getUserCredit(userId);
        
        if (credit == null) {
            return 0; // 默认允许
        }
        
        // 等级 0：禁止预约
        if (credit.getLevel() == 0) {
            return 1;
        }
        
        // 检查本周预约次数是否超过限制
        int maxReservations = getMaxWeeklyReservations(credit.getLevel());
        int currentWeekCount = countWeeklyReservations(userId);
        
        if (currentWeekCount >= maxReservations) {
            return 2;
        }
        
        return 0;
    }
    
    /**
     * 获取每周最大预约次数
     */
    private int getMaxWeeklyReservations(Integer level) {
        switch (level) {
            case 0: return creditRuleConfig.getLevelLimits().getLevel0MaxWeeklyReservations();
            case 1: return creditRuleConfig.getLevelLimits().getLevel1MaxWeeklyReservations();
            case 2: return creditRuleConfig.getLevelLimits().getLevel2MaxWeeklyReservations();
            case 3: return creditRuleConfig.getLevelLimits().getLevel3MaxWeeklyReservations();
            case 4: return creditRuleConfig.getLevelLimits().getLevel4MaxWeeklyReservations();
            default: return 5;
        }
    }
    
    /**
     * 统计用户本周预约次数
     */
    private int countWeeklyReservations(Long userId) {
        // TODO: 需要 ReservationMapper 支持
        // 这里简化处理，实际应该查询数据库
        return 0;
    }
    
    /**
     * 预约成功并通过审核（加分）
     */
    @Transactional
    public void approveReservation(Long userId, Long reservationId) {
        addScore(userId, creditRuleConfig.getAdd().getApproveReservation(), 
                "预约成功并通过审核", reservationId, "SYSTEM");
    }
    
    /**
     * 准时使用实验室（加分）
     */
    @Transactional
    public void onTimeUse(Long userId, Long reservationId) {
        UserCredit credit = getUserCredit(userId);
        int addScore = creditRuleConfig.getAdd().getOnTimeUse();
        
        // 增加连续使用次数
        userCreditMapper.incrementContinuousOnTimeCount(userId);
        
        // 检查连续使用奖励
        int continuousCount = credit != null ? credit.getContinuousOnTimeCount() + 1 : 1;
        
        if (continuousCount == 3) {
            addScore += creditRuleConfig.getAdd().getContinuous3Times();
            log.info("用户 {} 连续 3 次准时使用，额外奖励 {} 分", userId, creditRuleConfig.getAdd().getContinuous3Times());
        } else if (continuousCount == 5) {
            addScore += creditRuleConfig.getAdd().getContinuous5Times();
            log.info("用户 {} 连续 5 次准时使用，额外奖励 {} 分", userId, creditRuleConfig.getAdd().getContinuous5Times());
        } else if (continuousCount > 5 && continuousCount % 5 == 0) {
            // 每连续 5 次都有额外奖励
            addScore += creditRuleConfig.getAdd().getContinuous5Times();
        }
        
        addScore(userId, addScore, "准时使用实验室（连续第" + continuousCount + "次）", reservationId, "SYSTEM");
    }
    
    /**
     * 取消预约（扣分）
     */
    @Transactional
    public void cancelReservation(Long userId, Long reservationId, LocalDateTime cancelTime, LocalDateTime reserveTime) {
        long hoursBefore = java.time.Duration.between(cancelTime, reserveTime).toHours();
        
        int subtractScore;
        String description;
        
        if (hoursBefore < 1) {
            subtractScore = creditRuleConfig.getSubtract().getCancelWithin1Hour();
            description = "临时取消预约（开馆前 1 小时内）";
        } else if (hoursBefore < 24) {
            subtractScore = creditRuleConfig.getSubtract().getCancelWithin24Hours();
            description = "取消预约（开馆前 24 小时内）";
        } else {
            // 提前 24 小时以上取消，不扣分
            return;
        }
        
        // 重置连续使用次数
        userCreditMapper.resetContinuousOnTimeCount(userId);
        
        subtractScore(userId, subtractScore, description, reservationId, "SYSTEM");
    }
    
    /**
     * 爽约（未按时使用且未取消）（扣分）
     */
    @Transactional
    public void noShow(Long userId, Long reservationId) {
        // 重置连续使用次数
        userCreditMapper.resetContinuousOnTimeCount(userId);
        
        subtractScore(userId, creditRuleConfig.getSubtract().getNoShow(), 
                "爽约（未按时使用且未取消）", reservationId, "SYSTEM");
    }
    
    /**
     * 管理员手动调整分数
     */
    @Transactional
    public void adjustByAdmin(Long userId, Integer score, String description, String operator) {
        if (score > 0) {
            addScore(userId, score, description, null, operator);
        } else if (score < 0) {
            subtractScore(userId, Math.abs(score), description, null, operator);
        }
    }
    
    /**
     * 加分操作
     */
    private void addScore(Long userId, Integer score, String description, Long relatedId, String operator) {
        if (score <= 0) {
            return;
        }
        
        // 初始化信誉分（如果不存在）
        initCredit(userId);
        
        // 获取加分前的分数
        UserCredit credit = userCreditMapper.findByUserId(userId);
        Integer scoreBefore = credit.getScore();
        
        // 执行加分
        int result = userCreditMapper.addScore(userId, score);
        
        if (result > 0) {
            // 记录日志
            UserCreditLog creditLog = new UserCreditLog();
            creditLog.setUserId(userId);
            creditLog.setScoreBefore(scoreBefore);
            creditLog.setScoreAfter(scoreBefore + score);
            creditLog.setChangeScore(score);
            creditLog.setChangeType(1); // 1-加分
            creditLog.setRelatedId(relatedId);
            creditLog.setDescription(description);
            creditLog.setOperator(operator);
            
            userCreditLogMapper.insert(creditLog);
            
            log.info("用户 {} 信誉分 +{}，当前分数：{}", userId, score, scoreBefore + score);
        }
    }
    
    /**
     * 减分操作
     */
    private void subtractScore(Long userId, Integer score, String description, Long relatedId, String operator) {
        if (score <= 0) {
            return;
        }
        
        // 初始化信誉分（如果不存在）
        initCredit(userId);
        
        // 获取减分前的分数
        UserCredit credit = userCreditMapper.findByUserId(userId);
        Integer scoreBefore = credit.getScore();
        
        // 执行减分
        int result = userCreditMapper.subtractScore(userId, score);
        
        if (result > 0) {
            // 记录日志
            UserCreditLog creditLog = new UserCreditLog();
            creditLog.setUserId(userId);
            creditLog.setScoreBefore(scoreBefore);
            creditLog.setScoreAfter(scoreBefore - score);
            creditLog.setChangeScore(-score);
            creditLog.setChangeType(2); // 2-减分
            creditLog.setRelatedId(relatedId);
            creditLog.setDescription(description);
            creditLog.setOperator(operator);
            
            userCreditLogMapper.insert(creditLog);
            
            log.info("用户 {} 信誉分 -{}，当前分数：{}", userId, score, scoreBefore - score);
        }
    }
    
    /**
     * 查询用户信誉分变动记录
     */
    public List<UserCreditLog> getCreditLogs(Long userId, Integer page, Integer pageSize) {
        // TODO: 实现分页查询
        return userCreditLogMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserCreditLog>()
                .eq(UserCreditLog::getUserId, userId)
                .orderByDesc(UserCreditLog::getCreateTime)
                .last(page != null && pageSize != null ? "LIMIT " + ((page - 1) * pageSize) + "," + pageSize : "")
        );
    }
    
    /**
     * 检查用户本月是否参加过安全培训
     */
    public boolean hasTrainingRecordThisMonth(Long userId) {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        
        Long count = userCreditLogMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserCreditLog>()
                .eq(UserCreditLog::getUserId, userId)
                .eq(UserCreditLog::getDescription, "参加实验室安全培训")
                .ge(UserCreditLog::getCreateTime, startOfMonth)
        );
        
        return count > 0;
    }
    
    /**
     * 获取信誉分统计信息
     */
    public Map<String, Object> getCreditStats(Long userId) {
        UserCredit credit = getUserCredit(userId);
        Map<String, Object> stats = new HashMap<>();
        
        if (credit != null) {
            stats.put("score", credit.getScore());
            stats.put("level", credit.getLevel());
            stats.put("levelText", getLevelText(credit.getLevel()));
            stats.put("maxScore", credit.getMaxScore());
            stats.put("totalAddTimes", credit.getTotalAddTimes());
            stats.put("totalSubtractTimes", credit.getTotalSubtractTimes());
            stats.put("continuousOnTimeCount", credit.getContinuousOnTimeCount());
        }
        
        return stats;
    }
    
    /**
     * 获取信用等级文本
     */
    private String getLevelText(Integer level) {
        switch (level) {
            case 0: return "差";
            case 1: return "中";
            case 2: return "良";
            case 3: return "优";
            case 4: return "极好";
            default: return "未知";
        }
    }
}
