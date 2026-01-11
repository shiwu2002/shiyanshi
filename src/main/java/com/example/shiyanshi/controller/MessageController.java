package com.example.shiyanshi.controller;

import com.example.shiyanshi.common.Result;
import com.example.shiyanshi.entity.Message;
import com.example.shiyanshi.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 站内消息控制器
 */
@RestController
@RequestMapping("/api/messages")
@CrossOrigin
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    /**
     * 发送系统消息
     */
    @PostMapping("/system")
    public Result<Message> sendSystemMessage(@RequestBody Map<String, Object> params) {
        try {
            Long receiverId = Long.valueOf(params.get("receiverId").toString());
            String title = params.get("title").toString();
            String content = params.get("content").toString();
            Integer priority = params.containsKey("priority") ? 
                Integer.valueOf(params.get("priority").toString()) : 0;
            
            Message message = messageService.sendSystemMessage(receiverId, title, content, priority);
            return Result.success("系统消息发送成功", message);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 发送用户消息
     */
    @PostMapping("/user")
    public Result<Message> sendUserMessage(@RequestBody Map<String, Object> params) {
        try {
            Long senderId = Long.valueOf(params.get("senderId").toString());
            Long receiverId = Long.valueOf(params.get("receiverId").toString());
            String title = params.get("title").toString();
            String content = params.get("content").toString();
            
            Message message = messageService.sendUserMessage(senderId, receiverId, title, content);
            return Result.success("消息发送成功", message);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户的所有消息
     */
    @GetMapping("/list/{userId}")
    public Result<List<Message>> getUserMessages(@PathVariable Long userId) {
        try {
            List<Message> messages = messageService.getUserMessages(userId);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户的未读消息
     */
    @GetMapping("/unread/{userId}")
    public Result<List<Message>> getUnreadMessages(@PathVariable Long userId) {
        try {
            List<Message> messages = messageService.getUnreadMessages(userId);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据类型获取用户消息
     */
    @GetMapping("/list/{userId}/type/{messageType}")
    public Result<List<Message>> getUserMessagesByType(
            @PathVariable Long userId, 
            @PathVariable String messageType) {
        try {
            List<Message> messages = messageService.getUserMessagesByType(userId, messageType);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread-count/{userId}")
    public Result<Integer> getUnreadCount(@PathVariable Long userId) {
        try {
            int count = messageService.getUnreadCount(userId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取各类型未读消息数量统计
     */
    @GetMapping("/unread-count-by-types/{userId}")
    public Result<Map<String, Integer>> getUnreadCountByTypes(@PathVariable Long userId) {
        try {
            Map<String, Integer> counts = messageService.getUnreadCountByTypes(userId);
            return Result.success(counts);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取消息详情（自动标记为已读）
     */
    @GetMapping("/detail/{messageId}")
    public Result<Message> getMessageDetail(
            @PathVariable Long messageId,
            @RequestParam Long userId) {
        try {
            Message message = messageService.getMessageDetail(messageId, userId);
            if (message == null) {
                return Result.error("消息不存在或无权访问");
            }
            return Result.success(message);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 标记消息为已读
     */
    @PutMapping("/mark-read/{messageId}")
    public Result<String> markAsRead(
            @PathVariable Long messageId,
            @RequestParam Long userId) {
        try {
            boolean success = messageService.markAsRead(messageId, userId);
            if (success) {
                return Result.success("已标记为已读");
            } else {
                return Result.error("标记失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量标记消息为已读
     */
    @PutMapping("/batch-mark-read")
    public Result<String> batchMarkAsRead(@RequestBody Map<String, Object> params) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> messageIds = (List<Long>) params.get("messageIds");
            Long userId = Long.valueOf(params.get("userId").toString());
            
            boolean success = messageService.batchMarkAsRead(messageIds, userId);
            if (success) {
                return Result.success("已批量标记为已读");
            } else {
                return Result.error("批量标记失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 标记所有消息为已读
     */
    @PutMapping("/mark-all-read/{userId}")
    public Result<String> markAllAsRead(@PathVariable Long userId) {
        try {
            boolean success = messageService.markAllAsRead(userId);
            if (success) {
                return Result.success("已标记所有消息为已读");
            } else {
                return Result.error("标记失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除消息
     */
    @DeleteMapping("/{messageId}")
    public Result<String> deleteMessage(
            @PathVariable Long messageId,
            @RequestParam Long userId) {
        try {
            boolean success = messageService.deleteMessage(messageId, userId);
            if (success) {
                return Result.success("消息删除成功");
            } else {
                return Result.error("消息删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量删除消息
     */
    @DeleteMapping("/batch")
    public Result<String> batchDeleteMessages(@RequestBody Map<String, Object> params) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> messageIds = (List<Long>) params.get("messageIds");
            Long userId = Long.valueOf(params.get("userId").toString());
            
            boolean success = messageService.batchDeleteMessages(messageIds, userId);
            if (success) {
                return Result.success("消息批量删除成功");
            } else {
                return Result.error("消息批量删除失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户发送的消息列表
     */
    @GetMapping("/sent/{userId}")
    public Result<List<Message>> getSentMessages(@PathVariable Long userId) {
        try {
            List<Message> messages = messageService.getSentMessages(userId);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询用户消息
     */
    @GetMapping("/page/{userId}")
    public Result<List<Message>> getUserMessagesWithPage(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            List<Message> messages = messageService.getUserMessagesWithPage(userId, page, pageSize);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据优先级获取消息
     */
    @GetMapping("/priority/{userId}/{priority}")
    public Result<List<Message>> getMessagesByPriority(
            @PathVariable Long userId,
            @PathVariable Integer priority) {
        try {
            List<Message> messages = messageService.getMessagesByPriority(userId, priority);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取高优先级未读消息
     */
    @GetMapping("/high-priority-unread/{userId}")
    public Result<List<Message>> getHighPriorityUnreadMessages(@PathVariable Long userId) {
        try {
            List<Message> messages = messageService.getHighPriorityUnreadMessages(userId);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
