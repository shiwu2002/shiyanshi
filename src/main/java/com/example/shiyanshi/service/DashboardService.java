package com.example.shiyanshi.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据可视化大屏服务
 * 提供各类统计数据用于 ECharts 图表展示
 */
@Slf4j
@Service
public class DashboardService {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 获取核心指标卡片数据
     */
    public Map<String, Object> getCoreMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // 总用户数
        Integer totalUsers = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM user WHERE status = 1", Integer.class);
        metrics.put("totalUsers", totalUsers != null ? totalUsers : 0);
        
        // 总实验室数
        Integer totalLabs = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM laboratory WHERE status = 1", Integer.class);
        metrics.put("totalLabs", totalLabs != null ? totalLabs : 0);
        
        // 总预约次数
        Integer totalReservations = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM reservation", Integer.class);
        metrics.put("totalReservations", totalReservations != null ? totalReservations : 0);
        
        // 今日预约次数
        Integer todayReservations = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM reservation WHERE reserve_date = CURDATE()", Integer.class);
        metrics.put("todayReservations", todayReservations != null ? todayReservations : 0);
        
        // 待审核预约数
        Integer pendingReservations = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM reservation WHERE status = 0", Integer.class);
        metrics.put("pendingReservations", pendingReservations != null ? pendingReservations : 0);
        
        // 平均信誉分
        Double avgCreditScore = jdbcTemplate.queryForObject(
            "SELECT AVG(score) FROM user_credit", Double.class);
        metrics.put("avgCreditScore", avgCreditScore != null ? String.format("%.1f", avgCreditScore) : "100.0");
        
        return metrics;
    }
    
    /**
     * 预约趋势（最近 30 天）
     */
    public List<Map<String, Object>> getReservationTrend() {
        return jdbcTemplate.queryForList(
            "SELECT DATE_FORMAT(reserve_date, '%Y-%m-%d') as date, " +
            "       COUNT(*) as count " +
            "FROM reservation " +
            "WHERE reserve_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
            "GROUP BY reserve_date " +
            "ORDER BY reserve_date ASC"
        );
    }
    
    /**
     * 实验室利用率排行榜（Top 10）
     */
    public List<Map<String, Object>> getLabUtilizationRank() {
        return jdbcTemplate.queryForList(
            "SELECT l.lab_name, l.lab_number, " +
            "       COUNT(r.id) as reservation_count, " +
            "       COALESCE(AVG(r.rating), 0) as avg_rating " +
            "FROM laboratory l " +
            "LEFT JOIN reservation r ON l.id = r.lab_id AND r.status IN (1, 4) " +
            "WHERE l.status = 1 " +
            "GROUP BY l.id, l.lab_name, l.lab_number " +
            "ORDER BY reservation_count DESC " +
            "LIMIT 10"
        );
    }
    
    /**
     * 时间段热度分布
     */
    public List<Map<String, Object>> getTimeSlotHeatmap() {
        return jdbcTemplate.queryForList(
            "SELECT time_slot, " +
            "       COUNT(*) as usage_count, " +
            "       SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as approved_count " +
            "FROM reservation " +
            "WHERE reserve_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
            "GROUP BY time_slot " +
            "ORDER BY usage_count DESC"
        );
    }
    
    /**
     * 用户类型分布
     */
    public List<Map<String, Object>> getUserTypeDistribution() {
        return jdbcTemplate.queryForList(
            "SELECT CASE user_type " +
            "           WHEN 1 THEN '学生' " +
            "           WHEN 2 THEN '教师' " +
            "           WHEN 3 THEN '管理员' " +
            "           ELSE '其他' " +
            "       END as type_name, " +
            "       COUNT(*) as count " +
            "FROM user " +
            "WHERE status = 1 " +
            "GROUP BY user_type"
        );
    }
    
    /**
     * 预约状态分布
     */
    public List<Map<String, Object>> getReservationStatusDistribution() {
        return jdbcTemplate.queryForList(
            "SELECT CASE status " +
            "           WHEN 0 THEN '待审核' " +
            "           WHEN 1 THEN '已通过' " +
            "           WHEN 2 THEN '已拒绝' " +
            "           WHEN 3 THEN '已取消' " +
            "           WHEN 4 THEN '已完成' " +
            "           ELSE '未知' " +
            "       END as status_name, " +
            "       COUNT(*) as count " +
            "FROM reservation " +
            "GROUP BY status"
        );
    }
    
    /**
     * 信用等级分布
     */
    public List<Map<String, Object>> getCreditLevelDistribution() {
        return jdbcTemplate.queryForList(
            "SELECT CASE level " +
            "           WHEN 0 THEN '差 (0-59)' " +
            "           WHEN 1 THEN '中 (60-79)' " +
            "           WHEN 2 THEN '良 (80-99)' " +
            "           WHEN 3 THEN '优 (100-119)' " +
            "           WHEN 4 THEN '极好 (120+)' " +
            "           ELSE '未知' " +
            "       END as level_name, " +
            "       COUNT(*) as count " +
            "FROM user_credit " +
            "GROUP BY level"
        );
    }
    
    /**
     * 周几预约分布（分析工作日 vs 周末）
     */
    public List<Map<String, Object>> getWeekdayDistribution() {
        return jdbcTemplate.queryForList(
            "SELECT DAYNAME(reserve_date) as weekday, " +
            "       COUNT(*) as count " +
            "FROM reservation " +
            "WHERE reserve_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
            "GROUP BY DAYNAME(reserve_date), DAYOFWEEK(reserve_date) " +
            "ORDER BY DAYOFWEEK(reserve_date)"
        );
    }
    
    /**
     * 学院预约排行（Top 10）
     */
    public List<Map<String, Object>> getCollegeRank() {
        return jdbcTemplate.queryForList(
            "SELECT u.college, " +
            "       COUNT(r.id) as reservation_count, " +
            "       COUNT(DISTINCT u.id) as user_count " +
            "FROM user u " +
            "LEFT JOIN reservation r ON u.id = r.user_id " +
            "WHERE u.status = 1 AND u.college IS NOT NULL AND u.college != '' " +
            "GROUP BY u.college " +
            "ORDER BY reservation_count DESC " +
            "LIMIT 10"
        );
    }
    
    /**
     * 获取实时动态（最新 10 条预约）
     */
    public List<Map<String, Object>> getRecentActivities() {
        return jdbcTemplate.queryForList(
            "SELECT r.id, r.reserve_date, r.time_slot, r.status, " +
            "       r.create_time, " +
            "       u.real_name as user_name, " +
            "       l.lab_name, " +
            "       CASE r.status " +
            "           WHEN 0 THEN '待审核' " +
            "           WHEN 1 THEN '已通过' " +
            "           WHEN 2 THEN '已拒绝' " +
            "           WHEN 3 THEN '已取消' " +
            "           WHEN 4 THEN '已完成' " +
            "           ELSE '未知' " +
            "       END as status_text " +
            "FROM reservation r " +
            "JOIN user u ON r.user_id = u.id " +
            "JOIN laboratory l ON r.lab_id = l.id " +
            "ORDER BY r.create_time DESC " +
            "LIMIT 10"
        );
    }
    
    /**
     * 实验室容量使用率
     */
    public List<Map<String, Object>> getLabCapacityUsage() {
        return jdbcTemplate.queryForList(
            "SELECT l.lab_name, l.capacity, " +
            "       COALESCE(SUM(r.people_num), 0) as total_people, " +
            "       ROUND(COALESCE(AVG(r.people_num), 0) / l.capacity * 100, 2) as usage_rate " +
            "FROM laboratory l " +
            "LEFT JOIN reservation r ON l.id = r.lab_id AND r.status IN (1, 4) " +
            "WHERE l.status = 1 " +
            "GROUP BY l.id, l.lab_name, l.capacity " +
            "ORDER BY usage_rate DESC"
        );
    }
}
