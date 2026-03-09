package com.example.shiyanshi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户信誉分变动记录实体类
 * 用于记录每次信誉分的增减操作，便于审计和追溯
 */
@Data
@TableName("user_credit_log")
public class UserCreditLog {
    
    /**
     * 记录 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户 ID
     */
    private Long userId;
    
    /**
     * 变动前分数
     */
    private Integer scoreBefore;
    
    /**
     * 变动后分数
     */
    private Integer scoreAfter;
    
    /**
     * 变动分数（正数为加分，负数为扣分）
     */
    private Integer changeScore;
    
    /**
     * 变动类型：1-预约成功 2-准时使用 3-取消预约 4-爽约 5-管理员调整 6-其他
     */
    private Integer changeType;
    
    /**
     * 关联业务 ID（如预约 ID）
     */
    private Long relatedId;
    
    /**
     * 变动说明
     */
    private String description;
    
    /**
     * 操作人（系统自动或管理员手动）
     */
    private String operator;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
