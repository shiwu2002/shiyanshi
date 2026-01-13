# MessageController API 变更说明

## 变更概述
所有需要用户ID的接口已修改为从JWT token自动获取用户ID，无需在请求中手动传递userId参数。

## 变更原理
- JWT拦截器（JWTInterceptor）会自动解析token并将userId注入到HttpServletRequest的attribute中
- 控制器方法通过`request.getAttribute("userId")`获取当前登录用户的ID
- 提高了安全性，防止用户伪造或篡改userId参数

## API变更详情

### 1. 发送用户消息
**旧接口**: `POST /api/messages/user`
```json
{
  "senderId": 1,      // ❌ 需要手动传递
  "receiverId": 2,
  "title": "消息标题",
  "content": "消息内容"
}
```

**新接口**: `POST /api/messages/user`
```json
{
  "receiverId": 2,    // ✅ senderId自动从token获取
  "title": "消息标题",
  "content": "消息内容"
}
```

### 2. 获取用户所有消息
**旧接口**: `GET /api/messages/list/{userId}`
**新接口**: `GET /api/messages/list` （userId自动从token获取）

### 3. 获取未读消息
**旧接口**: `GET /api/messages/unread/{userId}`
**新接口**: `GET /api/messages/unread` （userId自动从token获取）

### 4. 根据类型获取消息
**旧接口**: `GET /api/messages/list/{userId}/type/{messageType}`
**新接口**: `GET /api/messages/list/type/{messageType}` （userId自动从token获取）

### 5. 获取未读消息数量
**旧接口**: `GET /api/messages/unread-count/{userId}` 或 `GET /api/messages/unread-count`
**新接口**: `GET /api/messages/unread-count` （统一接口，userId自动从token获取）

### 6. 获取各类型未读消息统计
**旧接口**: `GET /api/messages/unread-count-by-types/{userId}`
**新接口**: `GET /api/messages/unread-count-by-types` （userId自动从token获取）

### 7. 获取消息详情
**旧接口**: `GET /api/messages/detail/{messageId}?userId={userId}`
**新接口**: `GET /api/messages/detail/{messageId}` （userId自动从token获取）

### 8. 标记消息为已读
**旧接口**: `PUT /api/messages/mark-read/{messageId}?userId={userId}`
**新接口**: `PUT /api/messages/mark-read/{messageId}` （userId自动从token获取）

### 9. 批量标记消息为已读
**旧接口**: `PUT /api/messages/batch-mark-read`
```json
{
  "messageIds": [1, 2, 3],
  "userId": 1           // ❌ 需要手动传递
}
```

**新接口**: `PUT /api/messages/batch-mark-read`
```json
{
  "messageIds": [1, 2, 3]  // ✅ userId自动从token获取
}
```

### 10. 标记所有消息为已读
**旧接口**: `PUT /api/messages/mark-all-read/{userId}`
**新接口**: `PUT /api/messages/mark-all-read` （userId自动从token获取）

### 11. 删除消息
**旧接口**: `DELETE /api/messages/{messageId}?userId={userId}`
**新接口**: `DELETE /api/messages/{messageId}` （userId自动从token获取）

### 12. 批量删除消息
**旧接口**: `DELETE /api/messages/batch`
```json
{
  "messageIds": [1, 2, 3],
  "userId": 1           // ❌ 需要手动传递
}
```

**新接口**: `DELETE /api/messages/batch`
```json
{
  "messageIds": [1, 2, 3]  // ✅ userId自动从token获取
}
```

### 13. 获取用户发送的消息
**旧接口**: `GET /api/messages/sent/{userId}`
**新接口**: `GET /api/messages/sent` （userId自动从token获取）

### 14. 分页查询用户消息
**旧接口**: `GET /api/messages/page/{userId}?page=1&pageSize=20`
**新接口**: `GET /api/messages/page?page=1&pageSize=20` （userId自动从token获取）

### 15. 根据优先级获取消息
**旧接口**: `GET /api/messages/priority/{userId}/{priority}`
**新接口**: `GET /api/messages/priority/{priority}` （userId自动从token获取）

### 16. 获取高优先级未读消息
**旧接口**: `GET /api/messages/high-priority-unread/{userId}`
**新接口**: `GET /api/messages/high-priority-unread` （userId自动从token获取）

## 重要变更：系统消息广播功能

### 发送系统消息（已变更为广播模式）
**旧接口**: `POST /api/messages/system`
```json
{
  "receiverId": 1,    // ❌ 只能发给单个用户
  "title": "系统通知",
  "content": "通知内容",
  "priority": 0
}
```
**返回**: 单个Message对象

**新接口**: `POST /api/messages/system`（广播给所有用户）
```json
{
  "title": "系统通知",     // ✅ 自动发送给所有用户
  "content": "通知内容",
  "priority": 0
}
```
**返回**: 
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "count": 150,
    "message": "系统消息已发送给 150 个用户"
  }
}
```

**重要说明**：
- 系统消息现在是**广播模式**，会自动发送给系统中的所有用户
- 不再需要（也不再支持）`receiverId`参数
- 每个用户都会收到一条独立的消息记录
- 返回发送成功的用户数量
- 这是管理员功能，应配合权限控制使用

## 前端调整建议

### 1. 请求头设置
确保所有请求都携带JWT token：
```javascript
// 方式1: Authorization header
headers: {
  'Authorization': `Bearer ${token}`
}

// 方式2: 作为参数（备选）
params: {
  token: token
}
```

### 2. API调用示例

**旧方式（需要传userId）：**
```javascript
// 获取消息列表
axios.get(`/api/messages/list/${userId}`)

// 标记为已读
axios.put(`/api/messages/mark-read/${messageId}?userId=${userId}`)

// 批量删除
axios.delete('/api/messages/batch', {
  data: {
    messageIds: [1, 2, 3],
    userId: userId
  }
})
```

**新方式（自动从token获取）：**
```javascript
// 获取消息列表
axios.get('/api/messages/list', {
  headers: { 'Authorization': `Bearer ${token}` }
})

// 标记为已读
axios.put(`/api/messages/mark-read/${messageId}`, null, {
  headers: { 'Authorization': `Bearer ${token}` }
})

// 批量删除
axios.delete('/api/messages/batch', {
  data: { messageIds: [1, 2, 3] },
  headers: { 'Authorization': `Bearer ${token}` }
})
```

## 优势说明

1. **安全性提升**：用户无法伪造或篡改userId，只能操作自己的数据
2. **代码简化**：前端无需在每个请求中传递userId参数
3. **一致性**：所有需要用户身份的接口都采用统一的认证方式
4. **维护性**：减少了参数传递错误的可能性

## 错误处理

如果请求未携带有效token或token已过期，将返回：
```json
{
  "code": 401,
  "message": "未授权，请先登录",
  "data": null
}
```

如果token有效但无法获取userId，将返回：
```json
{
  "code": 500,
  "message": "缺少用户身份信息，请登录后重试",
  "data": null
}
```

## 迁移检查清单

- [ ] 更新前端所有消息相关API调用
- [ ] 移除请求中的userId参数
- [ ] 确保所有请求携带有效的JWT token
- [ ] 测试所有消息功能是否正常
- [ ] 更新API文档
- [ ] 通知相关开发人员API变更

## 测试建议

1. 测试未登录状态（无token）的访问控制
2. 测试token过期后的处理
3. 测试所有消息操作功能
4. 验证用户只能操作自己的消息
5. 测试边界情况（如空消息列表、无效messageId等）
