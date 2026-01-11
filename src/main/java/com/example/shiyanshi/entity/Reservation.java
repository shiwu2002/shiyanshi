package com.example.shiyanshi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约记录实体类
 */
@Data
@TableName("reservation")
public class Reservation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;             // 预约用户ID
    
    @TableField(exist = false)
    private String userName;         // 预约用户姓名（关联查询字段，不对应数据库列）
    
    private Long labId;              // 实验室ID
    
    @TableField(exist = false)
    private String labName;          // 实验室名称（关联查询字段，不对应数据库列）
    
    private LocalDate reserveDate;   // 预约日期
    private String timeSlot;         // 时间段：如 08:00-10:00
    private Integer peopleNum;       // 使用人数
    private String purpose;          // 使用目的
    private String experimentName;   // 实验名称
    private String equipment;        // 需要的设备
    private Integer status;          // 状态：0-待审核 1-已通过 2-已拒绝 3-已取消 4-已完成
    private String approver;         // 审核人
    private String approveComment;   // 审核意见
    private LocalDateTime approveTime; // 审核时间
    private String cancelReason;     // 取消原因
    private Integer rating;          // 评分：1-5分
    private String comment;          // 使用评价
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
