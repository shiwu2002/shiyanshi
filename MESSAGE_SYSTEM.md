# 站内消息系统说明文档

## 概述

站内消息系统是实验室预约系统的核心通知模块，提供系统消息、用户消息、预约通知、审批通知和提醒消息等多种消息类型，支持消息优先级、已读/未读状态管理、软删除等完整功能。

## 技术架构

### 核心技术栈
- Spring Boot 4.0.1
- MyBatis-Plus 3.5.9
- MySQL 8.0+
- Lombok

### 架构层次
```
Controller层(REST API) 
    ↓
Service层(业务逻辑)
    ↓
Mapper层(数据访问)
    ↓
Database(MySQL)
```

## 数据库设计

### message表结构

```sql
CREATE TABLE `message` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    `sender_id` BIGINT COMMENT '发送者ID(系统消息为NULL)',
    `sender_name` VARCHAR(100) COMMENT '发送者名称',
    `receiver_id` BIGINT NOT NULL COMMENT '接收者ID',
    `receiver_name` VARCHAR(100) NOT NULL COMMENT '接收者名称',
    `message_type` VARCHAR(20) NOT NULL COMMENT '消息类型',
    `title` VARCHAR(200) NOT NULL COMMENT '消息标题',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `related_id` BIGINT COMMENT '关联ID',
    `related_type` VARCHAR(50) COMMENT '关联类型',
    `is_read` TINYINT DEFAULT 0 COMMENT '是否已读: 0-未读 1-已读',
    `priority` TINYINT DEFAULT 0 COMMENT '优先级: 0-普通 1-重要 2-紧急',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `read_time` DATETIME COMMENT '阅读时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '删除标记: 0-未删除 1-已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 索引设计

| 索引名 | 字段 | 用途 |
|--------|------|------|
| idx_receiver_id | receiver_id | 查询用户消息 |
| idx_sender_id | sender_id | 查询发送记录 |
| idx_message_type | message_type | 按类型筛选 |
| idx_is_read | is_read | 查询未读消息 |
| idx_priority | priority | 优先级排序 |
| idx_create_time | create_time | 时间排序 |
| idx_deleted | deleted | 软删除过滤 |
| idx_receiver_read | receiver_id, is_read, deleted | 复合查询优化 |

## 消息类型

### 1. system - 系统消息
- **用途**: 系统级通知、公告、维护通知
- **发送者**: 系统(sender_id为NULL)
- **示例**: "系统将于今晚22:00-24:00进行维护"

### 2. user - 用户消息
- **用途**: 用户之间的私信交流
- **发送者**: 普通用户或管理员
- **示例**: "您的预约申请已被审批"

### 3. reservation - 预约通知
- **用途**: 预约相关的通知
- **关联数据**: related_id存储预约ID
- **示例**: "您预约的实验室A101已确认"

### 4. approval - 审批通知
- **用途**: 审批流程通知
- **关联数据**: related_id存储审批ID
- **优先级**: 通常为重要(1)或紧急(2)
- **示例**: "您有一条预约申请待审批"

### 5. reminder - 提醒消息
- **用途**: 定时提醒、到期提醒
- **关联数据**: related_id和related_type存储关联信息
- **示例**: "您预约的实验室将于30分钟后开始"

## 消息优先级

| 级别 | 值 | 说明 | 应用场景 |
|------|-----|------|----------|
| 普通 | 0 | 一般性通知 | 系统公告、普通消息 |
| 重要 | 1 | 需要关注 | 预约确认、审批通知 |
| 紧急 | 2 | 需立即处理 | 紧急通知、即将到期 |

## API接口文档

### 基础路径
```
/api/messages
```

### 1. 发送系统消息

**接口**: `POST /api/messages/system`

**请求参数**:
```json
{
  "receiverId": 1,
  "title": "系统维护通知",
  "content": "系统将于今晚进行维护",
  "priority": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "系统消息发送成功",
  "data": {
    "id": 1,
    "senderId": null,
    "senderName": "系统消息",
    "receiverId": 1,
    "receiverName": "张三",
    "messageType": "system",
    "title": "系统维护通知",
    "content": "系统将于今晚进行维护",
    "isRead": 0,
    "priority": 1,
    "createTime": "2026-01-11T13:00:00"
  }
}
```

### 2. 发送用户消息

**接口**: `POST /api/messages/user`

**请求参数**:
```json
{
  "senderId": 1,
  "receiverId": 2,
  "title": "关于实验室使用",
  "content": "请问实验室A101明天是否可用？"
}
```

### 3. 获取用户所有消息

**接口**: `GET /api/messages/list/{userId}`

**请求示例**: `GET /api/messages/list/1`

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": [
    {
      "id": 1,
      "messageType": "system",
      "title": "系统维护通知",
      "isRead": 0,
      "priority": 1,
      "createTime": "2026-01-11T13:00:00"
    }
  ]
}
```

### 4. 获取未读消息

**接口**: `GET /api/messages/unread/{userId}`

**请求示例**: `GET /api/messages/unread/1`

### 5. 按类型获取消息

**接口**: `GET /api/messages/list/{userId}/type/{messageType}`

**请求示例**: `GET /api/messages/list/1/type/system`

**消息类型值**: system, user, reservation, approval, reminder

### 6. 获取未读消息数量

**接口**: `GET /api/messages/unread-count/{userId}`

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": 5
}
```

### 7. 获取各类型未读数量统计

**接口**: `GET /api/messages/unread-count-by-types/{userId}`

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "system": 2,
    "user": 1,
    "reservation": 1,
    "approval": 1,
    "reminder": 0
  }
}
```

### 8. 获取消息详情

**接口**: `GET /api/messages/detail/{messageId}?userId={userId}`

**请求示例**: `GET /api/messages/detail/1?userId=1`

**功能**: 查看消息详情时自动标记为已读

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "id": 1,
    "senderId": null,
    "senderName": "系统消息",
    "receiverId": 1,
    "receiverName": "张三",
    "messageType": "system",
    "title": "系统维护通知",
    "content": "系统将于今晚22:00-24:00进行维护，届时将无法使用预约功能。",
    "isRead": 1,
    "priority": 1,
    "createTime": "2026-01-11T13:00:00",
    "readTime": "2026-01-11T13:05:00"
  }
}
```

### 9. 标记消息为已读

**接口**: `PUT /api/messages/mark-read/{messageId}?userId={userId}`

**请求示例**: `PUT /api/messages/mark-read/1?userId=1`

### 10. 批量标记已读

**接口**: `PUT /api/messages/batch-mark-read`

**请求参数**:
```json
{
  "userId": 1,
  "messageIds": [1, 2, 3]
}
```

### 11. 全部标记已读

**接口**: `PUT /api/messages/mark-all-read/{userId}`

**请求示例**: `PUT /api/messages/mark-all-read/1`

### 12. 删除消息

**接口**: `DELETE /api/messages/{messageId}?userId={userId}`

**请求示例**: `DELETE /api/messages/1?userId=1`

**说明**: 使用软删除，数据不会真正从数据库删除

### 13. 批量删除消息

**接口**: `DELETE /api/messages/batch`

**请求参数**:
```json
{
  "userId": 1,
  "messageIds": [1, 2, 3]
}
```

### 14. 分页查询消息

**接口**: `GET /api/messages/page/{userId}?page={page}&pageSize={pageSize}`

**请求示例**: `GET /api/messages/page/1?page=1&pageSize=20`

**响应示例**:
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "records": [
      {
        "id": 1,
        "messageType": "system",
        "title": "系统维护通知",
        "isRead": 0,
        "priority": 1,
        "createTime": "2026-01-11T13:00:00"
      }
    ],
    "total": 100,
    "size": 20,
    "current": 1,
    "pages": 5
  }
}
```

### 15. 获取高优先级未读消息

**接口**: `GET /api/messages/high-priority-unread/{userId}`

**请求示例**: `GET /api/messages/high-priority-unread/1`

**说明**: 返回优先级≥1的未读消息

## 核心功能实现

### 1. 消息发送

#### 系统消息发送
```java
// 发送系统消息
Message message = messageService.sendSystemMessage(
    receiverId,        // 接收者ID
    "系统维护通知",     // 标题
    "系统将于今晚维护", // 内容
    1                  // 优先级: 0-普通 1-重要 2-紧急
);
```

#### 用户消息发送
```java
// 发送用户消息
Message message = messageService.sendUserMessage(
    senderId,          // 发送者ID
    receiverId,        // 接收者ID
    "关于实验室使用",   // 标题
    "请问明天是否可用？" // 内容
);
```

#### 预约通知发送
```java
// 发送预约通知
Message message = messageService.sendReservationMessage(
    receiverId,        // 接收者ID
    "预约确认通知",     // 标题
    "您的预约已确认",   // 内容
    reservationId      // 预约ID
);
```

#### 审批通知发送
```java
// 发送审批通知
Message message = messageService.sendApprovalMessage(
    receiverId,        // 接收者ID
    "待审批通知",       // 标题
    "您有新的审批任务", // 内容
    reservationId,     // 关联ID
    1                  // 优先级
);
```

#### 提醒消息发送
```java
// 发送提醒消息
Message message = messageService.sendReminderMessage(
    receiverId,        // 接收者ID
    "预约提醒",         // 标题
    "实验室将于30分钟后开始", // 内容
    relatedId,         // 关联ID
    "reservation"      // 关联类型
);
```

### 2. 消息查询

#### 获取用户消息列表
```java
// 获取用户所有消息
List<Message> messages = messageService.getUserMessages(userId);

// 获取未读消息
List<Message> unreadMessages = messageService.getUnreadMessages(userId);

// 按类型获取消息
List<Message> systemMessages = messageService.getMessagesByType(userId, "system");

// 获取高优先级未读消息
List<Message> highPriorityMessages = messageService.getHighPriorityUnreadMessages(userId);
```

#### 消息统计
```java
// 获取未读消息数量
int unreadCount = messageService.getUnreadCount(userId);

// 获取各类型未读数量统计
Map<String, Integer> countByTypes = messageService.getUnreadCountByTypes(userId);
// 返回: {"system": 2, "user": 1, "reservation": 1, "approval": 1, "reminder": 0}
```

#### 分页查询
```java
// 分页查询消息
Page<Message> messagePage = messageService.getUserMessagesPage(userId, page, pageSize);
```

### 3. 消息状态管理

#### 标记已读
```java
// 单个消息标记已读
boolean success = messageService.markAsRead(messageId, userId);

// 批量标记已读
List<Long> messageIds = Arrays.asList(1L, 2L, 3L);
boolean success = messageService.batchMarkAsRead(messageIds, userId);

// 全部标记已读
boolean success = messageService.markAllAsRead(userId);
```

#### 消息删除
```java
// 单个消息删除(软删除)
boolean success = messageService.deleteMessage(messageId, userId);

// 批量删除
List<Long> messageIds = Arrays.asList(1L, 2L, 3L);
boolean success = messageService.batchDeleteMessages(messageIds, userId);
```

#### 查看消息详情
```java
// 查看消息详情(自动标记为已读)
Message message = messageService.getMessageDetail(messageId, userId);
```

## 权限控制

### 权限规则

1. **发送权限**
   - 系统消息: 仅管理员可发送
   - 用户消息: 所有用户可发送
   - 预约/审批/提醒: 系统自动发送

2. **查看权限**
   - 用户只能查看自己接收的消息
   - 管理员可查看所有消息

3. **操作权限**
   - 标记已读: 仅消息接收者
   - 删除消息: 仅消息接收者
   - 查看详情: 仅消息接收者

### 安全检查

所有操作都会验证userId与receiverId是否匹配:
```java
// 验证消息所有权
Message message = messageMapper.selectById(messageId);
if (!message.getReceiverId().equals(userId)) {
    throw new RuntimeException("无权操作此消息");
}
```

## 业务场景示例

### 场景1: 用户注册成功
```java
// 发送欢迎消息
messageService.sendSystemMessage(
    userId,
    "欢迎使用实验室预约系统",
    "感谢您注册本系统，祝您使用愉快！",
    0 // 普通优先级
);
```

### 场景2: 预约提交成功
```java
// 发送预约确认通知
messageService.sendReservationMessage(
    userId,
    "预约提交成功",
    "您的预约申请已提交，请等待审批",
    reservationId
);

// 发送审批通知给管理员
messageService.sendApprovalMessage(
    adminId,
    "待审批通知",
    "用户" + userName + "提交了新的预约申请",
    reservationId,
    1 // 重要优先级
);
```

### 场景3: 审批通过
```java
// 发送审批结果通知
messageService.sendReservationMessage(
    userId,
    "预约审批通过",
    "您的预约申请已通过审批，请按时使用实验室",
    reservationId
);
```

### 场景4: 预约即将开始
```java
// 发送提醒消息
messageService.sendReminderMessage(
    userId,
    "预约提醒",
    "您预约的实验室" + labName + "将于30分钟后开始，请做好准备",
    reservationId,
    "reservation"
);
```

### 场景5: 管理员发布公告
```java
// 群发系统消息
List<User> allUsers = userService.list();
for (User user : allUsers) {
    messageService.sendSystemMessage(
        user.getId(),
        "系统公告",
        "实验室系统将于本周末升级，敬请期待新功能",
        1 // 重要优先级
    );
}
```

## 前端集成指南

### 1. 消息中心页面

#### 消息列表展示
```javascript
// 获取消息列表
async function loadMessages(page = 1) {
    const response = await fetch(`/api/messages/page/${userId}?page=${page}&pageSize=20`);
    const result = await response.json();
    
    // 渲染消息列表
    renderMessageList(result.data.records);
    
    // 渲染分页
    renderPagination(result.data);
}

// 渲染消息列表
function renderMessageList(messages) {
    const listHtml = messages.map(msg => `
        <div class="message-item ${msg.isRead ? '' : 'unread'}" 
             data-id="${msg.id}"
             onclick="viewMessage(${msg.id})">
            <div class="message-header">
                <span class="message-type ${msg.messageType}">${getTypeLabel(msg.messageType)}</span>
                ${msg.priority > 0 ? `<span class="priority-${msg.priority}">重要</span>` : ''}
            </div>
            <div class="message-title">${msg.title}</div>
            <div class="message-time">${formatTime(msg.createTime)}</div>
        </div>
    `).join('');
    
    document.getElementById('message-list').innerHTML = listHtml;
}
```

#### 未读消息提醒
```javascript
// 获取未读消息数量
async function updateUnreadCount() {
    const response = await fetch(`/api/messages/unread-count/${userId}`);
    const result = await response.json();
    
    // 更新角标
    const badge = document.getElementById('message-badge');
    if (result.data > 0) {
        badge.textContent = result.data;
        badge.style.display = 'inline-block';
    } else {
        badge.style.display = 'none';
    }
}

// 定时更新(每30秒)
setInterval(updateUnreadCount, 30000);
```

#### 消息详情弹窗
```javascript
// 查看消息详情
async function viewMessage(messageId) {
    const response = await fetch(`/api/messages/detail/${messageId}?userId=${userId}`);
    const result = await response.json();
    
    // 显示消息详情
    showMessageDialog(result.data);
    
    // 刷新未读数量
    updateUnreadCount();
}

function showMessageDialog(message) {
    const dialog = document.getElementById('message-dialog');
    dialog.innerHTML = `
        <div class="message-detail">
            <div class="header">
                <span class="type">${getTypeLabel(message.messageType)}</span>
                <span class="time">${formatTime(message.createTime)}</span>
            </div>
            <h3>${message.title}</h3>
            <div class="sender">来自: ${message.senderName}</div>
            <div class="content">${message.content}</div>
            ${message.relatedId ? `<div class="related">相关信息: ${message.relatedType} #${message.relatedId}</div>` : ''}
        </div>
    `;
    dialog.style.display = 'block';
}
```

#### 批量操作
```javascript
// 全部标记已读
async function markAllAsRead() {
    const response = await fetch(`/api/messages/mark-all-read/${userId}`, {
        method: 'PUT'
    });
    
    if (response.ok) {
        loadMessages(); // 刷新列表
        updateUnreadCount(); // 更新未读数量
    }
}

// 批量删除
async function batchDelete(messageIds) {
    const response = await fetch('/api/messages/batch', {
        method: 'DELETE',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({userId, messageIds})
    });
    
    if (response.ok) {
        loadMessages(); // 刷新列表
    }
}
```

### 2. 消息类型筛选
```javascript
// 按类型筛选消息
async function filterByType(messageType) {
    const response = await fetch(`/api/messages/list/${userId}/type/${messageType}`);
    const result = await response.json();
    renderMessageList(result.data);
}

// 类型选择器
const typeSelector = `
    <select onchange="filterByType(this.value)">
        <option value="">全部消息</option>
        <option value="system">系统消息</option>
        <option value="user">用户消息</option>
        <option value="reservation">预约通知</option>
        <option value="approval">审批通知</option>
        <option value="reminder">提醒消息</option>
    </select>
`;
```

### 3. 小程序端实现

#### 消息列表页面
```javascript
// pages/messages/messages.js
Page({
    data: {
        messages: [],
        unreadCount: 0,
        page: 1,
        hasMore: true
    },
    
    onLoad() {
        this.loadMessages();
        this.updateUnreadCount();
    },
    
    async loadMessages() {
        const res = await wx.request({
            url: `/api/messages/page/${userId}`,
            data: {
                page: this.data.page,
                pageSize: 20
            }
        });
        
        this.setData({
            messages: [...this.data.messages, ...res.data.data.records],
            hasMore: res.data.data.current < res.data.data.pages
        });
    },
    
    async updateUnreadCount() {
        const res = await wx.request({
            url: `/api/messages/unread-count/${userId}`
        });
        
        // 更新TabBar角标
        if (res.data.data > 0) {
            wx.setTabBarBadge({
                index: 2, // 消息Tab的索引
                text: String(res.data.data)
            });
        } else {
            wx.removeTabBarBadge({index: 2});
        }
    },
    
    onReachBottom() {
        if (this.data.hasMore) {
            this.setData({page: this.data.page + 1});
            this.loadMessages();
        }
    }
});
```

## 性能优化

### 1. 数据库优化

#### 索引优化
- 为高频查询字段创建索引
- 使用复合索引优化多条件查询
- 定期分析慢查询日志

#### 查询优化
```java
// 使用分页查询避免一次加载大量数据
Page<Message> page = new Page<>(pageNum, pageSize);
messageService.page(page, wrapper);

// 只查询必要字段
wrapper.select("id", "title", "message_type", "is_read", "priority", "create_time");
```

### 2. 缓存策略

#### 未读数量缓存
```java
// 使用Redis缓存未读数量
@Cacheable(value = "message:unread:count", key = "#userId")
public int getUnreadCount(Long userId) {
    return messageMapper.countUnreadByReceiverId(userId);
}

// 标记已读时清除缓存
@CacheEvict(value = "message:unread:count", key = "#userId")
public boolean markAsRead(Long messageId, Long userId) {
    // 标记逻辑
}
```

### 3. 异步处理

#### 群发消息异步化
```java
@Async
public void sendBatchMessages(List<Long> userIds, String title, String content) {
    for (Long userId : userIds) {
        sendSystemMessage(userId, title, content, 0);
    }
}
```

## 监控与日志

### 1. 日志记录
```java
@Slf4j
@Service
public class MessageService {
    public Message sendSystemMessage(Long receiverId, String title, String content, Integer priority) {
        log.info("发送系统消息: receiverId={}, title={}, priority={}", receiverId, title, priority);
        
        try {
            // 发送逻辑
            log.info("系统消息发送成功: messageId={}", message.getId());
            return message;
        } catch (Exception e) {
            log.error("系统消息发送失败: receiverId={}, error={}", receiverId, e.getMessage(), e);
            throw e;
        }
    }
}
```

### 2. 监控指标
- 消息发送成功率
- 消息查询响应时间
- 未读消息数量统计
- 高优先级消息处理时间

## 常见问题

### Q1: 如何实现消息推送？
A: 可以集成以下方案:
- WebSocket: 实时推送
- 小程序订阅消息: 微信小程序推送
- 短信/邮件: 重要消息提醒

### Q2: 如何处理大量历史消息？
A: 
- 使用分页查询
- 实现消息归档功能
- 定期清理已删除消息
- 考虑消息过期策略

### Q3: 如何防止消息轰炸？
A:
- 限制发送频率
- 批量操作合并通知
- 智能消息聚合
- 用户消息偏好设置

### Q4: 消息软删除后如何清理？
A:
```sql
-- 定期清理30天前的已删除消息
DELETE FROM message 
WHERE deleted = 1 
  AND create_time < DATE_SUB(NOW(), INTERVAL 30 DAY);
```

## 未来扩展

### 1. 消息模板
- 预定义消息模板
- 支持变量替换
- 模板管理功能

### 2. 消息订阅
- 用户订阅偏好设置
- 按类型开关通知
- 推送渠道选择

### 3. 消息统计
- 消息发送统计
- 消息阅读率分析
- 用户活跃度分析

### 4. 富文本消息
- 支持HTML格式
- 图片/附件支持
- 消息模板美化

## 总结

站内消息系统是实验室预约系统的重要组成部分，提供了完整的消息通知功能。通过合理的设计和实现，系统具备良好的扩展性和性能，能够满足各种业务场景的需求。

### 核心特性
- ✅ 多种消息类型支持
- ✅ 消息优先级管理
- ✅ 已读/未读状态跟踪
- ✅ 软删除机制
- ✅ 分页查询支持
- ✅ 批量操作支持
- ✅ 权限控制完善
- ✅ 性能优化设计

### 技术亮点
- MyBatis-Plus高效数据访问
- 索引优化提升查询性能
- 软删除保护数据安全
- RESTful API设计规范
- 完善的权限控制
