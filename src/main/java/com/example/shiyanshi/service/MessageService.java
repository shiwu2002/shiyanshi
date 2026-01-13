package com.example.shiyanshi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.shiyanshi.entity.Message;
import com.example.shiyanshi.entity.User;
import com.example.shiyanshi.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 站内消息服务类
 */
@Service
public class MessageService extends ServiceImpl<MessageMapper, Message> {
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private UserService userService;
    
    /**
     * 发送系统消息（单个用户）
     */
    @Transactional
    public Message sendSystemMessage(Long receiverId, String title, String content, Integer priority) {
        User receiver = userService.findById(receiverId);
        if (receiver == null) {
            throw new RuntimeException("接收者不存在");
        }
        
        Message message = new Message();
        message.setSenderId(0L);
        message.setSenderName("系统");
        message.setReceiverId(receiverId);
        message.setReceiverName(receiver.getUsername());
        message.setMessageType("system");
        message.setTitle(title);
        message.setContent(content);
        message.setIsRead(0);
        message.setPriority(priority != null ? priority : 0);
        message.setCreateTime(LocalDateTime.now());
        message.setDeleted(0);
        
        messageMapper.insert(message);
        return message;
    }
    
    /**
     * 广播系统消息给所有用户
     */
    @Transactional
    public int broadcastSystemMessage(String title, String content, Integer priority) {
        // 获取所有用户
        List<User> allUsers = userService.findAll();
        if (allUsers == null || allUsers.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        LocalDateTime now = LocalDateTime.now();
        
        // 为每个用户创建一条系统消息
        for (User user : allUsers) {
            Message message = new Message();
            message.setSenderId(0L);
            message.setSenderName("系统");
            message.setReceiverId(user.getId());
            message.setReceiverName(user.getUsername());
            message.setMessageType("system");
            message.setTitle(title);
            message.setContent(content);
            message.setIsRead(0);
            message.setPriority(priority != null ? priority : 0);
            message.setCreateTime(now);
            message.setDeleted(0);
            
            messageMapper.insert(message);
            count++;
        }
        
        return count;
    }
    
    /**
     * 发送用户消息
     */
    @Transactional
    public Message sendUserMessage(Long senderId, Long receiverId, String title, String content) {
        User sender = userService.findById(senderId);
        User receiver = userService.findById(receiverId);
        
        if (sender == null || receiver == null) {
            throw new RuntimeException("发送者或接收者不存在");
        }
        
        Message message = new Message();
        message.setSenderId(senderId);
        message.setSenderName(sender.getUsername());
        message.setReceiverId(receiverId);
        message.setReceiverName(receiver.getUsername());
        message.setMessageType("user");
        message.setTitle(title);
        message.setContent(content);
        message.setIsRead(0);
        message.setPriority(0);
        message.setCreateTime(LocalDateTime.now());
        message.setDeleted(0);
        
        messageMapper.insert(message);
        return message;
    }
    
    /**
     * 发送预约通知消息
     */
    @Transactional
    public Message sendReservationMessage(Long receiverId, String title, String content, Long reservationId) {
        User receiver = userService.findById(receiverId);
        if (receiver == null) {
            throw new RuntimeException("接收者不存在");
        }
        
        Message message = new Message();
        message.setSenderId(0L);
        message.setSenderName("系统");
        message.setReceiverId(receiverId);
        message.setReceiverName(receiver.getUsername());
        message.setMessageType("reservation");
        message.setTitle(title);
        message.setContent(content);
        message.setRelatedId(reservationId);
        message.setRelatedType("reservation");
        message.setIsRead(0);
        message.setPriority(1);
        message.setCreateTime(LocalDateTime.now());
        message.setDeleted(0);
        
        messageMapper.insert(message);
        return message;
    }
    
    /**
     * 发送审核通知消息
     */
    @Transactional
    public Message sendApprovalMessage(Long receiverId, String title, String content, Long reservationId, Integer priority) {
        User receiver = userService.findById(receiverId);
        if (receiver == null) {
            throw new RuntimeException("接收者不存在");
        }
        
        Message message = new Message();
        message.setSenderId(0L);
        message.setSenderName("系统");
        message.setReceiverId(receiverId);
        message.setReceiverName(receiver.getUsername());
        message.setMessageType("approval");
        message.setTitle(title);
        message.setContent(content);
        message.setRelatedId(reservationId);
        message.setRelatedType("reservation");
        message.setIsRead(0);
        message.setPriority(priority != null ? priority : 1);
        message.setCreateTime(LocalDateTime.now());
        message.setDeleted(0);
        
        messageMapper.insert(message);
        return message;
    }
    
    /**
     * 发送提醒消息
     */
    @Transactional
    public Message sendReminderMessage(Long receiverId, String title, String content, Long relatedId, String relatedType) {
        User receiver = userService.findById(receiverId);
        if (receiver == null) {
            throw new RuntimeException("接收者不存在");
        }
        
        Message message = new Message();
        message.setSenderId(0L);
        message.setSenderName("系统");
        message.setReceiverId(receiverId);
        message.setReceiverName(receiver.getUsername());
        message.setMessageType("reminder");
        message.setTitle(title);
        message.setContent(content);
        message.setRelatedId(relatedId);
        message.setRelatedType(relatedType);
        message.setIsRead(0);
        message.setPriority(2);
        message.setCreateTime(LocalDateTime.now());
        message.setDeleted(0);
        
        messageMapper.insert(message);
        return message;
    }
    
    /**
     * 获取用户的所有消息
     */
    public List<Message> getUserMessages(Long userId) {
        return messageMapper.findByReceiverId(userId);
    }
    
    /**
     * 获取用户的未读消息
     */
    public List<Message> getUnreadMessages(Long userId) {
        return messageMapper.findUnreadByReceiverId(userId);
    }
    
    /**
     * 根据类型获取用户消息
     */
    public List<Message> getUserMessagesByType(Long userId, String messageType) {
        return messageMapper.findByReceiverIdAndType(userId, messageType);
    }
    
    /**
     * 获取用户的未读消息数量
     */
    public int getUnreadCount(Long userId) {
        return messageMapper.countUnreadByReceiverId(userId);
    }
    
    /**
     * 根据类型获取用户的未读消息数量
     */
    public int getUnreadCountByType(Long userId, String messageType) {
        return messageMapper.countUnreadByReceiverIdAndType(userId, messageType);
    }
    
    /**
     * 获取各类型未读消息数量统计
     */
    public Map<String, Integer> getUnreadCountByTypes(Long userId) {
        Map<String, Integer> result = new HashMap<>();
        result.put("total", getUnreadCount(userId));
        result.put("system", getUnreadCountByType(userId, "system"));
        result.put("reservation", getUnreadCountByType(userId, "reservation"));
        result.put("approval", getUnreadCountByType(userId, "approval"));
        result.put("reminder", getUnreadCountByType(userId, "reminder"));
        result.put("user", getUnreadCountByType(userId, "user"));
        return result;
    }
    
    /**
     * 标记消息为已读
     */
    @Transactional
    public boolean markAsRead(Long messageId, Long userId) {
        Message message = messageMapper.selectById(messageId);
        if (message == null || !message.getReceiverId().equals(userId)) {
            return false;
        }
        return messageMapper.markAsRead(messageId) > 0;
    }
    
    /**
     * 批量标记消息为已读
     */
    @Transactional
    public boolean batchMarkAsRead(List<Long> messageIds, Long userId) {
        // 验证所有消息都属于该用户
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Message::getId, messageIds)
               .eq(Message::getReceiverId, userId);
        long count = messageMapper.selectCount(wrapper);
        
        if (count != messageIds.size()) {
            throw new RuntimeException("部分消息不属于当前用户");
        }
        
        return messageMapper.batchMarkAsRead(messageIds) > 0;
    }
    
    /**
     * 标记所有消息为已读
     */
    @Transactional
    public boolean markAllAsRead(Long userId) {
        return messageMapper.markAllAsReadByReceiverId(userId) >= 0;
    }
    
    /**
     * 删除消息（软删除）
     */
    @Transactional
    public boolean deleteMessage(Long messageId, Long userId) {
        Message message = messageMapper.selectById(messageId);
        if (message == null || !message.getReceiverId().equals(userId)) {
            return false;
        }
        return messageMapper.softDelete(messageId) > 0;
    }
    
    /**
     * 批量删除消息（软删除）
     */
    @Transactional
    public boolean batchDeleteMessages(List<Long> messageIds, Long userId) {
        // 验证所有消息都属于该用户
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Message::getId, messageIds)
               .eq(Message::getReceiverId, userId);
        long count = messageMapper.selectCount(wrapper);
        
        if (count != messageIds.size()) {
            throw new RuntimeException("部分消息不属于当前用户");
        }
        
        return messageMapper.batchSoftDelete(messageIds) > 0;
    }
    
    /**
     * 获取消息详情
     */
    public Message getMessageDetail(Long messageId, Long userId) {
        Message message = messageMapper.selectById(messageId);
        if (message == null || !message.getReceiverId().equals(userId)) {
            return null;
        }
        
        // 自动标记为已读
        if (message.getIsRead() == 0) {
            messageMapper.markAsRead(messageId);
            message.setIsRead(1);
            message.setReadTime(LocalDateTime.now());
        }
        
        return message;
    }
    
    /**
     * 获取用户发送的消息列表
     */
    public List<Message> getSentMessages(Long userId) {
        return messageMapper.findBySenderId(userId);
    }
    
    /**
     * 根据相关业务获取消息列表
     */
    public List<Message> getMessagesByRelated(Long relatedId, String relatedType) {
        return messageMapper.findByRelated(relatedId, relatedType);
    }
    
    /**
     * 分页查询用户消息
     */
    public List<Message> getUserMessagesWithPage(Long userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return messageMapper.findByReceiverIdWithPage(userId, offset, pageSize);
    }
    
    /**
     * 根据优先级获取消息
     */
    public List<Message> getMessagesByPriority(Long userId, Integer priority) {
        return messageMapper.findByReceiverIdAndPriority(userId, priority);
    }
    
    /**
     * 获取高优先级未读消息
     */
    public List<Message> getHighPriorityUnreadMessages(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId)
               .eq(Message::getIsRead, 0)
               .eq(Message::getDeleted, 0)
               .in(Message::getPriority, 1, 2)
               .orderByDesc(Message::getPriority)
               .orderByDesc(Message::getCreateTime);
        return messageMapper.selectList(wrapper);
    }
}
