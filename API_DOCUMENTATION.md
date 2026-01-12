# 实验室预约系统接口文档

## 目录
- [1. 概述](#1-概述)
- [2. Vue管理端接口](#2-vue管理端接口)
- [3. 微信小程序用户端接口](#3-微信小程序用户端接口)
- [4. 通用说明](#4-通用说明)

---

## 1. 概述

### 1.1 基础信息
- **接口基础URL**: `http://localhost:8080/api`
- **数据格式**: JSON
- **字符编码**: UTF-8
- **跨域支持**: 已配置CORS，支持跨域访问

### 1.2 通用返回格式
```json
{
  "code": 200,           // 状态码：200-成功，其他-失败
  "message": "success",  // 返回消息
  "data": {}            // 返回数据
}
```

### 1.3 状态码说明
- **用户类型**: 0-普通用户, 1-管理员
- **实验室状态**: 0-维护中, 1-可用, 2-停用
- **预约状态**: 0-待审核, 1-已通过, 2-已拒绝, 3-已取消, 4-已完成
- **消息状态**: 0-未读, 1-已读
- **消息优先级**: 0-普通, 1-重要, 2-紧急
- **时间段状态**: 0-禁用, 1-启用

---

## 2. Vue管理端接口

### 2.1 用户管理模块

#### 2.1.1 管理员登录
- **接口**: `POST /user/login`
- **功能**: 管理员账号登录
- **入参**:
```json
{
  "username": "admin",
  "password": "123456"
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "管理员",
    "userType": 1,
    "phone": "13800138000",
    "email": "admin@example.com",
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

#### 2.1.2 查询所有用户
- **接口**: `GET /user/list`
- **功能**: 获取系统所有用户列表
- **入参**: 无
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "username": "zhangsan",
      "realName": "张三",
      "userType": 0,
      "phone": "13800138001",
      "email": "zhangsan@example.com",
      "studentNo": "2021001",
      "department": "计算机学院",
      "emailVerified": 1,
      "createTime": "2026-01-10 10:00:00"
    }
  ]
}
```

#### 2.1.3 根据用户类型查询
- **接口**: `GET /user/type/{userType}`
- **功能**: 按用户类型筛选用户
- **入参**: `userType` (路径参数) - 0:普通用户, 1:管理员
- **出参**: 同2.1.2

#### 2.1.4 搜索用户
- **接口**: `GET /user/search`
- **功能**: 按关键词和类型搜索用户
- **入参**:
  - `keyword` (可选) - 搜索关键词（用户名、姓名、学号）
  - `userType` (可选) - 用户类型
- **出参**: 同2.1.2

#### 2.1.5 查询用户详情
- **接口**: `GET /user/{id}`
- **功能**: 获取指定用户的详细信息
- **入参**: `id` (路径参数) - 用户ID
- **出参**: 单个用户对象（同2.1.2中的数组元素）

#### 2.1.6 更新用户信息
- **接口**: `PUT /user`
- **功能**: 更新用户基本信息
- **入参**:
```json
{
  "id": 1,
  "realName": "张三",
  "phone": "13800138001",
  "email": "zhangsan@example.com",
  "department": "计算机学院"
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

#### 2.1.7 重置用户密码（管理员功能）
- **接口**: `PUT /user/reset-password`
- **功能**: 管理员重置用户密码
- **入参**:
```json
{
  "id": 1,
  "newPassword": "123456"
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "密码重置成功",
  "data": null
}
```

#### 2.1.8 删除用户
- **接口**: `DELETE /user/{id}`
- **功能**: 删除指定用户
- **入参**: `id` (路径参数) - 用户ID
- **出参**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 2.2 实验室管理模块

#### 2.2.1 添加实验室
- **接口**: `POST /laboratory`
- **功能**: 创建新的实验室
- **入参**:
```json
{
  "labName": "计算机实验室A",
  "labNo": "LAB-001",
  "labType": "计算机类",
  "location": "A栋3楼301",
  "capacity": 50,
  "equipment": "计算机50台，投影仪1台",
  "description": "用于计算机编程实验",
  "status": 1,
  "imageUrl": "http://localhost:8080/uploads/lab/2026/01/11/xxx.jpg"
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "实验室添加成功",
  "data": {
    "id": 1,
    "labName": "计算机实验室A",
    ...
  }
}
```

#### 2.2.2 查询所有实验室
- **接口**: `GET /laboratory/list`
- **功能**: 获取所有实验室列表
- **入参**: 无
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "labName": "计算机实验室A",
      "labNo": "LAB-001",
      "labType": "计算机类",
      "location": "A栋3楼301",
      "capacity": 50,
      "equipment": "计算机50台，投影仪1台",
      "description": "用于计算机编程实验",
      "status": 1,
      "imageUrl": "http://localhost:8080/uploads/lab/xxx.jpg",
      "createTime": "2026-01-10 10:00:00",
      "updateTime": "2026-01-10 10:00:00"
    }
  ]
}
```

#### 2.2.3 查询实验室详情
- **接口**: `GET /laboratory/{id}`
- **功能**: 获取指定实验室详细信息
- **入参**: `id` (路径参数) - 实验室ID
- **出参**: 单个实验室对象

#### 2.2.4 根据类型查询实验室
- **接口**: `GET /laboratory/type/{labType}`
- **功能**: 按类型筛选实验室
- **入参**: `labType` (路径参数) - 实验室类型
- **出参**: 实验室列表

#### 2.2.5 根据状态查询实验室
- **接口**: `GET /laboratory/status/{status}`
- **功能**: 按状态筛选实验室
- **入参**: `status` (路径参数) - 0:维护中, 1:可用, 2:停用
- **出参**: 实验室列表

#### 2.2.6 搜索实验室
- **接口**: `GET /laboratory/search`
- **功能**: 按关键词、类型、状态搜索实验室
- **入参**:
  - `keyword` (可选) - 搜索关键词（名称、编号、位置）
  - `labType` (可选) - 实验室类型
  - `status` (可选) - 实验室状态
- **出参**: 实验室列表

#### 2.2.7 根据容量范围查询
- **接口**: `GET /laboratory/capacity`
- **功能**: 按容量范围筛选实验室
- **入参**:
  - `minCapacity` (可选) - 最小容量
  - `maxCapacity` (可选) - 最大容量
- **出参**: 实验室列表

#### 2.2.8 更新实验室信息
- **接口**: `PUT /laboratory`
- **功能**: 更新实验室信息
- **入参**:
```json
{
  "id": 1,
  "labName": "计算机实验室A",
  "location": "A栋3楼301",
  "capacity": 60,
  "equipment": "计算机60台，投影仪2台",
  "description": "用于计算机编程实验"
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "实验室信息更新成功",
  "data": null
}
```

#### 2.2.9 更新实验室状态
- **接口**: `PUT /laboratory/status`
- **功能**: 更新实验室状态
- **入参**:
  - `id` (Query参数) - 实验室ID
  - `status` (Query参数) - 新状态：0-维护中, 1-可用, 2-停用
- **出参**:
```json
{
  "code": 200,
  "message": "实验室状态更新成功",
  "data": null
}
```

#### 2.2.10 删除实验室
- **接口**: `DELETE /laboratory/{id}`
- **功能**: 删除指定实验室
- **入参**: `id` (路径参数) - 实验室ID
- **出参**:
```json
{
  "code": 200,
  "message": "实验室删除成功",
  "data": null
}
```

#### 2.2.11 获取实验室统计信息
- **接口**: `GET /laboratory/statistics`
- **功能**: 获取实验室统计数据
- **入参**: 无
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCount": 10,
    "availableCount": 8,
    "maintenanceCount": 1,
    "disabledCount": 1,
    "totalCapacity": 500
  }
}
```

---

### 2.3 预约管理模块

#### 2.3.1 查询所有预约
- **接口**: `GET /reservation/list`
- **功能**: 获取所有预约记录
- **入参**: 无
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "userId": 2,
      "userName": "张三",
      "labId": 1,
      "labName": "计算机实验室A",
      "reserveDate": "2026-01-15",
      "timeSlot": "08:00-10:00",
      "peopleNum": 30,
      "experimentName": "Java编程实验",
      "purpose": "课程实验",
      "equipment": "计算机30台",
      "status": 0,
      "approver": null,
      "approveComment": null,
      "createTime": "2026-01-12 10:00:00",
      "approveTime": null
    }
  ]
}
```

#### 2.3.2 查询预约详情
- **接口**: `GET /reservation/{id}`
- **功能**: 获取指定预约详细信息
- **入参**: `id` (路径参数) - 预约ID
- **出参**: 单个预约对象

#### 2.3.3 根据状态查询预约
- **接口**: `GET /reservation/status/{status}`
- **功能**: 按状态筛选预约
- **入参**: `status` (路径参数) - 0:待审核, 1:已通过, 2:已拒绝, 3:已取消, 4:已完成
- **出参**: 预约列表

#### 2.3.4 查询待审核预约
- **接口**: `GET /reservation/pending`
- **功能**: 获取所有待审核的预约
- **入参**: 无
- **出参**: 预约列表

#### 2.3.5 根据实验室查询预约
- **接口**: `GET /reservation/lab/{labId}`
- **功能**: 查询指定实验室的所有预约
- **入参**: `labId` (路径参数) - 实验室ID
- **出参**: 预约列表

#### 2.3.6 根据日期范围查询预约
- **接口**: `GET /reservation/date-range`
- **功能**: 按日期范围筛选预约
- **入参**:
  - `startDate` (Query参数) - 开始日期，格式：yyyy-MM-dd
  - `endDate` (Query参数) - 结束日期，格式：yyyy-MM-dd
- **出参**: 预约列表

#### 2.3.7 获取实验室预约时间表
- **接口**: `GET /reservation/lab-schedule`
- **功能**: 查询实验室在指定日期的预约情况
- **入参**:
  - `labId` (Query参数) - 实验室ID
  - `date` (Query参数) - 日期，格式：yyyy-MM-dd
- **出参**: 预约列表

#### 2.3.8 审核预约（通过）
- **接口**: `PUT /reservation/approve/{id}`
- **功能**: 审核通过预约申请
- **入参**:
  - `id` (路径参数) - 预约ID
  - `approvalNote` (Query参数，可选) - 审核意见
- **出参**:
```json
{
  "code": 200,
  "message": "预约审核通过",
  "data": null
}
```

#### 2.3.9 审核预约（拒绝）
- **接口**: `PUT /reservation/reject/{id}`
- **功能**: 拒绝预约申请
- **入参**:
  - `id` (路径参数) - 预约ID
  - `approvalNote` (Query参数，可选) - 拒绝原因
- **出参**:
```json
{
  "code": 200,
  "message": "预约已拒绝",
  "data": null
}
```

#### 2.3.10 更新预约信息
- **接口**: `PUT /reservation`
- **功能**: 更新预约信息
- **入参**:
```json
{
  "id": 1,
  "reserveDate": "2026-01-15",
  "timeSlot": "10:00-12:00",
  "peopleNum": 35,
  "experimentName": "Java编程实验",
  "purpose": "课程实验",
  "equipment": "计算机35台"
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "预约信息更新成功",
  "data": null
}
```

#### 2.3.11 删除预约
- **接口**: `DELETE /reservation/{id}`
- **功能**: 删除预约记录
- **入参**: `id` (路径参数) - 预约ID
- **出参**:
```json
{
  "code": 200,
  "message": "预约删除成功",
  "data": null
}
```

#### 2.3.12 获取预约统计信息
- **接口**: `GET /reservation/statistics`
- **功能**: 获取预约统计数据
- **入参**:
  - `userId` (Query参数，可选) - 用户ID，不传则统计全部
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCount": 100,
    "pendingCount": 10,
    "approvedCount": 60,
    "rejectedCount": 5,
    "cancelledCount": 15,
    "completedCount": 10
  }
}
```

---

### 2.4 时间段管理模块

#### 2.4.1 添加时间段
- **接口**: `POST /timeslot`
- **功能**: 创建新的预约时间段
- **入参**:
```json
{
  "slotName": "上午第一节",
  "startTime": "08:00",
  "endTime": "10:00",
  "status": 1,
  "sortOrder": 1
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "时间段添加成功",
  "data": {
    "id": 1,
    "slotName": "上午第一节",
    "startTime": "08:00",
    "endTime": "10:00",
    "status": 1,
    "sortOrder": 1
  }
}
```

#### 2.4.2 查询所有时间段
- **接口**: `GET /timeslot/list`
- **功能**: 获取所有时间段列表
- **入参**: 无
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "slotName": "上午第一节",
      "startTime": "08:00",
      "endTime": "10:00",
      "status": 1,
      "sortOrder": 1,
      "createTime": "2026-01-10 10:00:00"
    }
  ]
}
```

#### 2.4.3 查询时间段详情
- **接口**: `GET /timeslot/{id}`
- **功能**: 获取指定时间段详细信息
- **入参**: `id` (路径参数) - 时间段ID
- **出参**: 单个时间段对象

#### 2.4.4 查询启用的时间段
- **接口**: `GET /timeslot/enabled`
- **功能**: 获取所有启用的时间段
- **入参**: 无
- **出参**: 时间段列表

#### 2.4.5 根据状态查询时间段
- **接口**: `GET /timeslot/status/{status}`
- **功能**: 按状态筛选时间段
- **入参**: `status` (路径参数) - 0:禁用, 1:启用
- **出参**: 时间段列表

#### 2.4.6 更新时间段信息
- **接口**: `PUT /timeslot`
- **功能**: 更新时间段信息
- **入参**:
```json
{
  "id": 1,
  "slotName": "上午第一节",
  "startTime": "08:00",
  "endTime": "10:00",
  "status": 1,
  "sortOrder": 1
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "时间段信息更新成功",
  "data": null
}
```

#### 2.4.7 更新时间段状态
- **接口**: `PUT /timeslot/status`
- **功能**: 更新时间段状态
- **入参**:
  - `id` (Query参数) - 时间段ID
  - `status` (Query参数) - 新状态：0-禁用, 1-启用
- **出参**:
```json
{
  "code": 200,
  "message": "时间段状态更新成功",
  "data": null
}
```

#### 2.4.8 批量更新时间段排序
- **接口**: `PUT /timeslot/batch-sort`
- **功能**: 批量更新时间段的排序顺序
- **入参**:
```json
[
  {
    "id": 1,
    "sortOrder": 1
  },
  {
    "id": 2,
    "sortOrder": 2
  }
]
```
- **出参**:
```json
{
  "code": 200,
  "message": "时间段排序更新成功",
  "data": null
}
```

#### 2.4.9 删除时间段
- **接口**: `DELETE /timeslot/{id}`
- **功能**: 删除指定时间段
- **入参**: `id` (路径参数) - 时间段ID
- **出参**:
```json
{
  "code": 200,
  "message": "时间段删除成功",
  "data": null
}
```

#### 2.4.10 获取时间段统计信息
- **接口**: `GET /timeslot/statistics`
- **功能**: 获取时间段统计数据
- **入参**: 无
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCount": 8,
    "enabledCount": 7,
    "disabledCount": 1
  }
}
```

---

### 2.5 消息管理模块

#### 2.5.1 发送系统消息
- **接口**: `POST /messages/system`
- **功能**: 管理员发送系统消息给用户
- **入参**:
```json
{
  "receiverId": 2,
  "title": "系统通知",
  "content": "您的预约已审核通过",
  "priority": 1
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "系统消息发送成功",
  "data": {
    "id": 1,
    "senderId": null,
    "receiverId": 2,
    "messageType": "system",
    "title": "系统通知",
    "content": "您的预约已审核通过",
    "priority": 1,
    "isRead": 0,
    "createTime": "2026-01-12 10:00:00"
  }
}
```

#### 2.5.2 查询用户的所有消息
- **接口**: `GET /messages/list/{userId}`
- **功能**: 获取指定用户的所有消息
- **入参**: `userId` (路径参数) - 用户ID
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "senderId": null,
      "senderName": "系统",
      "receiverId": 2,
      "receiverName": "张三",
      "messageType": "system",
      "title": "系统通知",
      "content": "您的预约已审核通过",
      "priority": 1,
      "isRead": 0,
      "readTime": null,
      "createTime": "2026-01-12 10:00:00"
    }
  ]
}
```

#### 2.5.3 根据类型获取用户消息
- **接口**: `GET /messages/list/{userId}/type/{messageType}`
- **功能**: 按消息类型筛选用户消息
- **入参**:
  - `userId` (路径参数) - 用户ID
  - `messageType` (路径参数) - 消息类型：system(系统消息), user(用户消息)
- **出参**: 消息列表

#### 2.5.4 获取用户发送的消息列表
- **接口**: `GET /messages/sent/{userId}`
- **功能**: 获取用户发送的所有消息
- **入参**: `userId` (路径参数) - 用户ID
- **出参**: 消息列表

---

### 2.6 报表导出模块

#### 2.6.1 导出预约报表
- **接口**: `GET /report/export-reservations`
- **功能**: 导出预约数据为Excel文件
- **入参**:
  - `startDate` (Query参数，可选) - 开始日期，格式：yyyy-MM-dd
  - `endDate` (Query参数，可选) - 结束日期，格式：yyyy-MM-dd
  - `laboratoryId` (Query参数，可选) - 实验室ID
  - `status` (Query参数，可选) - 预约状态
- **出参**: Excel文件流
- **说明**: 直接下载Excel文件，包含预约详细信息

#### 2.6.2 导出统计报表
- **接口**: `GET /report/export-statistics`
- **功能**: 导出预约统计数据为Excel文件
- **入参**:
  - `startDate` (Query参数，可选) - 开始日期，格式：yyyy-MM-dd
  - `endDate` (Query参数，可选) - 结束日期，格式：yyyy-MM-dd
- **出参**: Excel文件流
- **说明**: 直接下载Excel文件，包含预约统计信息

---

### 2.7 文件管理模块

#### 2.7.1 上传单个文件
- **接口**: `POST /file/upload`
- **功能**: 上传图片或文档文件
- **入参**:
  - `file` (表单参数) - 文件对象
  - `type` (表单参数，可选) - 文件类型：avatar(头像), lab(实验室图片), document(文档)，默认document
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "url": "http://localhost:8080/uploads/lab/2026/01/11/xxx.jpg",
    "path": "/uploads/lab/2026/01/11/xxx.jpg",
    "fileName": "uuid-xxx.jpg",
    "originalName": "实验室图片.jpg",
    "size": "102400"
  }
}
```

#### 2.7.2 批量上传文件
- **接口**: `POST /file/upload-batch`
- **功能**: 批量上传多个文件（最多10个）
- **入参**:
  - `files` (表单参数) - 文件数组
  - `type` (表单参数，可选) - 文件类型
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 3,
    "success": 3,
    "fail": 0
  }
}
```

#### 2.7.3 删除文件
- **接口**: `DELETE /file/delete`
- **功能**: 删除指定文件
- **入参**:
  - `path` (Query参数) - 文件路径
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 3. 微信小程序用户端接口

### 3.1 用户模块

#### 3.1.1 用户登录
- **接口**: `POST /user/login`
- **功能**: 用户账号登录
- **入参**:
```json
{
  "username": "zhangsan",
  "password": "123456"
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 2,
    "username": "zhangsan",
    "realName": "张三",
    "userType": 0,
    "phone": "13800138001",
    "email": "zhangsan@example.com",
    "studentNo": "2021001",
    "department": "计算机学院",
    "emailVerified": 1,
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

#### 3.1.2 用户注册
- **接口**: `POST /user/register`
- **功能**: 新用户注册
- **入参**:
```json
{
  "username": "zhangsan",
  "password": "123456",
  "realName": "张三",
  "phone": "13800138001",
  "email": "zhangsan@example.com",
  "studentNo": "2021001",
  "department": "计算机学院",
  "userType": 0
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 2,
    "username": "zhangsan",
    "realName": "张三",
    ...
  }
}
```

#### 3.1.3 获取用户信息
- **接口**: `GET /user/{id}`
- **功能**: 获取当前用户详细信息
- **入参**: `id` (路径参数) - 用户ID
- **出参**: 用户对象

#### 3.1.4 更新个人信息
- **接口**: `PUT /user`
- **功能**: 更新用户个人信息
- **入参**:
```json
{
  "id": 2,
  "realName": "张三",
  "phone": "13800138002",
  "email": "zhangsan@example.com",
  "avatar": "http://localhost:8080/uploads/avatar/xxx.jpg"
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "更新成功",
  "data": null
}
```

#### 3.1.5 修改密码
- **接口**: `PUT /user/password`
- **功能**: 用户修改自己的密码
- **入参**:
```json
{
  "id": 2,
  "oldPassword": "123456",
  "newPassword": "654321"
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

#### 3.1.6 发送注册验证邮件
- **接口**: `POST /user/send-register-email`
- **功能**: 发送邮箱注册验证链接
- **入参**:
```json
{
  "email": "zhangsan@example.com",
  "username": "zhangsan"
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "验证邮件已发送，请查收",
  "data": null
}
```

#### 3.1.7 验证邮箱
- **接口**: `GET /user/verify-email`
- **功能**: 验证邮箱注册token
- **入参**:
  - `token` (Query参数) - 验证令牌
- **出参**:
```json
{
  "code": 200,
  "message": "邮箱验证成功",
  "data": null
}
```

#### 3.1.8 发送验证码邮件
- **接口**: `POST /user/send-code`
- **功能**: 发送邮箱验证码
- **入参**:
```json
{
  "email": "zhangsan@example.com",
  "purpose": "register"
}
```
- **说明**: `purpose`可选值：register(注册), reset-password(重置密码), bind-email(绑定邮箱)
- **出参**:
```json
{
  "code": 200,
  "message": "验证码已发送，请查收邮件",
  "data": null
}
```

#### 3.1.9 验证验证码
- **接口**: `POST /user/verify-code`
- **功能**: 验证邮箱验证码
- **入参**:
```json
{
  "email": "zhangsan@example.com",
  "code": "123456"
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "验证成功",
  "data": null
}
```

#### 3.1.10 通过邮箱验证码重置密码
- **接口**: `POST /user/reset-password-by-email`
- **功能**: 通过邮箱验证码重置密码
- **入参**:
```json
{
  "email": "zhangsan@example.com",
  "code": "123456",
  "newPassword": "654321"
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "密码重置成功",
  "data": null
}
```

#### 3.1.11 绑定邮箱
- **接口**: `POST /user/bind-email`
- **功能**: 绑定或更换邮箱
- **入参**:
```json
{
  "userId": 2,
  "email": "newemail@example.com",
  "code": "123456"
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "邮箱绑定成功",
  "data": null
}
```

#### 3.1.12 重新发送邮箱验证邮件
- **接口**: `POST /user/resend-verify-email`
- **功能**: 重新发送邮箱验证邮件
- **入参**:
```json
{
  "userId": 2
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "验证邮件已重新发送",
  "data": null
}
```

---

### 3.2 实验室浏览模块

#### 3.2.1 查询所有实验室
- **接口**: `GET /laboratory/list`
- **功能**: 浏览所有实验室列表
- **入参**: 无
- **出参**: 实验室列表（同管理端2.2.2）

#### 3.2.2 查询实验室详情
- **接口**: `GET /laboratory/{id}`
- **功能**: 查看实验室详细信息
- **入参**: `id` (路径参数) - 实验室ID
- **出参**: 单个实验室对象

#### 3.2.3 查询可用实验室
- **接口**: `GET /laboratory/available`
- **功能**: 获取所有可用的实验室
- **入参**: 无
- **出参**: 实验室列表

#### 3.2.4 根据类型查询实验室
- **接口**: `GET /laboratory/type/{labType}`
- **功能**: 按类型筛选实验室
- **入参**: `labType` (路径参数) - 实验室类型
- **出参**: 实验室列表

#### 3.2.5 搜索实验室
- **接口**: `GET /laboratory/search`
- **功能**: 按关键词搜索实验室
- **入参**:
  - `keyword` (Query参数，可选) - 搜索关键词
  - `labType` (Query参数，可选) - 实验室类型
  - `status` (Query参数，可选) - 实验室状态
- **出参**: 实验室列表

#### 3.2.6 根据容量范围查询
- **接口**: `GET /laboratory/capacity`
- **功能**: 按容量范围筛选实验室
- **入参**:
  - `minCapacity` (Query参数，可选) - 最小容量
  - `maxCapacity` (Query参数，可选) - 最大容量
- **出参**: 实验室列表

---

### 3.3 预约管理模块

#### 3.3.1 创建预约
- **接口**: `POST /reservation`
- **功能**: 用户提交实验室预约申请
- **入参**:
```json
{
  "userId": 2,
  "labId": 1,
  "reserveDate": "2026-01-15",
  "timeSlot": "08:00-10:00",
  "peopleNum": 30,
  "experimentName": "Java编程实验",
  "purpose": "课程实验",
  "equipment": "计算机30台"
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "预约创建成功",
  "data": {
    "id": 1,
    "userId": 2,
    "labId": 1,
    "reserveDate": "2026-01-15",
    "timeSlot": "08:00-10:00",
    "status": 0,
    "createTime": "2026-01-12 10:00:00"
  }
}
```

#### 3.3.2 查询我的预约
- **接口**: `GET /reservation/user/{userId}`
- **功能**: 获取用户的所有预约记录
- **入参**: `userId` (路径参数) - 用户ID
- **出参**: 预约列表

#### 3.3.3 查询预约详情
- **接口**: `GET /reservation/{id}`
- **功能**: 查看预约详细信息
- **入参**: `id` (路径参数) - 预约ID
- **出参**: 单个预约对象

#### 3.3.4 取消预约
- **接口**: `PUT /reservation/cancel/{id}`
- **功能**: 用户取消自己的预约
- **入参**: `id` (路径参数) - 预约ID
- **出参**:
```json
{
  "code": 200,
  "message": "预约已取消",
  "data": null
}
```

#### 3.3.5 完成预约
- **接口**: `PUT /reservation/complete/{id}`
- **功能**: 标记预约为已完成（可选填反馈）
- **入参**:
  - `id` (路径参数) - 预约ID
  - `feedback` (Query参数，可选) - 使用反馈
- **出参**:
```json
{
  "code": 200,
  "message": "预约已完成",
  "data": null
}
```

#### 3.3.6 检查时间冲突
- **接口**: `GET /reservation/check-conflict`
- **功能**: 检查指定时间段是否已被预约
- **入参**:
  - `labId` (Query参数) - 实验室ID
  - `reserveDate` (Query参数) - 预约日期
  - `timeSlot` (Query参数) - 时间段
- **出参**:
```json
{
  "code": 200,
  "message": "该时间段可用",
  "data": null
}
```

#### 3.3.7 获取实验室预约时间表
- **接口**: `GET /reservation/lab-schedule`
- **功能**: 查看实验室在指定日期的预约情况
- **入参**:
  - `labId` (Query参数) - 实验室ID
  - `date` (Query参数) - 日期，格式：yyyy-MM-dd
- **出参**: 预约列表

#### 3.3.8 获取我的预约统计
- **接口**: `GET /reservation/statistics`
- **功能**: 获取用户的预约统计数据
- **入参**:
  - `userId` (Query参数) - 用户ID
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCount": 20,
    "pendingCount": 2,
    "approvedCount": 12,
    "rejectedCount": 1,
    "cancelledCount": 3,
    "completedCount": 2
  }
}
```

---

### 3.4 时间段模块

#### 3.4.1 查询启用的时间段
- **接口**: `GET /timeslot/enabled`
- **功能**: 获取所有可预约的时间段
- **入参**: 无
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "slotName": "上午第一节",
      "startTime": "08:00",
      "endTime": "10:00",
      "status": 1,
      "sortOrder": 1
    }
  ]
}
```

#### 3.4.2 查询所有时间段
- **接口**: `GET /timeslot/list`
- **功能**: 获取所有时间段列表
- **入参**: 无
- **出参**: 时间段列表

---

### 3.5 消息中心模块

#### 3.5.1 获取我的消息
- **接口**: `GET /messages/list/{userId}`
- **功能**: 获取用户的所有消息
- **入参**: `userId` (路径参数) - 用户ID
- **出参**: 消息列表

#### 3.5.2 获取未读消息
- **接口**: `GET /messages/unread/{userId}`
- **功能**: 获取用户的未读消息
- **入参**: `userId` (路径参数) - 用户ID
- **出参**: 消息列表

#### 3.5.3 获取未读消息数量
- **接口**: `GET /messages/unread-count/{userId}`
- **功能**: 获取未读消息总数
- **入参**: `userId` (路径参数) - 用户ID
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": 5
}
```

#### 3.5.4 获取各类型未读消息数量
- **接口**: `GET /messages/unread-count-by-types/{userId}`
- **功能**: 获取各消息类型的未读数量
- **入参**: `userId` (路径参数) - 用户ID
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "system": 3,
    "user": 2
  }
}
```

#### 3.5.5 获取消息详情
- **接口**: `GET /messages/detail/{messageId}`
- **功能**: 查看消息详情（自动标记为已读）
- **入参**:
  - `messageId` (路径参数) - 消息ID
  - `userId` (Query参数) - 用户ID
- **出参**: 单个消息对象

#### 3.5.6 标记消息为已读
- **接口**: `PUT /messages/mark-read/{messageId}`
- **功能**: 将指定消息标记为已读
- **入参**:
  - `messageId` (路径参数) - 消息ID
  - `userId` (Query参数) - 用户ID
- **出参**:
```json
{
  "code": 200,
  "message": "已标记为已读",
  "data": null
}
```

#### 3.5.7 批量标记为已读
- **接口**: `PUT /messages/batch-mark-read`
- **功能**: 批量标记多条消息为已读
- **入参**:
```json
{
  "messageIds": [1, 2, 3],
  "userId": 2
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "已批量标记为已读",
  "data": null
}
```

#### 3.5.8 标记所有消息为已读
- **接口**: `PUT /messages/mark-all-read/{userId}`
- **功能**: 将用户所有未读消息标记为已读
- **入参**: `userId` (路径参数) - 用户ID
- **出参**:
```json
{
  "code": 200,
  "message": "已标记所有消息为已读",
  "data": null
}
```

#### 3.5.9 删除消息
- **接口**: `DELETE /messages/{messageId}`
- **功能**: 删除指定消息
- **入参**:
  - `messageId` (路径参数) - 消息ID
  - `userId` (Query参数) - 用户ID
- **出参**:
```json
{
  "code": 200,
  "message": "消息删除成功",
  "data": null
}
```

#### 3.5.10 批量删除消息
- **接口**: `DELETE /messages/batch`
- **功能**: 批量删除多条消息
- **入参**:
```json
{
  "messageIds": [1, 2, 3],
  "userId": 2
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "消息批量删除成功",
  "data": null
}
```

#### 3.5.11 根据类型获取消息
- **接口**: `GET /messages/list/{userId}/type/{messageType}`
- **功能**: 按消息类型筛选用户消息
- **入参**:
  - `userId` (路径参数) - 用户ID
  - `messageType` (路径参数) - 消息类型：system(系统消息), user(用户消息)
- **出参**: 消息列表

#### 3.5.12 分页查询消息
- **接口**: `GET /messages/page/{userId}`
- **功能**: 分页获取用户消息
- **入参**:
  - `userId` (路径参数) - 用户ID
  - `page` (Query参数，可选) - 页码，默认1
  - `pageSize` (Query参数，可选) - 每页数量，默认20
- **出参**: 消息列表

#### 3.5.13 获取高优先级未读消息
- **接口**: `GET /messages/high-priority-unread/{userId}`
- **功能**: 获取用户的高优先级未读消息
- **入参**: `userId` (路径参数) - 用户ID
- **出参**: 消息列表

#### 3.5.14 发送用户消息
- **接口**: `POST /messages/user`
- **功能**: 用户之间发送消息
- **入参**:
```json
{
  "senderId": 2,
  "receiverId": 3,
  "title": "消息标题",
  "content": "消息内容"
}
```
- **出参**:
```json
{
  "code": 200,
  "message": "消息发送成功",
  "data": {
    "id": 1,
    "senderId": 2,
    "receiverId": 3,
    "messageType": "user",
    "title": "消息标题",
    "content": "消息内容",
    "priority": 0,
    "isRead": 0,
    "createTime": "2026-01-12 10:00:00"
  }
}
```

---

### 3.6 文件上传模块

#### 3.6.1 上传头像
- **接口**: `POST /file/upload`
- **功能**: 上传用户头像
- **入参**:
  - `file` (表单参数) - 图片文件
  - `type` (表单参数) - 固定值：avatar
- **出参**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "url": "http://localhost:8080/uploads/avatar/2026/01/11/xxx.jpg",
    "path": "/uploads/avatar/2026/01/11/xxx.jpg",
    "fileName": "uuid-xxx.jpg",
    "originalName": "头像.jpg",
    "size": "102400"
  }
}
```

#### 3.6.2 上传文档
- **接口**: `POST /file/upload`
- **功能**: 上传文档文件
- **入参**:
  - `file` (表单参数) - 文件对象
  - `type` (表单参数) - 固定值：document
- **出参**: 同3.6.1

---

## 4. 通用说明

### 4.1 身份认证
- 系统使用JWT(JSON Web Token)进行身份认证
- 登录成功后，服务端返回token
- 后续请求需在请求头中携带token：`Authorization: Bearer {token}`
- token有效期默认为24小时，过期需重新登录

### 4.2 权限控制
- **管理员权限(userType=1)**:
  - 用户管理（查看、编辑、删除所有用户）
  - 实验室管理（完整的增删改查权限）
  - 预约审核（审核通过、拒绝预约）
  - 时间段管理（完整的增删改查权限）
  - 消息管理（发送系统消息）
  - 报表导出（导出各类统计报表）

- **普通用户权限(userType=0)**:
  - 个人信息管理（查看、编辑自己的信息）
  - 实验室浏览（查看实验室信息）
  - 预约管理（创建、查看、取消自己的预约）
  - 消息中心（查看、管理自己的消息）
  - 文件上传（上传头像、文档）

### 4.3 错误码说明
```json
{
  "200": "操作成功",
  "400": "请求参数错误",
  "401": "未授权，需要登录",
  "403": "权限不足",
  "404": "资源不存在",
  "500": "服务器内部错误"
}
```

### 4.4 日期时间格式
- **日期格式**: `yyyy-MM-dd`，例如：2026-01-15
- **时间格式**: `HH:mm`，例如：08:00
- **日期时间格式**: `yyyy-MM-dd HH:mm:ss`，例如：2026-01-12 10:00:00
- **时间段格式**: `HH:mm-HH:mm`，例如：08:00-10:00

### 4.5 文件上传限制
- **单文件大小限制**: 10MB
- **批量上传数量限制**: 最多10个文件
- **支持的图片格式**: .jpg, .jpeg, .png, .gif, .bmp, .webp
- **支持的文档格式**: .pdf, .doc, .docx, .xls, .xlsx, .txt
- **文件存储路径**: 
  - 头像：`uploads/avatar/yyyy/MM/dd/`
  - 实验室图片：`uploads/lab/yyyy/MM/dd/`
  - 文档：`uploads/document/yyyy/MM/dd/`

### 4.6 邮件验证说明
- **验证码有效期**: 5分钟
- **验证链接有效期**: 24小时
- **验证码长度**: 6位数字
- **邮件发送限制**: 同一邮箱1分钟内最多发送1次

### 4.7 预约规则
- 预约必须提前至少1天
- 同一用户同一时间段只能预约一个实验室
- 同一实验室同一时间段只能被预约一次
- 预约状态流转：待审核 → 已通过/已拒绝
- 用户可以取消待审核或已通过的预约
- 已使用的预约可标记为已完成并填写反馈

### 4.8 消息推送机制
系统会自动发送消息通知用户以下事件：
- 预约提交成功
- 预约审核通过
- 预约审核拒绝
- 预约即将开始（提前1天）
- 预约取消通知

### 4.9 数据分页
- 默认每页20条数据
- 最大每页100条数据
- 分页参数：`page`(页码，从1开始)，`pageSize`(每页数量)

### 4.10 接口调用频率限制
- 一般接口：100次/分钟
- 文件上传接口：10次/分钟
- 邮件发送接口：1次/分钟

### 4.11 开发环境配置
```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/lab_reservation
spring.datasource.username=root
spring.datasource.password=your_password

# 文件上传配置
file.upload-dir=./uploads
file.access-url=http://localhost:8080

# 邮件配置
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=your_email@example.com
spring.mail.password=your_email_password

# JWT配置
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000
```

### 4.12 接口测试工具推荐
- **Postman**: 适用于API接口测试
- **Swagger**: 在线API文档（访问：http://localhost:8080/swagger-ui.html）
- **ApiPost**: 国产API接口测试工具

### 4.13 前端对接建议

#### Vue管理端
```javascript
// axios配置示例
import axios from 'axios'

const service = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      // 错误处理
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res.data
  },
  error => {
    return Promise.reject(error)
  }
)

export default service
```

#### 微信小程序用户端
```javascript
// request.js 封装示例
const BASE_URL = 'http://localhost:8080/api'

function request(url, method = 'GET', data = {}) {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    
    wx.request({
      url: BASE_URL + url,
      method: method,
      data: data,
      header: {
        'content-type': 'application/json',
        'Authorization': token ? 'Bearer ' + token : ''
      },
      success(res) {
        if (res.data.code === 200) {
          resolve(res.data.data)
        } else {
          wx.showToast({
            title: res.data.message,
            icon: 'none'
          })
          reject(res.data.message)
        }
      },
      fail(err) {
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

module.exports = {
  get: (url, data) => request(url, 'GET', data),
  post: (url, data) => request(url, 'POST', data),
  put: (url, data) => request(url, 'PUT', data),
  delete: (url, data) => request(url, 'DELETE', data)
}
```

### 4.14 常见问题FAQ

**Q1: 登录后token如何存储？**
A: 登录成功后，将返回的token存储在localStorage（Web端）或wx.setStorageSync（小程序端）中，后续请求需在请求头中携带。

**Q2: 图片上传后如何显示？**
A: 图片上传成功后会返回url字段，该url即为图片的访问地址，可直接在img标签中使用。

**Q3: 如何处理token过期？**
A: 当接口返回401状态码时，说明token已过期，需要清除本地token并跳转到登录页面重新登录。

**Q4: 预约冲突如何处理？**
A: 在提交预约前，先调用`/reservation/check-conflict`接口检查时间段是否可用，避免冲突。

**Q5: 如何实现实时消息提醒？**
A: 可以定期轮询`/messages/unread-count/{userId}`接口获取未读消息数量，或使用WebSocket实现实时推送（需后端支持）。

---

## 附录

### A. 数据库表结构参考
详见项目中的SQL文件：`src/main/resources/mysql/lab_reservation_*.sql`

### B. 项目源码地址
GitHub: https://github.com/shiwu2002/shiyanshi.git

### C. 技术栈
- **后端**: Spring Boot 3.x, MyBatis-Plus, MySQL
- **认证**: JWT (JSON Web Token)
- **邮件**: Spring Mail
- **文档导出**: Apache POI
- **文件上传**: MultipartFile

### D. 版本历史
- v1.0.0 (2026-01-12): 初始版本，包含基础功能
  - 用户管理
  - 实验室管理
  - 预约管理
  - 时间段管理
  - 消息系统
  - 文件上传
  - 报表导出
  - 邮件验证

---

**文档最后更新时间**: 2026-01-12  
**文档版本**: v1.0.0  
**维护者**: 开发团队
