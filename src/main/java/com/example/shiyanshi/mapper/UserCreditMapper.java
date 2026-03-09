package com.example.shiyanshi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.shiyanshi.entity.UserCredit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户信誉分 Mapper 接口
 */
@Mapper
public interface UserCreditMapper extends BaseMapper<UserCredit> {
    
    /**
     * 增加信誉分
     * @param userId 用户 ID
     * @param score 增加的分数
     * @return 影响行数
     */
    @Update("UPDATE user_credit SET score = LEAST(score + #{score}, 150), " +
            "max_score = GREATEST(max_score, score + #{score}), " +
            "total_add_times = total_add_times + 1, " +
            "last_change_time = NOW(), " +
            "level = CASE " +
            "  WHEN score + #{score} >= 120 THEN 4 " +
            "  WHEN score + #{score} >= 100 THEN 3 " +
            "  WHEN score + #{score} >= 80 THEN 2 " +
            "  WHEN score + #{score} >= 60 THEN 1 " +
            "  ELSE 0 " +
            "END " +
            "WHERE user_id = #{userId}")
    int addScore(@Param("userId") Long userId, @Param("score") Integer score);
    
    /**
     * 减少信誉分
     * @param userId 用户 ID
     * @param score 减少的分数
     * @return 影响行数
     */
    @Update("UPDATE user_credit SET score = GREATEST(score - #{score}, 0), " +
            "total_subtract_times = total_subtract_times + 1, " +
            "continuous_on_time_count = 0, " +
            "last_change_time = NOW(), " +
            "level = CASE " +
            "  WHEN score - #{score} >= 120 THEN 4 " +
            "  WHEN score - #{score} >= 100 THEN 3 " +
            "  WHEN score - #{score} >= 80 THEN 2 " +
            "  WHEN score - #{score} >= 60 THEN 1 " +
            "  ELSE 0 " +
            "END " +
            "WHERE user_id = #{userId}")
    int subtractScore(@Param("userId") Long userId, @Param("score") Integer score);
    
    /**
     * 增加连续准时使用次数
     * @param userId 用户 ID
     * @return 影响行数
     */
    @Update("UPDATE user_credit SET continuous_on_time_count = continuous_on_time_count + 1, " +
            "last_change_time = NOW() " +
            "WHERE user_id = #{userId}")
    int incrementContinuousOnTimeCount(@Param("userId") Long userId);
    
    /**
     * 重置连续准时使用次数
     * @param userId 用户 ID
     * @return 影响行数
     */
    @Update("UPDATE user_credit SET continuous_on_time_count = 0, " +
            "last_change_time = NOW() " +
            "WHERE user_id = #{userId}")
    int resetContinuousOnTimeCount(@Param("userId") Long userId);
    
    /**
     * 根据用户 ID 查询信誉分
     * @param userId 用户 ID
     * @return 用户信誉分对象
     */
    @Select("SELECT id, user_id, score, max_score, total_add_times, total_subtract_times, " +
            "continuous_on_time_count, last_change_time, level, remark, create_time, update_time " +
            "FROM user_credit WHERE user_id = #{userId}")
    UserCredit findByUserId(@Param("userId") Long userId);
    
    /**
     * 初始化用户信誉分（如果不存在）
     * @param userId 用户 ID
     * @return 影响行数
     */
    @Update("INSERT INTO user_credit (user_id, score, max_score, level) " +
            "VALUES (#{userId}, 100, 100, 3) " +
            "ON DUPLICATE KEY UPDATE score = score")
    int initCreditIfNotExists(@Param("userId") Long userId);
}
