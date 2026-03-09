package com.example.shiyanshi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户信誉分实体类
 * 用于记录和管理用户的信用积分，防止恶意预约、爽约等行为
 */
@Data
@TableName("user_credit")
public class UserCredit {
    
    /**
     * 信誉 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户 ID
     */
    private Long userId;
    
    /**
     * 当前信誉分（初始 100 分，范围 0-150）
     */
    private Integer score;
    
    /**
     * 历史最高分
     */
    private Integer maxScore;
    
    /**
     * 累计加分次数
     */
    private Integer totalAddTimes;
    
    /**
     * 累计扣分次数
     */
    private Integer totalSubtractTimes;
    
    /**
     * 连续准时使用次数（用于额外奖励）
     */
    private Integer continuousOnTimeCount;
    
    /**
     * 最后一次变动时间
     */
    private LocalDateTime lastChangeTime;
    
    /**
     * 信誉等级：0-差 (0-59), 1-中 (60-79), 2-良 (80-99), 3-优 (100-119), 4-极好 (120+)
     */
    private Integer level;
    
    /**
     * 备注说明
     */
    private String remark;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
