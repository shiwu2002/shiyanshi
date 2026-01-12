# 实验室预约系统后端

基于 Spring Boot + 小程序的实验室预约管理系统后端实现。

## 项目简介

本系统是一个完整的实验室预约管理系统后端，提供用户管理、实验室管理、预约管理和时间段配置等核心功能。系统采用前后端分离架构，后端提供 RESTful API 接口，可与小程序前端或其他前端应用集成。

## 技术栈

- **框架**: Spring Boot 3.4.1
- **持久层**: MyBatis 3.0.4
- **数据库**: MySQL 8.0+
- **认证**: JWT (JSON Web Token)
- **加密**: MD5
- **JSON处理**: FastJSON 2.0.43
- **工具**: Lombok

## 功能特性

### 1. 用户管理
- 用户注册/登录
- 用户信息查询和更新
- 用户密码修改
- 管理员用户管理（CRUD）
- 用户状态管理

### 2. 实验室管理
- 实验室信息的增删改查
- 实验室列表查询（支持条件筛选）
- 实验室状态管理（可预约/维护中）
- 实验室详情查询

### 3. 预约管理
- 创建预约申请
- 预约审核（通过/拒绝）
- 预约取消
- 预约完成
- 预约评价
- 预约记录查询（支持多种条件）
- 时间冲突检测

### 4. 时间段管理
- 时间段配置的增删改查
- 时间段状态管理
- 时间段排序
- 批量更新排序
- 时间段统计

## 项目结构

```
src/main/java/com/example/shiyanshi/
├── common/              # 公共类
│   └── Result.java      # 统一响应结果封装
├── config/              # 配置类
│   └── WebConfig.java   # Web配置（拦截器、CORS）
├── controller/          # 控制器层
│   ├── UserController.java
│   ├── LaboratoryController.java
│   ├── ReservationController.java
│   └── TimeSlotController.java
├── entity/              # 实体类
│   ├── User.java
│   ├── Laboratory.java
│   ├── Reservation.java
│   └── TimeSlot.java
├── interceptor/         # 拦截器
│   └── JWTInterceptor.java
├── mapper/              # 数据访问层
│   ├── UserMapper.java
│   ├── LaboratoryMapper.java
│   ├── ReservationMapper.java
│   └── TimeSlotMapper.java
├── service/             # 业务逻辑层
│   ├── UserService.java
│   ├── LaboratoryService.java
│   ├── ReservationService.java
│   └── TimeSlotService.java
└── util/                # 工具类
    ├── JWTUtil.java     # JWT工具
    └── MD5Util.java     # MD5加密工具
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置

执行 SQL 脚本初始化数据库：

```bash
mysql -u root -p < src/main/resources/schema.sql
```

或者在 MySQL 客户端中执行 `schema.sql` 文件中的 SQL 语句。

### 3. 配置文件

修改 `src/main/resources/application.properties` 中的数据库连接信息：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lab_reservation?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 4. 运行项目

```bash
# 使用 Maven 运行
mvn spring-boot:run

# 或者先打包再运行
mvn clean package
java -jar target/shiyanshi-0.0.1-SNAPSHOT.jar
```

项目启动后，默认访问地址：`http://localhost:8080`

## API 接口文档

### 用户接口

#### 1. 用户注册
- **接口**: `POST /api/user/register`
- **参数**: 
  ```json
  {
    "username": "用户名",
    "password": "密码",
    "realName": "真实姓名",
    "phone": "手机号",
    "email": "邮箱"
  }
  ```

#### 2. 用户登录
- **接口**: `POST /api/user/login`
- **参数**: 
  ```json
  {
    "username": "用户名",
    "password": "密码"
  }
  ```
- **返回**: 包含 token 的登录信息

#### 3. 获取当前用户信息
- **接口**: `GET /api/user/info`
- **请求头**: `Authorization: Bearer {token}`

#### 4. 更新用户信息
- **接口**: `PUT /api/user/update`
- **请求头**: `Authorization: Bearer {token}`

#### 5. 修改密码
- **接口**: `PUT /api/user/password`
- **请求头**: `Authorization: Bearer {token}`
- **参数**:
  ```json
  {
    "oldPassword": "旧密码",
    "newPassword": "新密码"
  }
  ```

### 实验室接口

#### 1. 查询所有实验室
- **接口**: `GET /api/laboratory/list`
- **参数**: `status`（可选，实验室状态）

#### 2. 查询实验室详情
- **接口**: `GET /api/laboratory/{id}`

#### 3. 添加实验室（需要管理员权限）
- **接口**: `POST /api/laboratory`
- **请求头**: `Authorization: Bearer {token}`

#### 4. 更新实验室（需要管理员权限）
- **接口**: `PUT /api/laboratory`
- **请求头**: `Authorization: Bearer {token}`

#### 5. 删除实验室（需要管理员权限）
- **接口**: `DELETE /api/laboratory/{id}`
- **请求头**: `Authorization: Bearer {token}`

### 预约接口

#### 1. 创建预约
- **接口**: `POST /api/reservation`
- **请求头**: `Authorization: Bearer {token}`
- **参数**:
  ```json
  {
    "labId": 1,
    "reserveDate": "2024-01-15",
    "timeSlot": "第3-4节",
    "purpose": "实验目的说明"
  }
  ```

#### 2. 查询我的预约
- **接口**: `GET /api/reservation/my`
- **请求头**: `Authorization: Bearer {token}`
- **参数**: `status`（可选）

#### 3. 审核预约（管理员）
- **接口**: `PUT /api/reservation/approve/{id}`
- **请求头**: `Authorization: Bearer {token}`

#### 4. 拒绝预约（管理员）
- **接口**: `PUT /api/reservation/reject/{id}`
- **请求头**: `Authorization: Bearer {token}`
- **参数**:
  ```json
  {
    "rejectReason": "拒绝原因"
  }
  ```

#### 5. 取消预约
- **接口**: `PUT /api/reservation/cancel/{id}`
- **请求头**: `Authorization: Bearer {token}`

#### 6. 完成预约
- **接口**: `PUT /api/reservation/complete/{id}`
- **请求头**: `Authorization: Bearer {token}`

#### 7. 评价预约
- **接口**: `PUT /api/reservation/rate/{id}`
- **请求头**: `Authorization: Bearer {token}`
- **参数**:
  ```json
  {
    "rating": 5,
    "comment": "评价内容"
  }
  ```

### 时间段接口

#### 1. 查询所有时间段
- **接口**: `GET /api/timeslot/list`

#### 2. 查询启用的时间段
- **接口**: `GET /api/timeslot/enabled`

#### 3. 添加时间段（管理员）
- **接口**: `POST /api/timeslot`
- **请求头**: `Authorization: Bearer {token}`

#### 4. 更新时间段（管理员）
- **接口**: `PUT /api/timeslot`
- **请求头**: `Authorization: Bearer {token}`

#### 5. 删除时间段（管理员）
- **接口**: `DELETE /api/timeslot/{id}`
- **请求头**: `Authorization: Bearer {token}`

## 初始账号

系统已预置以下测试账号：

### 管理员账号
- 用户名: `admin`
- 密码: `admin123`

### 普通用户账号
- 用户名: `user001` / `user002` / `user003`
- 密码: `123456`

## 数据库设计

### 用户表 (user)
- id: 用户ID（主键）
- username: 用户名（唯一）
- password: 密码（MD5加密）
- real_name: 真实姓名
- phone: 手机号
- email: 邮箱
- user_type: 用户类型（0-普通用户，1-管理员）
- status: 状态（0-禁用，1-启用）
- create_time: 创建时间
- update_time: 更新时间

### 实验室表 (laboratory)
- id: 实验室ID（主键）
- lab_name: 实验室名称
- lab_number: 实验室编号（唯一）
- location: 位置
- capacity: 容纳人数
- equipment: 设备信息
- description: 描述
- status: 状态（0-维护中，1-可预约）
- create_time: 创建时间
- update_time: 更新时间

### 预约表 (reservation)
- id: 预约ID（主键）
- user_id: 用户ID（外键）
- lab_id: 实验室ID（外键）
- reserve_date: 预约日期
- time_slot: 时间段
- purpose: 预约目的
- status: 状态（0-待审核，1-已通过，2-已拒绝，3-已取消，4-已完成）
- reject_reason: 拒绝原因
- rating: 评分（1-5）
- comment: 评价
- create_time: 创建时间
- update_time: 更新时间

### 时间段表 (time_slot)
- id: 时间段ID（主键）
- slot_name: 时间段名称
- start_time: 开始时间
- end_time: 结束时间
- sort_order: 排序号
- status: 状态（0-禁用，1-启用）
- description: 描述
- create_time: 创建时间
- update_time: 更新时间

## 安全机制

1. **JWT认证**: 所有需要认证的接口都需要在请求头中携带 JWT token
2. **密码加密**: 用户密码采用 MD5 加密存储
3. **权限控制**: 通过拦截器验证用户身份和权限
4. **CORS配置**: 支持跨域访问，便于与前端应用集成

## 开发说明

### 添加新的接口

1. 在对应的 Controller 中添加接口方法
2. 在 Service 层实现业务逻辑
3. 在 Mapper 层添加数据库操作
4. 更新 API 文档

### 数据库表变更

1. 修改实体类
2. 更新 Mapper 接口
3. 更新 schema.sql 脚本
4. 测试功能

## 常见问题

### 1. 数据库连接失败
- 检查 MySQL 服务是否启动
- 确认数据库配置信息是否正确
- 检查数据库是否已创建

### 2. Token 验证失败
- 确认是否已登录并获取 token
- 检查 token 是否已过期（默认有效期 24 小时）
- 确认请求头格式：`Authorization: Bearer {token}`

### 3. 端口被占用
- 修改 application.properties 中的端口配置：
  ```properties
  server.port=8081
  ```

## 后续优化建议

1. 增加接口文档（Swagger/Knife4j）
2. 添加日志记录功能
3. 实现文件上传功能（实验室图片、用户头像）
4. 添加数据统计和报表功能
5. 实现消息通知功能
6. 添加单元测试和集成测试
7. 优化数据库查询性能
8. 添加缓存机制（Redis）

## 可优化项
- 签到打卡功能（提升预约真实性）

- 预约评价系统（提升服务质量）

- 消息推送（提升用户体验）

- 统计分析功能（数据驱动决策）

- 操作日志记录（系统安全审计



## 联系方式

如有问题或建议，欢迎反馈。

## 许可证

本项目仅供学习和研究使用。
