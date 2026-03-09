package com.example.shiyanshi.controller;

import com.example.shiyanshi.annotation.RequirePermission;
import com.example.shiyanshi.common.Result;
import com.example.shiyanshi.entity.UserCredit;
import com.example.shiyanshi.entity.UserCreditLog;
import com.example.shiyanshi.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户信誉分管理控制器
 */
@RestController
@RequestMapping("/api/credit")
@CrossOrigin
public class CreditController {
    
    @Autowired
    private CreditService creditService;
    
    /**
     * 查询当前用户的信誉分
     * GET /api/credit/my
     */
    @GetMapping("/my")
    public Result getMyCredit(jakarta.servlet.http.HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("未获取到用户信息");
            }
            
            UserCredit credit = creditService.getUserCredit(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("credit", credit);
            result.put("stats", creditService.getCreditStats(userId));
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询信誉分失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询用户的信誉分变动记录
     * GET /api/credit/my/logs?page=1&pageSize=10
     */
    @GetMapping("/my/logs")
    public Result getMyCreditLogs(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("未获取到用户信息");
            }
            
            List<UserCreditLog> logs = creditService.getCreditLogs(userId, page, pageSize);
            return Result.success(logs);
        } catch (Exception e) {
            return Result.error("查询信誉分记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理员查询指定用户的信誉分
     * GET /api/credit/user/{userId}
     */
    @RequirePermission(value = 2, description = "查询用户信誉分需要管理员权限")
    @GetMapping("/user/{userId}")
    public Result getUserCredit(@PathVariable Long userId) {
        try {
            UserCredit credit = creditService.getUserCredit(userId);
            Map<String, Object> result = new HashMap<>();
            result.put("credit", credit);
            result.put("stats", creditService.getCreditStats(userId));
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询用户信誉分失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理员查询指定用户的信誉分变动记录
     * GET /api/credit/user/{userId}/logs?page=1&pageSize=10
     */
    @RequirePermission(value = 2, description = "查询用户信誉分记录需要管理员权限")
    @GetMapping("/user/{userId}/logs")
    public Result getUserCreditLogs(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        try {
            List<UserCreditLog> logs = creditService.getCreditLogs(userId, page, pageSize);
            return Result.success(logs);
        } catch (Exception e) {
            return Result.error("查询用户信誉分记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 管理员手动调整用户信誉分
     * POST /api/credit/user/{userId}/adjust
     */
    @RequirePermission(value = 3, description = "调整用户信誉分需要超级管理员权限")
    @PostMapping("/user/{userId}/adjust")
    public Result adjustCredit(
            @PathVariable Long userId,
            @RequestParam Integer score,
            @RequestParam(required = false) String description,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            if (score == 0 || Math.abs(score) > 50) {
                return Result.error("调整分数必须在 -50 到 50 之间且不为 0");
            }
            
            String operator = (String) request.getAttribute("username");
            if (operator == null) {
                operator = "ADMIN";
            }
            
            creditService.adjustByAdmin(userId, score, 
                description != null ? description : "管理员手动调整", operator);
            
            UserCredit credit = creditService.getUserCredit(userId);
            return Result.success("信誉分调整成功，当前分数：" + credit.getScore());
        } catch (Exception e) {
            return Result.error("调整信誉分失败：" + e.getMessage());
        }
    }
    
    /**
     * 参加实验室安全培训恢复信誉分（每月限一次）
     * POST /api/credit/training/recover
     */
    @PostMapping("/training/recover")
    public Result recoverByTraining(jakarta.servlet.http.HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("未获取到用户信息");
            }
            
            // TODO: 检查本月是否已参加过培训
            
            // 恢复 20 分
            creditService.adjustByAdmin(userId, 20, "参加实验室安全培训", "SYSTEM");
            
            return Result.success("培训完成，信誉分 +20");
        } catch (Exception e) {
            return Result.error("培训恢复失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取信誉分规则说明
     * GET /api/credit/rules
     */
    @GetMapping("/rules")
    public Result getRules() {
        Map<String, Object> rules = new HashMap<>();
        
        rules.put("initialScore", 100);
        rules.put("scoreRange", "0-150");
        
        // 信用等级
        Map<String, String> levels = new HashMap<>();
        levels.put("0", "差 (0-59 分) - 禁止预约");
        levels.put("1", "中 (60-79 分) - 每周最多 2 次");
        levels.put("2", "良 (80-99 分) - 每周最多 5 次");
        levels.put("3", "优 (100-119 分) - 每周最多 10 次");
        levels.put("4", "极好 (120+ 分) - 免审核快速通道");
        rules.put("levels", levels);
        
        // 加分规则
        Map<String, Integer> addRules = new HashMap<>();
        addRules.put("预约成功并通过审核", 1);
        addRules.put("准时使用实验室", 2);
        addRules.put("连续 3 次准时使用额外奖励", 3);
        addRules.put("连续 5 次准时使用额外奖励", 5);
        addRules.put("管理员手动奖励", 10);
        rules.put("addRules", addRules);
        
        // 扣分规则
        Map<String, Integer> subtractRules = new HashMap<>();
        subtractRules.put("取消预约（24 小时内）", -1);
        subtractRules.put("临时取消（1 小时内）", -5);
        subtractRules.put("爽约（未按时使用）", -10);
        subtractRules.put("恶意破坏设备或违规操作", -20);
        rules.put("subtractRules", subtractRules);
        
        // 恢复机制
        rules.put("recovery", "信誉分低于 60 分可参加实验室安全培训恢复 20 分（每月限 1 次）");
        
        return Result.success(rules);
    }
}
