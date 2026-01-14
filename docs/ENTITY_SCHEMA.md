# 实体模型对接文档（前后端对接版）

用途：为前端（含 Vue 与小程序）与后端对接提供统一字段定义、取值范围、含义说明与示例，降低联调歧义。字段类型以 Java 类型为准，前端请按 JSON 进行序列化/反序列化。

全局说明：
- 日期格式建议：yyyy-MM-dd（如：2026-01-15）
- 时间格式建议：HH:mm（如：08:00）
- 时间戳（LocalDateTime）序列化建议：yyyy-MM-dd HH:mm:ss
- 枚举/状态字段请严格遵守取值范围
- 标注“非持久化字段”表示不直接对应数据库列，多用于关联展示

目录：
- User 用户
- Laboratory 实验室
- TimeSlot 时间段
- Reservation 预约
- Message 站内消息
- UserWechatAuth 第三方绑定（新增）

---

## User（用户）

数据表：user

权限/状态枚举：
- userType：1-学生 2-教师 3-管理员
- status：0-禁用 1-正常
- emailVerified：0-未验证 1-已验证

字段定义：
| 字段名 | 类型 | 说明 | 取值/示例 | 备注 |
|---|---|---|---|---|
| id | Long | 主键ID | 1 | 自增 |
| username | String | 用户名/学号/工号 | 2020123456 | 唯一性约束建议 |
| password | String | 密码 | — | 后端存储加密摘要 |
| realName | String | 真实姓名 | 张三 |  |
| phone | String | 手机号 | 13800000000 |  |
| email | String | 邮箱 | a@b.com |  |
| userType | Integer | 用户类型 | 1/2/3 | 见上方枚举 |
| college | String | 学院 | 计算机学院 |  |
| major | String | 专业 | 软件工程 |  |
| studentId | String | 学号 | 2020123456 | 学生用户 |
| teacherId | String | 工号 | T00123 | 教师用户 |
| status | Integer | 状态 | 0/1 | 0-禁用 1-正常 |
| emailVerified | Integer | 邮箱验证 | 0/1 | 0-未验证 1-已验证 |
| avatar | String | 头像URL | http://... | 与文件上传返回的url一致 |
| createTime | LocalDateTime | 创建时间 | 2026-01-14 12:00:00 | 自动填充 |
| updateTime | LocalDateTime | 更新时间 | 2026-01-14 12:00:00 | 自动填充 |

JSON 示例：
```json
{
  "id": 1,
  "username": "2020123456",
  "realName": "张三",
  "phone": "13800000000",
  "email": "zhangsan@univ.edu",
  "userType": 1,
  "college": "计算机学院",
  "major": "软件工程",
  "studentId": "2020123456",
  "status": 1,
  "emailVerified": 1,
  "avatar": "http://localhost:8080/uploads/avatar/2026/01/11/xxx.jpg",
  "createTime": "2026-01-14 12:00:00",
  "updateTime": "2026-01-14 12:00:00"
}
```

---

## Laboratory（实验室）

数据表：laboratory

状态枚举：
- status：0-停用 1-正常 2-维护中

字段定义：
| 字段名 | 类型 | 说明 | 取值/示例 | 备注 |
|---|---|---|---|---|
| id | Long | 主键ID | 2 | 自增 |
| labName | String | 实验室名称 | 计算机实验室A101 |  |
| labNumber | String | 实验室编号 | A101 |  |
| location | String | 位置 | 西校区A栋 |  |
| building | String | 楼栋 | A栋 |  |
| floor | String | 楼层 | 1F |  |
| capacity | Integer | 容纳人数 | 60 |  |
| equipment | String | 设备清单 | JSON字符串 | 如 '["示波器","电源"]' |
| description | String | 实验室描述 | ... |  |
| imageUrl | String | 图片URL | http://... | 与数据库 image_url 对应 |
| labType | String | 实验室类型 | 计算机/物理/化学 | 非持久化字段 |
| status | Integer | 状态 | 0/1/2 | 0-停用 1-正常 2-维护中 |
| manager | String | 负责人 | 李四 | 非持久化字段 |
| managerPhone | String | 负责人电话 | 13900000000 | 非持久化字段 |
| images | String | 实验室图片 | 多图URL | 非持久化，单图用 imageUrl |
| openTime | String | 开放时间说明 | 工作日8:00-18:00 | 非持久化 |
| createTime | LocalDateTime | 创建时间 | 2026-01-14 12:00:00 | 自动填充 |
| updateTime | LocalDateTime | 更新时间 | 2026-01-14 12:00:00 | 自动填充 |

JSON 示例：
```json
{
  "id": 2,
  "labName": "计算机实验室A101",
  "labNumber": "A101",
  "location": "西校区A栋",
  "building": "A栋",
  "floor": "1F",
  "capacity": 60,
  "equipment": "[\"示波器\",\"电源\"]",
  "description": "配备多媒体与上机位",
  "imageUrl": "http://localhost:8080/uploads/lab/2026/01/11/xxx.jpg",
  "labType": "计算机",
  "status": 1,
  "manager": "李四",
  "managerPhone": "13900000000",
  "images": "http://...;http://...",
  "openTime": "工作日8:00-18:00",
  "createTime": "2026-01-14 12:00:00",
  "updateTime": "2026-01-14 12:00:00"
}
```

---

## TimeSlot（时间段）

数据表：time_slot

状态枚举：
- status：0-停用 1-启用

字段定义：
| 字段名 | 类型 | 说明 | 取值/示例 | 备注 |
|---|---|---|---|---|
| id | Long | 主键ID | 3 | 自增 |
| slotName | String | 时间段名称 | 上午第一节 |  |
| startTime | String | 开始时间 | 08:00 | 24小时制 |
| endTime | String | 结束时间 | 10:00 | 24小时制 |
| sortOrder | Integer | 排序顺序 | 1 | 越小越靠前 |
| status | Integer | 状态 | 0/1 | 0-停用 1-启用 |
| description | String | 描述 | — |  |
| createTime | LocalDateTime | 创建时间 | 2026-01-14 12:00:00 | 自动填充 |
| updateTime | LocalDateTime | 更新时间 | 2026-01-14 12:00:00 | 自动填充 |

JSON 示例：
```json
{
  "id": 3,
  "slotName": "上午第一节",
  "startTime": "08:00",
  "endTime": "10:00",
  "sortOrder": 1,
  "status": 1,
  "description": "上午第一教学时段",
  "createTime": "2026-01-14 12:00:00",
  "updateTime": "2026-01-14 12:00:00"
}
```

---

## Reservation（预约）

数据表：reservation

状态枚举：
- status：0-待审核 1-已通过 2-已拒绝 3-已取消 4-已完成
评分：
- rating：1-5 分

字段定义：
| 字段名 | 类型 | 说明 | 取值/示例 | 备注 |
|---|---|---|---|---|
| id | Long | 主键ID | 10 | 自增 |
| userId | Long | 预约用户ID | 1 |  |
| userName | String | 预约用户姓名 | 张三 | 非持久化（exist=false） |
| labId | Long | 实验室ID | 2 |  |
| labName | String | 实验室名称 | 计算机实验室A101 | 非持久化（exist=false） |
| reserveDate | LocalDate | 预约日期 | 2026-01-15 | yyyy-MM-dd |
| timeSlot | String | 时间段 | 08:00-10:00 | 或者系统定义名称 |
| peopleNum | Integer | 使用人数 | 30 |  |
| purpose | String | 使用目的 | 课程实验 |  |
| experimentName | String | 实验名称 | 算法实验一 |  |
| equipment | String | 需要的设备 | 多媒体/示波器 |  |
| status | Integer | 审核状态 | 0/1/2/3/4 | 见上方枚举 |
| approver | String | 审核人 | 李四 |  |
| approveComment | String | 审核意见 | 同意/补充说明... |  |
| approveTime | LocalDateTime | 审核时间 | 2026-01-15 09:00:00 |  |
| cancelReason | String | 取消原因 | 临时调整 |  |
| rating | Integer | 评分 | 1-5 |  |
| comment | String | 使用评价 | 设备齐全 |  |
| createTime | LocalDateTime | 创建时间 | 2026-01-14 12:00:00 | 自动填充 |
| updateTime | LocalDateTime | 更新时间 | 2026-01-14 12:00:00 | 自动填充 |

JSON 示例：
```json
{
  "id": 10,
  "userId": 1,
  "userName": "张三",
  "labId": 2,
  "labName": "计算机实验室A101",
  "reserveDate": "2026-01-15",
  "timeSlot": "08:00-10:00",
  "peopleNum": 30,
  "purpose": "课程实验",
  "experimentName": "算法实验一",
  "equipment": "多媒体",
  "status": 0,
  "approver": null,
  "approveComment": null,
  "approveTime": null,
  "cancelReason": null,
  "rating": null,
  "comment": null,
  "createTime": "2026-01-14 12:00:00",
  "updateTime": "2026-01-14 12:00:00"
}
```

前端注意：
- 创建预约前建议调用“时间冲突校验”接口
- timeSlot 与 TimeSlot 实体的 slotName/startTime/endTime 需保持一致的业务含义

---

## Message（站内消息）

数据表：message

枚举：
- messageType：system-系统消息, reservation-预约通知, approval-审核通知, reminder-提醒消息, user-用户消息
- isRead：0-未读 1-已读
- priority：0-普通 1-重要 2-紧急
- deleted：0-未删除 1-已删除

字段定义：
| 字段名 | 类型 | 说明 | 取值/示例 | 备注 |
|---|---|---|---|---|
| id | Long | 主键ID | 100 | 自增 |
| senderId | Long | 发送者ID | 1 | 系统消息可为0或null |
| senderName | String | 发送者用户名 | admin |  |
| receiverId | Long | 接收者ID | 2 |  |
| receiverName | String | 接收者用户名 | zhangsan |  |
| messageType | String | 消息类型 | reservation | 见上方枚举 |
| title | String | 标题 | 预约审核结果 |  |
| content | String | 内容 | 您的预约已通过 |  |
| relatedId | Long | 相关业务ID | 10 | 如预约ID |
| relatedType | String | 相关业务类型 | reservation | reservation/laboratory等 |
| isRead | Integer | 已读状态 | 0/1 | 0-未读 1-已读 |
| priority | Integer | 优先级 | 0/1/2 | 0-普通 1-重要 2-紧急 |
| createTime | LocalDateTime | 创建时间 | 2026-01-14 12:00:00 |  |
| readTime | LocalDateTime | 阅读时间 | 2026-01-14 13:00:00 |  |
| deleted | Integer | 是否删除 | 0/1 | 0-未删除 1-已删除 |

JSON 示例：
```json
{
  "id": 100,
  "senderId": 1,
  "senderName": "admin",
  "receiverId": 2,
  "receiverName": "zhangsan",
  "messageType": "reservation",
  "title": "预约审核结果",
  "content": "您的预约已通过",
  "relatedId": 10,
  "relatedType": "reservation",
  "isRead": 0,
  "priority": 1,
  "createTime": "2026-01-14 12:00:00",
  "readTime": null,
  "deleted": 0
}
```

---

## UserWechatAuth（第三方绑定关系）（新增）

数据表：user_wechat_auth

用途：
- 解耦第三方身份（微信 openid/unionid）与业务用户（user表）
- 支持多平台/多账号绑定：微信小程序/公众号/企业微信等
- 便于登录审计与扩展

枚举：
- platform：mini_program（微信小程序）/ mp（公众号）/ enterprise_wechat（企业微信）…可扩展
- bindStatus：0-未绑定 1-已绑定
- deleted：0-未删除 1-已删除

字段定义：
| 字段名 | 类型 | 说明 | 取值/示例 | 备注 |
|---|---|---|---|---|
| id | Long | 主键ID | 500 | 自增 |
| userId | Long | 业务用户ID | 1 | 关联 user.id |
| platform | String | 平台标识 | mini_program | 可扩展 |
| openid | String | 平台下唯一ID | oXXXXXX | 必填 |
| unionid | String | 统一ID | uXXXXXX | 可能为空 |
| sessionKey | String | 最近会话密钥 | sXXXXXX | 可选 |
| bindStatus | Integer | 绑定状态 | 0/1 | 1-已绑定 |
| lastLoginTime | LocalDateTime | 最近登录时间 | 2026-01-14 12:00:00 | 微信登录成功时更新 |
| deleted | Integer | 是否删除 | 0/1 | 逻辑删除 |
| createTime | LocalDateTime | 创建时间 | 2026-01-14 12:00:00 | 自动填充 |
| updateTime | LocalDateTime | 更新时间 | 2026-01-14 12:00:00 | 自动填充 |

JSON 示例：
```json
{
  "id": 500,
  "userId": 1,
  "platform": "mini_program",
  "openid": "oXXXXXX",
  "unionid": null,
  "sessionKey": "sXXXXXX",
  "bindStatus": 1,
  "lastLoginTime": "2026-01-14 12:00:00",
  "deleted": 0,
  "createTime": "2026-01-14 12:00:00",
  "updateTime": "2026-01-14 12:00:00"
}
```

对接注意：
- 开发环境下建议仅存储必要字段（openid/unionid），sessionKey仅用于登录后短期使用。
- 登录流程：`/api/wx/login` → 若已绑定返回系统JWT；未绑定返回 openid 等信息 → 前端调用 `/api/wx/bind` 将 openid 绑定到用户后端，再次登录或直接返回 JWT。

---

## 对接建议

- 字段命名以实体为准，前端如需展示友好名称可在视图层映射。
- 后端存在的“非持久化字段”在某些查询接口中才会返回，前端需做好兼容判空。
- 与文件相关字段（avatar、imageUrl等）请配合文件上传接口取得的可访问URL使用。
- 与状态/类型相关的整型/字符串字段请统一枚举映射，避免魔法数散落前端。
- 推荐将本文件作为接口联调的单一数据源，若有字段新增/变更，应同时更新本文档与对应接口文档（docs/MINI_PROGRAM_API.md）。
