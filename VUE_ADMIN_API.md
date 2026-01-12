# Vue管理端接口文档

## 目录
- [1. 概述](#1-概述)
- [2. 用户管理模块](#2-用户管理模块)
- [3. 实验室管理模块](#3-实验室管理模块)
- [4. 预约管理模块](#4-预约管理模块)
- [5. 时间段管理模块](#5-时间段管理模块)
- [6. 消息管理模块](#6-消息管理模块)
- [7. 报表导出模块](#7-报表导出模块)
- [8. 文件管理模块](#8-文件管理模块)
- [9. 通用说明](#9-通用说明)

---

## 1. 概述

### 1.1 基础信息
- **接口基础URL**: `http://localhost:8080/api`
- **数据格式**: JSON
- **字符编码**: UTF-8
- **跨域支持**: 已配置CORS，支持跨域访问
- **目标用户**: 管理员(userType=1)

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

## 2. 用户管理模块

### 2.1 管理员登录
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
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJ1c2VyVHlwZSI6MSwic3ViIjoiYWRtaW4iLCJpYXQiOjE3MDQ5NjAwMDAsImV4cCI6MTcwNTA0NjQwMH0.xxxxx",
    "userId": 1,
    "username": "admin",
    "userType": 1,
    "realName": "管理员"
  }
}
```
- **说明**: 
  - `token`: JWT认证令牌，有效期24小时，需存储到localStorage中，后续请求需在请求头中携带
  - `userId`: 用户ID
  - `username`: 用户名
  - `userType`: 用户类型（1-管理员）
  - `realName`: 真实姓名

### 2.2 查询所有用户
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

### 2.3 根据用户类型查询
- **接口**: `GET /user/type/{userType}`
- **功能**: 按用户类型筛选用户
- **入参**: `userType` (路径参数) - 0:普通用户, 1:管理员
- **出参**: 同2.2

### 2.4 搜索用户
- **接口**: `GET /user/search`
- **功能**: 按关键词和类型搜索用户
- **入参**:
  - `keyword` (可选) - 搜索关键词（用户名、姓名、学号）
  - `userType` (可选) - 用户类型
- **出参**: 同2.2

### 2.5 查询用户详情
- **接口**: `GET /user/{id}`
- **功能**: 获取指定用户的详细信息
- **入参**: `id` (路径参数) - 用户ID
- **出参**: 单个用户对象（同2.2中的数组元素）

### 2.6 更新用户信息
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

### 2.7 重置用户密码（管理员功能）
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

### 2.8 删除用户
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

## 3. 实验室管理模块

### 3.1 添加实验室
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

### 3.2 查询所有实验室
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

### 3.3 查询实验室详情
- **接口**: `GET /laboratory/{id}`
- **功能**: 获取指定实验室详细信息
- **入参**: `id` (路径参数) - 实验室ID
- **出参**: 单个实验室对象

### 3.4 根据类型查询实验室
- **接口**: `GET /laboratory/type/{labType}`
- **功能**: 按类型筛选实验室
- **入参**: `labType` (路径参数) - 实验室类型
- **出参**: 实验室列表

### 3.5 根据状态查询实验室
- **接口**: `GET /laboratory/status/{status}`
- **功能**: 按状态筛选实验室
- **入参**: `status` (路径参数) - 0:维护中, 1:可用, 2:停用
- **出参**: 实验室列表

### 3.6 搜索实验室
- **接口**: `GET /laboratory/search`
- **功能**: 按关键词、类型、状态搜索实验室
- **入参**:
  - `keyword` (可选) - 搜索关键词（名称、编号、位置）
  - `labType` (可选) - 实验室类型
  - `status` (可选) - 实验室状态
- **出参**: 实验室列表

### 3.7 根据容量范围查询
- **接口**: `GET /laboratory/capacity`
- **功能**: 按容量范围筛选实验室
- **入参**:
  - `minCapacity` (可选) - 最小容量
  - `maxCapacity` (可选) - 最大容量
- **出参**: 实验室列表

### 3.8 更新实验室信息
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

### 3.9 更新实验室状态
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

### 3.10 删除实验室
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

### 3.11 获取实验室统计信息
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

## 4. 预约管理模块

### 4.1 查询所有预约
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

### 4.2 查询预约详情
- **接口**: `GET /reservation/{id}`
- **功能**: 获取指定预约详细信息
- **入参**: `id` (路径参数) - 预约ID
- **出参**: 单个预约对象

### 4.3 根据状态查询预约
- **接口**: `GET /reservation/status/{status}`
- **功能**: 按状态筛选预约
- **入参**: `status` (路径参数) - 0:待审核, 1:已通过, 2:已拒绝, 3:已取消, 4:已完成
- **出参**: 预约列表

### 4.4 查询待审核预约
- **接口**: `GET /reservation/pending`
- **功能**: 获取所有待审核的预约
- **入参**: 无
- **出参**: 预约列表

### 4.5 根据实验室查询预约
- **接口**: `GET /reservation/lab/{labId}`
- **功能**: 查询指定实验室的所有预约
- **入参**: `labId` (路径参数) - 实验室ID
- **出参**: 预约列表

### 4.6 根据日期范围查询预约
- **接口**: `GET /reservation/date-range`
- **功能**: 按日期范围筛选预约
- **入参**:
  - `startDate` (Query参数) - 开始日期，格式：yyyy-MM-dd
  - `endDate` (Query参数) - 结束日期，格式：yyyy-MM-dd
- **出参**: 预约列表

### 4.7 获取实验室预约时间表
- **接口**: `GET /reservation/lab-schedule`
- **功能**: 查询实验室在指定日期的预约情况
- **入参**:
  - `labId` (Query参数) - 实验室ID
  - `date` (Query参数) - 日期，格式：yyyy-MM-dd
- **出参**: 预约列表

### 4.8 审核预约（通过）
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

### 4.9 审核预约（拒绝）
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

### 4.10 更新预约信息
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

### 4.11 删除预约
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

### 4.12 获取预约统计信息
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

## 5. 时间段管理模块

### 5.1 添加时间段
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

### 5.2 查询所有时间段
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

### 5.3 查询时间段详情
- **接口**: `GET /timeslot/{id}`
- **功能**: 获取指定时间段详细信息
- **入参**: `id` (路径参数) - 时间段ID
- **出参**: 单个时间段对象

### 5.4 查询启用的时间段
- **接口**: `GET /timeslot/enabled`
- **功能**: 获取所有启用的时间段
- **入参**: 无
- **出参**: 时间段列表

### 5.5 根据状态查询时间段
- **接口**: `GET /timeslot/status/{status}`
- **功能**: 按状态筛选时间段
- **入参**: `status` (路径参数) - 0:禁用, 1:启用
- **出参**: 时间段列表

### 5.6 更新时间段信息
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

### 5.7 更新时间段状态
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

### 5.8 批量更新时间段排序
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

### 5.9 删除时间段
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

### 5.10 获取时间段统计信息
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

## 6. 消息管理模块

### 6.1 发送系统消息
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

### 6.2 查询用户的所有消息
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

### 6.3 根据类型获取用户消息
- **接口**: `GET /messages/list/{userId}/type/{messageType}`
- **功能**: 按消息类型筛选用户消息
- **入参**:
  - `userId` (路径参数) - 用户ID
  - `messageType` (路径参数) - 消息类型：system(系统消息), user(用户消息)
- **出参**: 消息列表

### 6.4 获取用户发送的消息列表
- **接口**: `GET /messages/sent/{userId}`
- **功能**: 获取用户发送的所有消息
- **入参**: `userId` (路径参数) - 用户ID
- **出参**: 消息列表

---

## 7. 报表导出模块

### 7.1 导出预约报表
- **接口**: `GET /report/export-reservations`
- **功能**: 导出预约数据为Excel文件
- **入参**:
  - `startDate` (Query参数，可选) - 开始日期，格式：yyyy-MM-dd
  - `endDate` (Query参数，可选) - 结束日期，格式：yyyy-MM-dd
  - `laboratoryId` (Query参数，可选) - 实验室ID
  - `status` (Query参数，可选) - 预约状态
- **出参**: Excel文件流
- **说明**: 直接下载Excel文件，包含预约详细信息

### 7.2 导出统计报表
- **接口**: `GET /report/export-statistics`
- **功能**: 导出预约统计数据为Excel文件
- **入参**:
  - `startDate` (Query参数，可选) - 开始日期，格式：yyyy-MM-dd
  - `endDate` (Query参数，可选) - 结束日期，格式：yyyy-MM-dd
- **出参**: Excel文件流
- **说明**: 直接下载Excel文件，包含预约统计信息

---

## 8. 文件管理模块

### 8.1 上传单个文件
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

### 8.2 批量上传文件
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

### 8.3 删除文件
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

## 9. 通用说明

### 9.1 身份认证
- 系统使用JWT(JSON Web Token)进行身份认证
- 登录成功后，服务端返回token
- 后续请求需在请求头中携带token：`Authorization: Bearer {token}`
- token有效期默认为24小时，过期需重新登录

### 9.2 管理员权限
管理员(userType=1)具有以下权限：
- **用户管理**: 查看、编辑、删除所有用户，重置用户密码
- **实验室管理**: 完整的增删改查权限，管理实验室状态
- **预约审核**: 审核通过、拒绝预约申请，查看所有预约记录
- **时间段管理**: 完整的增删改查权限，管理时间段状态
- **消息管理**: 发送系统消息给用户
- **报表导出**: 导出各类统计报表

### 9.3 错误码说明
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

### 9.4 日期时间格式
- **日期格式**: `yyyy-MM-dd`，例如：2026-01-15
- **时间格式**: `HH:mm`，例如：08:00
- **日期时间格式**: `yyyy-MM-dd HH:mm:ss`，例如：2026-01-12 10:00:00
- **时间段格式**: `HH:mm-HH:mm`，例如：08:00-10:00

### 9.5 文件上传限制
- **单文件大小限制**: 10MB
- **批量上传数量限制**: 最多10个文件
- **支持的图片格式**: .jpg, .jpeg, .png, .gif, .bmp, .webp
- **支持的文档格式**: .pdf, .doc, .docx, .xls, .xlsx, .txt
- **文件存储路径**: 
  - 头像：`uploads/avatar/yyyy/MM/dd/`
  - 实验室图片：`uploads/lab/yyyy/MM/dd/`
  - 文档：`uploads/document/yyyy/MM/dd/`

### 9.6 数据分页
- 默认每页20条数据
- 最大每页100条数据
- 分页参数：`page`(页码，从1开始)，`pageSize`(每页数量)

### 9.7 接口调用频率限制
- 一般接口：100次/分钟
- 文件上传接口：10次/分钟
- 邮件发送接口：1次/分钟

### 9.8 开发环境配置
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

### 9.9 Vue前端对接建议

#### axios配置示例
```javascript
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

### 9.10 常见问题FAQ

**Q1: 登录后token如何存储？**
A: 登录成功后，将返回的token存储在localStorage中，后续请求需在请求头中携带。

**Q2: 图片上传后如何显示？**
A: 图片上传成功后会返回url字段，该url即为图片的访问地址，可直接在img标签中使用。

**Q3: 如何处理token过期？**
A: 当接口返回401状态码时，说明token已过期，需要清除本地token并跳转到登录页面重新登录。

**Q4: 如何导出Excel报表？**
A: 直接访问导出接口URL，浏览器会自动下载Excel文件。可以使用window.open()或创建隐藏的iframe来触发下载。

**Q5: 批量操作如何处理？**
A: 批量操作（如批量更新排序）需要将所有数据以数组形式提交，后端会逐一处理并返回整体结果。

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

### D. 接口测试工具推荐
- **Postman**: 适用于API接口测试
- **Swagger**: 在线API文档（访问：http://localhost:8080/swagger-ui.html）
- **ApiPost**: 国产API接口测试工具

---

**文档最后更新时间**: 2026-01-12  
**文档版本**: v1.0.0  
**目标用户**: Vue管理端开发者  
**维护者**: 开发团队
