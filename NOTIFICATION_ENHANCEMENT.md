# 通知增强功能文档

## 功能概述

通知增强功能为实验室预约系统提供了全面的通知机制，包括：
- 预约提醒定时任务（提前24小时、1小时、30分钟和当天提醒）
- 预约状态变更通知（审核通过/拒绝、取消、完成）
- 站内消息和邮件双通道通知
- 自动处理过期预约

## 核心组件

### 1. 预约提醒定时任务 (ReservationReminderTask)

**文件位置**: `src/main/java/com/example/shiyanshi/task/ReservationReminderTask.java`

**主要功能**:
- 提前24小时提醒（每小时执行一次）
- 提前1小时提醒（每10分钟执行一次）
- 提前30分钟提醒（每5分钟执行一次）
- 当天预约提醒（每天凌晨1点执行）
- 过期预约处理（每天凌晨2点执行）

**定时任务配置**:
```java
// 提前24小时提醒 - 每小时执行
@Scheduled(cron = "0 0 * * * ?")
public void remind24Hours()

// 提前1小时提醒 - 每10分钟执行
@Scheduled(cron = "0 */10 * * * ?")
public void remind1Hour()

// 提前30分钟提醒 - 每5分钟执行
@Scheduled(cron = "0 */5 * * * ?")
public void remind30Minutes()

// 当天预约提醒 - 每天凌晨1点执行
@Scheduled(cron = "0 0 1 * * ?")
public void remindToday()

// 过期预约处理 - 每天凌晨2点执行
@Scheduled(cron = "0 0 2 * * ?")
public void handleExpiredReservations()
```

### 2. 预约状态变更通知

**集成位置**: `src/main/java/com/example/shiyanshi/service/ReservationService.java`

**通知场景**:

#### 审核通过/拒绝通知
```java
// 在 approve() 方法中
messageService.sendApprovalMessage(
    reservation.getUserId(),
    title,
    content,
    reservation.getId(),
    1
);
emailService.sendApprovalNotification(email, username, reservationInfo);
```

#### 预约取消通知
```java
// 在 cancel() 方法中
messageService.sendSystemMessage(
    reservation.getUserId(),
    title,
    content,
    1
);
emailService.sendApprovalNotification(email, username, reservationInfo);
```

#### 预约完成通知
```java
// 在 complete() 方法中
messageService.sendSystemMessage(
    reservation.getUserId(),
    title,
    content,
    0
);
```

## 通知渠道

### 1. 站内消息

通过 `MessageService` 发送站内消息，支持：
- 系统消息：`sendSystemMessage()`
- 审核消息：`sendApprovalMessage()`
- 提醒消息：`sendReminderMessage()`

详细说明参见 [MESSAGE_SYSTEM.md](MESSAGE_SYSTEM.md)

### 2. 邮件通知

通过 `EmailService` 发送邮件通知，使用Thymeleaf模板引擎渲染：
- 预约通知：`email-reservation.html`
- 审核通知：`email-approval.html`
- 提醒通知：`email-reminder.html`

详细说明参见 [EMAIL_VERIFICATION.md](EMAIL_VERIFICATION.md)

## 配置说明

### 1. 启用定时任务

确保主应用类添加了 `@EnableScheduling` 注解：

```java
@SpringBootApplication
@EnableScheduling
public class ShiyanshiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShiyanshiApplication.class, args);
    }
}
```

### 2. 邮件配置

在 `application.properties` 中配置邮件服务：

```properties
# 邮件配置
spring.mail.host=smtp.example.com
spring.mail.port=587
spring.mail.username=your-email@example.com
spring.mail.password=your-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

### 3. 定时任务调整

如需调整定时任务执行频率，修改 `@Scheduled` 注解的 cron 表达式：

**Cron表达式格式**: `秒 分 时 日 月 周`

**示例**:
- `0 0 * * * ?` - 每小时执行
- `0 */10 * * * ?` - 每10分钟执行
- `0 */5 * * * ?` - 每5分钟执行
- `0 0 1 * * ?` - 每天凌晨1点执行

## 提醒时间规则

### 1. 提前24小时提醒
- **触发时间**: 预约开始时间前24-25小时
- **执行频率**: 每小时检查一次
- **通知内容**: 包含实验室名称、预约时间、注意事项

### 2. 提前1小时提醒
- **触发时间**: 预约开始时间前1-1.2小时
- **执行频率**: 每10分钟检查一次
- **通知内容**: 包含实验室名称、预约时间、准备提示

### 3. 提前30分钟提醒
- **触发时间**: 预约开始时间前30-35分钟
- **执行频率**: 每5分钟检查一次
- **通知内容**: 包含实验室名称、预约时间、即将开始提示

### 4. 当天预约提醒
- **触发时间**: 每天凌晨1点
- **检查范围**: 当天的所有预约
- **通知内容**: 当天预约的概览信息

### 5. 过期预约处理
- **触发时间**: 每天凌晨2点
- **处理规则**: 结束时间小于当前时间且状态为"已批准"的预约自动标记为"已完成"

## 通知消息模板

### 1. 审核通过消息
```
标题: 预约审核通过
内容: 您的实验室预约已通过审核！
      实验室: [实验室名称]
      时间: [开始时间] - [结束时间]
      请准时使用实验室。
```

### 2. 审核拒绝消息
```
标题: 预约审核未通过
内容: 很遗憾，您的实验室预约未通过审核。
      实验室: [实验室名称]
      时间: [开始时间] - [结束时间]
      原因: [备注信息]
```

### 3. 预约取消消息
```
标题: 预约已取消
内容: 您的实验室预约已被取消。
      实验室: [实验室名称]
      时间: [开始时间] - [结束时间]
```

### 4. 预约完成消息
```
标题: 预约已完成
内容: 您的实验室使用已完成，感谢您的使用！
      实验室: [实验室名称]
      时间: [开始时间] - [结束时间]
```

### 5. 预约提醒消息
```
标题: 预约提醒
内容: 您有一个预约即将开始，请做好准备！
      实验室: [实验室名称]
      时间: [开始时间] - [结束时间]
      距离开始还有[X]小时/分钟
```

## 数据库依赖

通知增强功能依赖以下数据库表：
- `user` - 用户信息（邮箱地址）
- `reservation` - 预约信息
- `laboratory` - 实验室信息
- `message` - 站内消息记录

相关迁移脚本：
- `V2__Add_Email_Verified_Field.sql` - 添加邮箱验证字段
- `V3__Create_Message_Table.sql` - 创建消息表

## 日志记录

系统使用 `@Slf4j` 注解记录关键操作日志：

```java
log.info("开始执行提前24小时提醒任务");
log.info("找到{}个需要提醒的预约", reservations.size());
log.info("发送提醒给用户：{}", user.getUsername());
log.error("发送提醒失败", e);
```

## 错误处理

### 1. 用户不存在
```java
if (user == null) {
    log.error("用户不存在: {}", reservation.getUserId());
    continue;
}
```

### 2. 实验室不存在
```java
if (laboratory == null) {
    log.error("实验室不存在: {}", reservation.getLaboratoryId());
    continue;
}
```

### 3. 通知发送失败
系统会捕获异常并记录日志，不会影响其他预约的提醒：
```java
try {
    // 发送通知
} catch (Exception e) {
    log.error("发送提醒失败", e);
}
```

## 性能优化建议

### 1. 批量查询优化
```java
// 使用 IN 查询批量获取用户信息
List<Long> userIds = reservations.stream()
    .map(Reservation::getUserId)
    .distinct()
    .collect(Collectors.toList());
Map<Long, User> userMap = userService.listByIds(userIds).stream()
    .collect(Collectors.toMap(User::getId, u -> u));
```

### 2. 异步发送
对于大量通知，建议使用异步方式发送：
```java
@Async
public void sendNotificationAsync(Long userId, String title, String content) {
    // 发送通知
}
```

### 3. 消息队列
对于高并发场景，建议引入消息队列（如RabbitMQ、Kafka）：
```java
// 将通知任务推送到消息队列
rabbitTemplate.convertAndSend("notification.queue", notificationMessage);
```

## 测试说明

### 1. 单元测试
测试各个定时任务方法：
```java
@Test
public void testRemind24Hours() {
    // 测试提前24小时提醒
}
```

### 2. 集成测试
测试完整的通知流程：
```java
@Test
public void testReservationApprovalNotification() {
    // 测试预约审核通知流程
}
```

### 3. 手动测试
1. 创建测试预约
2. 调整定时任务执行时间
3. 观察控制台日志
4. 检查站内消息和邮件

## 常见问题

### Q1: 定时任务没有执行？
**A**: 检查以下几点：
1. 主应用类是否添加了 `@EnableScheduling` 注解
2. 定时任务类是否添加了 `@Component` 注解
3. cron表达式是否正确
4. 查看控制台日志是否有错误信息

### Q2: 邮件发送失败？
**A**: 检查以下几点：
1. 邮件服务器配置是否正确
2. 邮箱账号密码是否正确
3. 是否开启了SMTP服务
4. 邮箱是否设置了授权码
5. 查看详细的异常信息

### Q3: 收不到站内消息？
**A**: 检查以下几点：
1. 用户ID是否正确
2. message表是否正确创建
3. MessageService是否正确注入
4. 查看数据库是否有消息记录

### Q4: 提醒时间不准确？
**A**: 检查以下几点：
1. 服务器时区是否正确
2. 数据库时区是否正确
3. LocalDateTime的处理是否正确
4. 定时任务的cron表达式是否符合预期

## 功能扩展

### 1. 微信通知
集成微信公众号或企业微信，发送模板消息：
```java
public void sendWechatNotification(String openId, Map<String, Object> data) {
    // 调用微信API发送模板消息
}
```

### 2. 短信通知
集成短信服务商（如阿里云、腾讯云），发送短信通知：
```java
public void sendSmsNotification(String phone, String content) {
    // 调用短信API发送通知
}
```

### 3. 通知偏好设置
允许用户自定义通知方式和时间：
```java
@Entity
public class NotificationPreference {
    private Long userId;
    private Boolean emailEnabled;
    private Boolean messageEnabled;
    private Boolean smsEnabled;
    private Integer reminderMinutes;  // 提前多少分钟提醒
}
```

### 4. 通知历史记录
记录所有发送的通知，便于追踪和统计：
```java
@Entity
public class NotificationLog {
    private Long id;
    private Long userId;
    private String type;  // email/message/sms/wechat
    private String title;
    private String content;
    private Boolean success;
    private LocalDateTime sendTime;
}
```

## 相关文档

- [邮箱验证功能](EMAIL_VERIFICATION.md)
- [站内消息系统](MESSAGE_SYSTEM.md)
- [功能增强计划](FEATURE_ENHANCEMENT_PLAN.md)

## 更新日志

### v1.0.0 (2026-01-11)
- ✅ 实现预约提醒定时任务
- ✅ 实现预约状态变更通知
- ✅ 集成站内消息和邮件双通道
- ✅ 实现过期预约自动处理
- ✅ 完善日志记录和错误处理
