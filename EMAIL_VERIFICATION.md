# 邮箱验证功能说明文档

## 功能概述

邮箱验证功能为实验室预约系统提供了完整的邮箱验证和管理能力，包括注册验证、验证码发送、密码重置等功能。

## 核心功能

### 1. 注册邮箱验证

用户注册后可以验证邮箱，系统会发送包含验证链接的邮件。

**接口：** `POST /api/user/send-register-email`

**请求参数：**
```json
{
  "email": "user@example.com",
  "username": "张三"
}
```

**验证流程：**
1. 用户点击邮件中的验证链接
2. 系统验证token有效性（30分钟有效期）
3. 更新用户邮箱验证状态为已验证

**验证接口：** `GET /api/user/verify-email?token={token}`

### 2. 验证码功能

系统支持多种场景的验证码发送，包括注册、重置密码、绑定邮箱等。

**发送验证码接口：** `POST /api/user/send-code`

**请求参数：**
```json
{
  "email": "user@example.com",
  "purpose": "register"  // register, reset-password, bind-email
}
```

**验证码特性：**
- 6位数字验证码
- 5分钟有效期
- 发送后存储在Redis中

**验证验证码接口：** `POST /api/user/verify-code`

**请求参数：**
```json
{
  "email": "user@example.com",
  "code": "123456"
}
```

### 3. 通过邮箱重置密码

用户可以通过邮箱验证码重置密码，无需记住旧密码。

**接口：** `POST /api/user/reset-password-by-email`

**请求参数：**
```json
{
  "email": "user@example.com",
  "code": "123456",
  "newPassword": "newPassword123"
}
```

**流程：**
1. 用户请求发送验证码
2. 输入收到的验证码和新密码
3. 系统验证验证码有效性
4. 更新用户密码

### 4. 绑定邮箱

已注册用户可以绑定或更换邮箱地址。

**接口：** `POST /api/user/bind-email`

**请求参数：**
```json
{
  "userId": 1,
  "email": "newemail@example.com",
  "code": "123456"
}
```

**流程：**
1. 用户输入新邮箱地址
2. 请求发送验证码到新邮箱
3. 输入验证码完成绑定
4. 邮箱验证状态自动设置为已验证

### 5. 重新发送验证邮件

用户可以重新请求发送验证邮件。

**接口：** `POST /api/user/resend-verify-email`

**请求参数：**
```json
{
  "userId": 1
}
```

## 邮件模板

系统包含5个精美的HTML邮件模板：

### 1. 注册验证邮件 (email-register.html)
- 欢迎消息
- 大型验证按钮
- 渐变色设计
- 30分钟有效期提示

### 2. 验证码邮件 (email-code.html)
- 大字号验证码显示
- 用途说明
- 5分钟有效期提示
- 安全提示

### 3. 预约通知邮件 (email-reservation.html)
- 预约详情展示
- 实验室信息
- 预约时间
- 状态标识

### 4. 审核结果邮件 (email-approval.html)
- 审核结果（通过/拒绝）
- 审核原因
- 下一步操作指引

### 5. 预约提醒邮件 (email-reminder.html)
- 倒计时提醒
- 实验室位置
- 温馨提示
- 准备清单

## 技术实现

### 依赖组件

```xml
<!-- 邮件服务 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- Thymeleaf模板引擎 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- Redis缓存 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 配置说明

需要在 `application.properties` 中配置：

```properties
# 邮件服务器配置
spring.mail.host=smtp.qq.com
spring.mail.port=587
spring.mail.username=your-email@qq.com
spring.mail.password=your-smtp-authorization-code
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# 系统邮件配置
system.email.from=your-email@qq.com
system.email.from-name=实验室预约系统
system.base-url=http://localhost:8080

# Redis配置
spring.data.redis.host=localhost
spring.data.redis.port=6379

# 验证码配置
verification.code.expire-minutes=5
verification.code.length=6
```

### 数据库变更

执行以下SQL脚本添加邮箱验证字段：

```sql
ALTER TABLE `user` ADD COLUMN `email_verified` INT DEFAULT 0 COMMENT '邮箱验证状态：0-未验证 1-已验证';
CREATE INDEX idx_email ON `user`(`email`);
CREATE INDEX idx_email_verified ON `user`(`email_verified`);
```

## 核心类说明

### EmailService
邮件服务核心类，提供以下方法：

- `sendRegisterVerifyEmail()` - 发送注册验证邮件
- `sendVerificationCode()` - 发送验证码邮件
- `verifyCode()` - 验证验证码
- `verifyEmailToken()` - 验证邮箱token
- `sendReservationNotification()` - 发送预约通知
- `sendApprovalNotification()` - 发送审核通知
- `sendReservationReminder()` - 发送预约提醒

### UserService
用户服务扩展，新增方法：

- `existsByEmail()` - 检查邮箱是否存在
- `updateEmailVerified()` - 更新邮箱验证状态
- `resetPasswordByEmail()` - 通过邮箱重置密码
- `bindEmail()` - 绑定邮箱
- `findByEmail()` - 根据邮箱查询用户

### UserMapper
数据访问层扩展，新增方法：

- `findByEmail()` - 根据邮箱查询用户
- `updateEmailVerified()` - 更新邮箱验证状态
- `updateEmailByUserId()` - 更新用户邮箱

## 安全考虑

1. **Token安全**
   - 使用UUID生成随机token
   - Token存储在Redis中，设置过期时间
   - 验证后立即删除token，防止重复使用

2. **验证码安全**
   - 随机生成6位数字验证码
   - 5分钟有效期
   - 验证后立即删除，一次性使用

3. **邮箱验证**
   - 验证邮箱是否已被占用
   - 防止重复绑定
   - 验证码验证通过后才能进行敏感操作

4. **密码安全**
   - 使用MD5加密存储密码
   - 重置密码需要邮箱验证码
   - 不在接口中返回密码字段

## 使用示例

### 前端集成示例

```javascript
// 1. 发送注册验证邮件
async function sendRegisterEmail(email, username) {
  const response = await fetch('/api/user/send-register-email', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, username })
  });
  const result = await response.json();
  return result;
}

// 2. 发送验证码
async function sendVerificationCode(email, purpose) {
  const response = await fetch('/api/user/send-code', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, purpose })
  });
  const result = await response.json();
  return result;
}

// 3. 验证验证码
async function verifyCode(email, code) {
  const response = await fetch('/api/user/verify-code', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, code })
  });
  const result = await response.json();
  return result;
}

// 4. 通过邮箱重置密码
async function resetPasswordByEmail(email, code, newPassword) {
  const response = await fetch('/api/user/reset-password-by-email', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, code, newPassword })
  });
  const result = await response.json();
  return result;
}
```

## QQ邮箱授权码获取

使用QQ邮箱发送邮件需要获取授权码（不是QQ密码）：

1. 登录QQ邮箱网页版
2. 点击"设置" -> "账户"
3. 找到"POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务"
4. 开启"POP3/SMTP服务"或"IMAP/SMTP服务"
5. 点击"生成授权码"
6. 按提示发送短信后获得16位授权码
7. 将授权码配置到 `spring.mail.password`

## 常见问题

### 1. 邮件发送失败
**可能原因：**
- 邮箱授权码配置错误
- SMTP服务器地址或端口错误
- 网络连接问题
- 邮箱服务未开启SMTP服务

**解决方案：**
- 检查配置文件中的邮箱设置
- 确认已获取正确的授权码
- 测试网络连接
- 查看日志获取详细错误信息

### 2. Redis连接失败
**可能原因：**
- Redis服务未启动
- Redis连接配置错误
- 防火墙阻止连接

**解决方案：**
- 启动Redis服务：`redis-server`
- 检查Redis配置
- 测试Redis连接：`redis-cli ping`

### 3. 验证码收不到
**可能原因：**
- 邮件被拦截到垃圾箱
- 邮件发送失败
- 邮箱地址输入错误

**解决方案：**
- 检查垃圾邮件文件夹
- 查看服务器日志
- 确认邮箱地址正确

## 后续扩展

邮箱验证功能已完成，接下来将继续实现：

1. **站内消息系统** - 用户间消息通知
2. **通知增强** - 预约状态变更自动发送邮件
3. **预约功能增强** - 冲突检测、连续预约、签到签退
4. **权限管理** - 角色权限、菜单管理
5. **二维码功能** - 实验室二维码、签到扫码

## 更新日志

### v1.0.0 (2026-01-11)
- ✅ 实现注册邮箱验证功能
- ✅ 实现验证码发送和验证
- ✅ 实现通过邮箱重置密码
- ✅ 实现邮箱绑定功能
- ✅ 创建5个邮件HTML模板
- ✅ 集成Redis缓存
- ✅ 添加数据库迁移脚本
