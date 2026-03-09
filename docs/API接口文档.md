# 实验室预约系统 - 完整 API 接口文档

**版本**: v2.0  
**更新日期**: 2026-03-09  
**基础 URL**: `http://localhost:8080/api`

---

## 📋 目录

1. [权限级别说明](#权限级别说明)
2. [用户界面接口](#用户界面接口) - 学生/教师普通用户使用
3. [管理员专用接口](#管理员专用接口) - 管理员和超级管理员使用
4. [公开接口](#公开接口) - 无需登录即可访问
5. [通用响应格式](#通用响应格式)

---

## 🔐 权限级别说明

| 级别 | 角色 | 标识 | 说明 |
|------|------|------|------|
| 0 | 学生 | USER | 普通用户，基础权限 |
| 1 | 教师 | TEACHER | 教学人员，较高权限 |
| 2 | 管理员 | ADMIN | 管理实验室和预约审核 |
| 3 | 超级管理员 | SUPER_ADMIN | 系统最高权限 |

**接口分类说明**：
- **用户界面接口**：前端用户中心、预约页面调用的接口
- **管理员专用接口**：管理后台使用的接口，需要相应权限
- **公开接口**：登录注册、公开查询等无需认证的接口

---

## 👥 用户界面接口

### 1. 用户管理 (`/api/user`)

| 方法 | 路径 | 功能 | 权限 | 参数说明 |
|------|------|------|------|----------|
| POST | `/login` | 用户登录 | 公开 | `username`, `password` |
| POST | `/register` | 用户注册 | 公开 | `User` 对象 |
| GET | `/` | 查询当前用户信息 | 需登录 | - |
| PUT | `/password` | 修改密码 | 需登录 | `id`, `oldPassword`, `newPassword` |
| POST | `/send-code` | 发送验证码邮件 | 公开 | `email`, `purpose` |
| POST | `/verify-code` | 验证验证码 | 公开 | `email`, `code` |
| POST | `/reset-password-by-email` | 邮箱重置密码 | 公开 | `email`, `code`, `newPassword` |
| POST | `/bind-email` | 绑定邮箱 | 需登录 | `userId`, `email`, `code` |
| GET | `/statistics` | 用户预约统计 | 需登录 | `userId`(可选) |

### 2. 实验室查询 (`/api/laboratory`)

| 方法 | 路径 | 功能 | 权限 | 参数说明 |
|------|------|------|------|----------|
| GET | `/{id}` | 查询实验室详情 | 公开 | `id`: 实验室 ID |
| GET | `/list` | 查询所有实验室 | 公开 | - |
| GET | `/type/{labType}` | 按类型查询 | 公开 | `labType` |
| GET | `/status/{status}` | 按状态查询 | 公开 | `status` |
| GET | `/available` | 查询可用实验室 | 公开 | - |
| GET | `/search` | 搜索实验室 | 公开 | `keyword`, `status` |
| GET | `/capacity` | 按容量查询 | 公开 | `minCapacity`, `maxCapacity` |
| GET | `/statistics` | 实验室统计 | 公开 | - |

### 3. 预约管理 (`/api/reservation`)

| 方法 | 路径 | 功能 | 权限 | 参数说明 |
|------|------|------|------|----------|
| POST | `/` | 创建预约 | 需登录 | `Reservation` 对象 |
| GET | `/my` | 我的预约列表 | 需登录 | `page`, `pageSize`, `status` |
| GET | `/{id}` | 预约详情 | 公开 | `id` |
| GET | `/check-conflict` | 检查时间冲突 | 公开 | `labId`, `reserveDate`, `timeSlot` |
| PUT | `/cancel/{id}` | 取消预约 | 需登录 | `id` |
| PUT | `/complete/{id}` | 完成预约 | 需登录 | `id`, `feedback` |
| GET | `/date-range` | 日期范围查询 | 公开 | `startDate`, `endDate` |
| GET | `/lab-schedule` | 实验室日程 | 公开 | `labId`, `date` |

### 4. 消息中心 (`/api/messages`)

| 方法 | 路径 | 功能 | 权限 | 参数说明 |
|------|------|------|------|----------|
| GET | `/list` | 我的消息列表 | 需登录 | - |
| GET | `/unread` | 未读消息 | 需登录 | `page`, `pageSize` |
| GET | `/unread-count` | 未读消息数量 | 需登录 | - |
| GET | `/detail/{messageId}` | 消息详情 | 需登录 | `messageId` |
| PUT | `/mark-read/{messageId}` | 标记已读 | 需登录 | `messageId` |
| PUT | `/batch-mark-read` | 批量标记已读 | 需登录 | `messageIds` |
| PUT | `/mark-all-read` | 全部标记已读 | 需登录 | - |
| DELETE | `/{messageId}` | 删除消息 | 需登录 | `messageId` |
| POST | `/user` | 发送用户消息 | 需登录 | `receiverId`, `title`, `content` |

### 5. 时间段查询 (`/api/timeslot`)

| 方法 | 路径 | 功能 | 权限 | 参数说明 |
|------|------|------|------|----------|
| GET | `/list` | 所有时间段 | 公开 | - |
| GET | `/available` | 可用时间段 | 公开 | `labId`, `date` |
| GET | `/enabled` | 启用的时间段 | 公开 | - |

### 6. 信誉分系统 (`/api/credit`)

| 方法 | 路径 | 功能 | 权限 | 参数说明 |
|------|------|------|------|----------|
| GET | `/my` | 我的信誉分 | 需登录 | - |
| GET | `/my/logs` | 信誉分记录 | 需登录 | `page`, `pageSize` |
| POST | `/training/recover` | 培训恢复分数 | 需登录 | - |
| GET | `/rules` | 信誉分规则 | 公开 | - |

### 7. 数据大屏 (`/api/dashboard`)

**所有接口均为公开访问，用于数据展示**

| 方法 | 路径 | 功能 | 说明 |
|------|------|------|------|
| GET | `/core-metrics` | 核心指标 | 总用户数、实验室数等 6 个指标 |
| GET | `/reservation-trend` | 预约趋势 | 最近 30 天折线图 |
| GET | `/lab-utilization` | 实验室利用率 | TOP10 柱状图 |
| GET | `/time-slot-heatmap` | 时间段热度 | 热力图数据 |
| GET | `/user-type-distribution` | 用户类型分布 | 饼图数据 |
| GET | `/status-distribution` | 预约状态分布 | 饼图数据 |
| GET | `/credit-level-distribution` | 信用等级分布 | 柱状图 |
| GET | `/weekday-distribution` | 周几分布 | 柱状图 |
| GET | `/college-rank` | 学院排行 | TOP10 柱状图 |
| GET | `/recent-activities` | 实时动态 | 最新 10 条记录 |
| GET | `/capacity-usage` | 容量使用率 | 各实验室使用率 |
| GET | `/all` | 完整数据 | 一次性获取所有数据 |

### 8. 文件上传 (`/api/file`)

| 方法 | 路径 | 功能 | 权限 | 参数说明 |
|------|------|------|------|----------|
| POST | `/upload` | 上传单个文件 | 公开 | `file`, `type` |

---

## 👨‍💼 管理员专用接口

### 1. 用户管理 (`/api/user`)

| 方法 | 路径 | 功能 | 权限 | 参数说明 |
|------|------|------|------|----------|
| GET | `/list` | 查询所有用户 | 管理员 | - |
| GET | `/type/{userType}` | 按类型查询用户 | 管理员 | `userType` |
| PUT | `/{id}` | 更新用户信息 | 自己或超管 | `id`, `User` 对象 |
| PUT | `/status` | 更新用户状态 | **超管 (3)** | `userId`, `status` |
| PUT | `/reset-password` | 重置密码 | **超管 (3)** | `id`, `newPassword` |
| DELETE | `/{id}` | 删除用户 | **超管 (3)** | `id` |
| GET | `/search` | 搜索用户 | 管理员 | `keyword`, `userType` |

### 2. 实验室管理 (`/api/laboratory`)

| 方法 | 路径 | 功能 | 权限 | 参数说明 |
|------|------|------|------|----------|
| POST | `/` | 添加实验室 | **管理员 (2+)** | `Laboratory` 对象 |
| PUT | `/` | 更新实验室信息 | **管理员 (2+)** | `Laboratory` 对象 |
| PUT | `/status` | 更新实验室状态 | **管理员 (2+)** | `id`, `status` |
| DELETE | `/{id}` | 删除实验室 | **超管 (3)** | `id` |

### 3. 预约审核 (`/api/reservation`)

| 方法 | 路径 | 功能 | 权限 | 参数说明 |
|------|------|------|------|----------|
| GET | `/pending` | 待审核预约 | 管理员 | - |
| PUT | `/approve/{id}` | 审核通过 | **管理员 (2+)** | `id`, `approvalNote` |
| PUT | `/reject/{id}` | 审核拒绝 | **管理员 (2+)** | `id`, `approvalNote` |
| PUT | `/` | 更新预约信息 | **管理员 (2+)** | `Reservation` 对象 |
| DELETE | `/{id}` | 删除预约 | **超管 (3)** | `id` |
| GET | `/statistics` | 预约统计 | 管理员 | `userId` |

### 4. 系统消息 (`/api/messages`)

| 方法 | 路径 | 功能 | 权限 | 参数说明 |
|------|------|------|------|----------|
| POST | `/system` | 广播系统消息 | **管理员 (2+)** | `title`, `content`, `priority` |

### 5. 时间段管理 (`/api/timeslot`)

| 方法 | 路径 | 功能 | 权限 | 参数说明 |
|------|------|------|------|----------|
| POST | `/` | 添加时间段 | **管理员 (2+)** | `TimeSlot` 对象 |
| PUT | `/` | 更新时间段 | **管理员 (2+)** | `TimeSlot` 对象 |
| PUT | `/{id}` | 更新时间段 | **管理员 (2+)** | `id`, `TimeSlot` |
| PUT | `/status` | 更新状态 | **管理员 (2+)** | `id`, `status` |
| DELETE | `/{id}` | 删除时间段 | **超管 (3)** | `id` |
| PUT | `/batch-sort` | 批量更新排序 | **管理员 (2+)** | `TimeSlot` 数组 |

### 6. 信誉分管理 (`/api/credit`)

| 方法 | 路径 | 功能 | 权限 | 参数说明 |
|------|------|------|------|----------|
| GET | `/user/{userId}` | 查询用户信誉分 | **管理员 (2+)** | `userId` |
| GET | `/user/{userId}/logs` | 信誉分记录 | **管理员 (2+)** | `userId`, `page`, `pageSize` |
| POST | `/user/{userId}/adjust` | 调整信誉分 | **超管 (3)** | `userId`, `score`, `description` |

### 7. 文件管理 (`/api/file`)

| 方法 | 路径 | 功能 | 权限 | 参数说明 |
|------|------|------|------|----------|
| POST | `/upload-batch` | 批量上传 | **管理员 (2+)** | `files`(最多 10 个), `type` |
| DELETE | `/delete` | 删除文件 | **超管 (3)** | `path` |

### 8. 报表导出 (`/api/report`)

| 方法 | 路径 | 功能 | 权限 | 参数说明 |
|------|------|------|------|----------|
| GET | `/export-reservations` | 导出预约报表 | **管理员 (2+)** | `startDate`, `endDate`, `laboratoryId`, `status` |
| GET | `/export-statistics` | 导出统计报表 | **管理员 (2+)** | `startDate`, `endDate` |

---

## 🌐 公开接口

### 1. 认证相关

| 方法 | 路径 | 功能 | 说明 |
|------|------|------|------|
| POST | `/api/user/login` | 用户登录 | 返回 JWT Token |
| POST | `/api/user/register` | 用户注册 | 需要邮箱验证 |
| POST | `/api/wx/login` | 微信小程序登录 | 返回 openid/sessionKey |

### 2. 公开查询

**实验室查询**：
- `GET /api/laboratory/list` - 所有实验室
- `GET /api/laboratory/{id}` - 实验室详情
- `GET /api/laboratory/type/{labType}` - 按类型查询
- `GET /api/laboratory/status/{status}` - 按状态查询
- `GET /api/laboratory/available` - 可用实验室
- `GET /api/laboratory/search` - 搜索实验室
- `GET /api/laboratory/capacity` - 按容量查询

**预约查询**：
- `GET /api/reservation/{id}` - 预约详情
- `GET /api/reservation/list` - 所有预约
- `GET /api/reservation/user/{userId}` - 用户预约列表
- `GET /api/reservation/lab/{labId}` - 实验室预约列表
- `GET /api/reservation/status/{status}` - 按状态查询
- `GET /api/reservation/pending` - 待审核预约
- `GET /api/reservation/check-conflict` - 检查冲突
- `GET /api/reservation/date-range` - 日期范围查询
- `GET /api/reservation/lab-schedule` - 实验室日程

**时间段查询**：
- `GET /api/timeslot/list` - 所有时间段
- `GET /api/timeslot/available` - 可用时间段
- `GET /api/timeslot/enabled` - 启用的时间段
- `GET /api/timeslot/status/{status}` - 按状态查询

**其他公开接口**：
- `GET /api/user/type/{userType}` - 按类型查询用户
- `GET /api/user/search` - 搜索用户
- `GET /api/timeslot/statistics` - 时间段统计
- `GET /api/laboratory/statistics` - 实验室统计

### 3. 数据大屏（全部公开）

所有 `/api/dashboard/*` 接口均无需登录，用于公开展示屏。

### 4. 文件上传

- `POST /api/file/upload` - 上传单个文件（头像、实验图片等）

---

## 📦 通用响应格式

### 成功响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    // 具体数据
  }
}
```

### 失败响应

```json
{
  "code": 500,
  "message": "错误信息描述",
  "data": null
}
```

### 分页响应

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "list": [],      // 数据列表
    "total": 100,    // 总记录数
    "page": 1,       // 当前页
    "pageSize": 10,  // 每页大小
    "totalPages": 10 // 总页数
  }
}
```

---

## 🔑 认证方式

需要登录的接口需在请求头携带 JWT Token：

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 获取 Token

```http
POST /api/user/login
Content-Type: application/json

{
  "username": "admin",
  "password": "123456"
}
```

**响应**：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "admin",
    "userType": 3,
    "realName": "张三"
  }
}
```

---

## 📊 接口统计

### 按 Controller 分类

| Controller | 接口总数 | 用户界面 | 管理员专用 | 公开接口 |
|------------|----------|----------|------------|----------|
| UserController | 20 | 8 | 6 | 6 |
| LaboratoryController | 12 | 8 | 3 | 8 |
| ReservationController | 19 | 10 | 4 | 11 |
| MessageController | 18 | 14 | 1 | 1 |
| TimeSlotController | 12 | 4 | 6 | 6 |
| CreditController | 7 | 3 | 2 | 2 |
| DashboardController | 12 | 12 | 0 | 12 |
| FileController | 3 | 1 | 2 | 1 |
| ReportController | 2 | 0 | 2 | 0 |
| WxAuthController | 2 | 2 | 0 | 2 |
| **总计** | **107** | **62** | **26** | **49** |

### 按权限分类

| 权限要求 | 接口数量 | 占比 |
|----------|----------|------|
| 无需登录 | ~49 | 46% |
| 需登录（普通用户） | ~32 | 30% |
| 管理员及以上 | 17 | 16% |
| 超级管理员 | 9 | 8% |

---

## 🎯 快速查找索引

### 用户常用接口

- 登录：`POST /api/user/login`
- 我的预约：`GET /api/reservation/my`
- 创建预约：`POST /api/reservation`
- 取消预约：`PUT /api/reservation/cancel/{id}`
- 我的消息：`GET /api/messages/list`
- 我的信誉分：`GET /api/credit/my`

### 管理员常用接口

- 待审核预约：`GET /api/reservation/pending`
- 审核通过：`PUT /api/reservation/approve/{id}`
- 审核拒绝：`PUT /api/reservation/reject/{id}`
- 广播消息：`POST /api/messages/system`
- 导出报表：`GET /api/report/export-reservations`
- 调整信誉分：`POST /api/credit/user/{userId}/adjust`

### 数据大屏接口

- 核心指标：`GET /api/dashboard/core-metrics`
- 预约趋势：`GET /api/dashboard/reservation-trend`
- 实验室利用率：`GET /api/dashboard/lab-utilization`
- 实时动态：`GET /api/dashboard/recent-activities`
- 完整数据：`GET /api/dashboard/all`

---

## ⚠️ 注意事项

1. **权限验证**：管理员接口会验证 JWT Token 中的 `userType` 字段
2. **数据隔离**：普通用户只能访问自己的数据，不能越权访问
3. **频率限制**：建议对短信/邮件发送接口做限流处理
4. **文件上传**：限制文件大小和类型，防止恶意上传
5. **敏感操作**：删除、重置密码等操作会记录日志

---

**文档维护**：每次新增或修改接口后，请及时更新此文档。
