package com.example.shiyanshi.controller;

import com.example.shiyanshi.common.Result;
import com.example.shiyanshi.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据可视化大屏控制器
 */
@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    /**
     * 获取核心指标卡片数据
     * GET /api/dashboard/core-metrics
     */
    @GetMapping("/core-metrics")
    public Result getCoreMetrics() {
        try {
            Map<String, Object> metrics = dashboardService.getCoreMetrics();
            return Result.success(metrics);
        } catch (Exception e) {
            return Result.error("获取核心指标失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取预约趋势数据（折线图）
     * GET /api/dashboard/reservation-trend
     */
    @GetMapping("/reservation-trend")
    public Result getReservationTrend() {
        try {
            List<Map<String, Object>> trend = dashboardService.getReservationTrend();
            return Result.success(trend);
        } catch (Exception e) {
            return Result.error("获取预约趋势失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取实验室利用率排行（柱状图）
     * GET /api/dashboard/lab-utilization
     */
    @GetMapping("/lab-utilization")
    public Result getLabUtilization() {
        try {
            List<Map<String, Object>> rank = dashboardService.getLabUtilizationRank();
            return Result.success(rank);
        } catch (Exception e) {
            return Result.error("获取实验室利用率失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取时间段热度分布（热力图）
     * GET /api/dashboard/time-slot-heatmap
     */
    @GetMapping("/time-slot-heatmap")
    public Result getTimeSlotHeatmap() {
        try {
            List<Map<String, Object>> heatmap = dashboardService.getTimeSlotHeatmap();
            return Result.success(heatmap);
        } catch (Exception e) {
            return Result.error("获取时间段热度失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户类型分布（饼图）
     * GET /api/dashboard/user-type-distribution
     */
    @GetMapping("/user-type-distribution")
    public Result getUserTypeDistribution() {
        try {
            List<Map<String, Object>> distribution = dashboardService.getUserTypeDistribution();
            return Result.success(distribution);
        } catch (Exception e) {
            return Result.error("获取用户类型分布失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取预约状态分布（饼图）
     * GET /api/dashboard/status-distribution
     */
    @GetMapping("/status-distribution")
    public Result getStatusDistribution() {
        try {
            List<Map<String, Object>> distribution = dashboardService.getReservationStatusDistribution();
            return Result.success(distribution);
        } catch (Exception e) {
            return Result.error("获取状态分布失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取信用等级分布（柱状图）
     * GET /api/dashboard/credit-level-distribution
     */
    @GetMapping("/credit-level-distribution")
    public Result getCreditLevelDistribution() {
        try {
            List<Map<String, Object>> distribution = dashboardService.getCreditLevelDistribution();
            return Result.success(distribution);
        } catch (Exception e) {
            return Result.error("获取信用等级分布失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取周几预约分布（柱状图）
     * GET /api/dashboard/weekday-distribution
     */
    @GetMapping("/weekday-distribution")
    public Result getWeekdayDistribution() {
        try {
            List<Map<String, Object>> distribution = dashboardService.getWeekdayDistribution();
            return Result.success(distribution);
        } catch (Exception e) {
            return Result.error("获取周几分布失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取学院预约排行（柱状图）
     * GET /api/dashboard/college-rank
     */
    @GetMapping("/college-rank")
    public Result getCollegeRank() {
        try {
            List<Map<String, Object>> rank = dashboardService.getCollegeRank();
            return Result.success(rank);
        } catch (Exception e) {
            return Result.error("获取学院排行失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取实时动态
     * GET /api/dashboard/recent-activities
     */
    @GetMapping("/recent-activities")
    public Result getRecentActivities() {
        try {
            List<Map<String, Object>> activities = dashboardService.getRecentActivities();
            return Result.success(activities);
        } catch (Exception e) {
            return Result.error("获取实时动态失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取实验室容量使用率
     * GET /api/dashboard/capacity-usage
     */
    @GetMapping("/capacity-usage")
    public Result getCapacityUsage() {
        try {
            List<Map<String, Object>> usage = dashboardService.getLabCapacityUsage();
            return Result.success(usage);
        } catch (Exception e) {
            return Result.error("获取容量使用率失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取完整大屏数据（一次性获取所有数据）
     * GET /api/dashboard/all
     */
    @GetMapping("/all")
    public Result getAllData() {
        try {
            Map<String, Object> allData = new HashMap<>();
            allData.put("coreMetrics", dashboardService.getCoreMetrics());
            allData.put("reservationTrend", dashboardService.getReservationTrend());
            allData.put("labUtilization", dashboardService.getLabUtilizationRank());
            allData.put("timeSlotHeatmap", dashboardService.getTimeSlotHeatmap());
            allData.put("userTypeDistribution", dashboardService.getUserTypeDistribution());
            allData.put("statusDistribution", dashboardService.getReservationStatusDistribution());
            allData.put("creditLevelDistribution", dashboardService.getCreditLevelDistribution());
            allData.put("weekdayDistribution", dashboardService.getWeekdayDistribution());
            allData.put("collegeRank", dashboardService.getCollegeRank());
            allData.put("recentActivities", dashboardService.getRecentActivities());
            
            return Result.success(allData);
        } catch (Exception e) {
            return Result.error("获取大屏数据失败：" + e.getMessage());
        }
    }
}
