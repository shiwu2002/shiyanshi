package com.example.shiyanshi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 站内消息实体类
 */
@Data
@TableName("message")
public class Message {
    
    /**
     * 消息ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 发送者用户ID（系统消息时为null或0）
     */
    private Long senderId;
    
    /**
     * 发送者用户名
     */
    private String senderName;
    
    /**
     * 接收者用户ID
     */
    private Long receiverId;
    
    /**
     * 接收者用户名
     */
    private String receiverName;
    
    /**
     * 消息类型：system-系统消息, reservation-预约通知, approval-审核通知, reminder-提醒消息, user-用户消息
     */
    private String messageType;
    
    /**
     * 消息标题
     */
    private String title;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 相关业务ID（预约ID、实验室ID等）
     */
    private Long relatedId;
    
    /**
     * 相关业务类型（reservation、laboratory等）
     */
    private String relatedType;
    
    /**
     * 已读状态：0-未读 1-已读
     */
    private Integer isRead;
    
    /**
     * 消息优先级：0-普通 1-重要 2-紧急
     */
    private Integer priority;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 阅读时间
     */
    private LocalDateTime readTime;
    
    /**
     * 是否删除：0-未删除 1-已删除
     */
    private Integer deleted;
}
