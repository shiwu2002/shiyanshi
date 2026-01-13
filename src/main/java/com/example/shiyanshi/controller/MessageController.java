package com.example.shiyanshi.controller;

import com.example.shiyanshi.annotation.RequirePermission;
import com.example.shiyanshi.common.Result;
import com.example.shiyanshi.entity.Message;
import com.example.shiyanshi.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
     * 广播系统消息给所有用户
     */
    @RequirePermission(value = 2, description = "发布系统消息需要管理员及以上权限")
    @PostMapping("/system")
    public Result<Map<String, Object>> sendSystemMessage(@RequestBody Map<String, Object> params) {
        try {
            // 参数验证
            if (!params.containsKey("title")) {
                return Result.error("缺少必填参数: title");
            }
            if (!params.containsKey("content")) {
                return Result.error("缺少必填参数: content");
            }
            
            String title = params.get("title").toString();
            String content = params.get("content").toString();
            Integer priority = params.containsKey("priority") ? 
                Integer.valueOf(params.get("priority").toString()) : 0;
            
            // 广播系统消息给所有用户
            int count = messageService.broadcastSystemMessage(title, content, priority);
            
            Map<String, Object> result = new HashMap<>();
            result.put("count", count);
            result.put("message", "系统消息已发送给 " + count + " 个用户");
            
            return Result.success(result);
        } catch (NumberFormatException e) {
            return Result.error("参数格式错误");
        } catch (Exception e) {
            return Result.error("发送系统消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送用户消息（从token获取发送者ID）
     */
    @PostMapping("/user")
    public Result<Message> sendUserMessage(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            // 从token获取发送者ID
            Object senderIdObj = request.getAttribute("userId");
            if (senderIdObj == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Long senderId = (senderIdObj instanceof Long) ? (Long) senderIdObj : Long.valueOf(senderIdObj.toString());
            
            // 参数验证
            if (!params.containsKey("receiverId")) {
                return Result.error("缺少必填参数: receiverId");
            }
            if (!params.containsKey("title")) {
                return Result.error("缺少必填参数: title");
            }
            if (!params.containsKey("content")) {
                return Result.error("缺少必填参数: content");
            }
            
            Long receiverId = Long.valueOf(params.get("receiverId").toString());
            String title = params.get("title").toString();
            String content = params.get("content").toString();
            
            Message message = messageService.sendUserMessage(senderId, receiverId, title, content);
            return Result.success("消息发送成功", message);
        } catch (NumberFormatException e) {
            return Result.error("receiverId参数格式错误");
        } catch (Exception e) {
            return Result.error("发送消息失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的所有消息（从token获取用户ID）
     */
    @GetMapping("/list")
    public Result<List<Message>> getUserMessages(HttpServletRequest request) {
        try {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
            
            List<Message> messages = messageService.getUserMessages(userId);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户的未读消息（从token获取用户ID）
     */
    @GetMapping("/unread")
    public Result<List<Message>> getUnreadMessages(HttpServletRequest request) {
        try {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
            
            List<Message> messages = messageService.getUnreadMessages(userId);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据类型获取用户消息（从token获取用户ID）
     */
    @GetMapping("/list/type/{messageType}")
    public Result<List<Message>> getUserMessagesByType(
            @PathVariable String messageType,
            HttpServletRequest request) {
        try {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
            
            List<Message> messages = messageService.getUserMessagesByType(userId, messageType);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取未读消息数量（从token获取用户ID）
     */
    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount(HttpServletRequest request) {
        try {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
            
            int count = messageService.getUnreadCount(userId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取各类型未读消息数量统计（从token获取用户ID）
     */
    @GetMapping("/unread-count-by-types")
    public Result<Map<String, Integer>> getUnreadCountByTypes(HttpServletRequest request) {
        try {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
            
            Map<String, Integer> counts = messageService.getUnreadCountByTypes(userId);
            return Result.success(counts);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取消息详情（自动标记为已读，从token获取用户ID）
     */
    @GetMapping("/detail/{messageId}")
    public Result<Message> getMessageDetail(
            @PathVariable Long messageId,
            HttpServletRequest request) {
        try {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
            
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
     * 标记消息为已读（从token获取用户ID）
     */
    @PutMapping("/mark-read/{messageId}")
    public Result<String> markAsRead(
            @PathVariable Long messageId,
            HttpServletRequest request) {
        try {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
            
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
     * 批量标记消息为已读（从token获取用户ID）
     */
    @PutMapping("/batch-mark-read")
    public Result<String> batchMarkAsRead(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
            
            @SuppressWarnings("unchecked")
            List<Long> messageIds = (List<Long>) params.get("messageIds");
            
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
     * 标记所有消息为已读（从token获取用户ID）
     */
    @PutMapping("/mark-all-read")
    public Result<String> markAllAsRead(HttpServletRequest request) {
        try {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
            
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
     * 删除消息（从token获取用户ID）
     */
    @DeleteMapping("/{messageId}")
    public Result<String> deleteMessage(
            @PathVariable Long messageId,
            HttpServletRequest request) {
        try {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
            
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
     * 批量删除消息（从token获取用户ID）
     */
    @DeleteMapping("/batch")
    public Result<String> batchDeleteMessages(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
            
            @SuppressWarnings("unchecked")
            List<Long> messageIds = (List<Long>) params.get("messageIds");
            
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
     * 获取用户发送的消息列表（从token获取用户ID）
     */
    @GetMapping("/sent")
    public Result<List<Message>> getSentMessages(HttpServletRequest request) {
        try {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
            
            List<Message> messages = messageService.getSentMessages(userId);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 分页查询用户消息（从token获取用户ID）
     */
    @GetMapping("/page")
    public Result<List<Message>> getUserMessagesWithPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpServletRequest request) {
        try {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
            
            List<Message> messages = messageService.getUserMessagesWithPage(userId, page, pageSize);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据优先级获取消息（从token获取用户ID）
     */
    @GetMapping("/priority/{priority}")
    public Result<List<Message>> getMessagesByPriority(
            @PathVariable Integer priority,
            HttpServletRequest request) {
        try {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
            
            List<Message> messages = messageService.getMessagesByPriority(userId, priority);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取高优先级未读消息（从token获取用户ID）
     */
    @GetMapping("/high-priority-unread")
    public Result<List<Message>> getHighPriorityUnreadMessages(HttpServletRequest request) {
        try {
            Object userIdObj = request.getAttribute("userId");
            if (userIdObj == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : Long.valueOf(userIdObj.toString());
            
            List<Message> messages = messageService.getHighPriorityUnreadMessages(userId);
            return Result.success(messages);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
