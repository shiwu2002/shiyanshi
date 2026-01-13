# 实验室预约系统功能扩展方案

## 目录
1. [当前系统功能概览](#当前系统功能概览)
2. [核心功能扩展建议](#核心功能扩展建议)
3. [邮箱验证功能实现方案](#邮箱验证功能实现方案)
4. [通知系统实现方案](#通知系统实现方案)
5. [其他推荐功能](#其他推荐功能)

---

## 当前系统功能概览

### 已实现功能
✅ 用户管理（注册、登录、信息修改、密码管理）
✅ 实验室管理（CRUD操作）
✅ 时间段管理
✅ 预约管理（创建、审核、取消、查询）
✅ 文件上传（头像、附件）
✅ 报表导出（预约明细、统计分析）
✅ JWT身份认证
✅ 数据加密（MD5密码加密）

### 系统架构
- **后端框架**: Spring Boot 4.0.1
- **持久层**: MyBatis-Plus
- **数据库**: MySQL
- **文件处理**: Apache POI
- **认证方式**: JWT Token

---

## 核心功能扩展建议

### 1. 用户认证增强 ⭐⭐⭐⭐⭐
#### 1.1 邮箱验证功能
- **注册邮箱验证**: 用户注册时发送验证邮件
- **登录邮箱验证**: 支持邮箱+密码登录
- **找回密码**: 通过邮箱重置密码
- **邮箱绑定/解绑**: 已有用户绑定邮箱

#### 1.2 手机验证功能
- **短信验证码**: 注册/登录/找回密码
- **手机号绑定**: 账号安全加固

#### 1.3 第三方登录
- **微信登录**: 小程序端直接微信授权登录
- **QQ/支付宝登录**: Web端社交登录

### 2. 通知系统 ⭐⭐⭐⭐⭐
#### 2.1 站内消息
- **系统通知**: 预约状态变更、审核结果
- **消息中心**: 未读消息提醒、消息列表
- **消息推送**: WebSocket实时推送

#### 2.2 邮件通知
- **预约确认**: 预约提交成功通知
- **审核通知**: 审核通过/拒绝通知
- **提醒通知**: 预约前24小时/1小时提醒
- **取消通知**: 预约取消通知

#### 2.3 短信通知
- **重要通知**: 审核结果、预约提醒
- **验证码**: 登录、找回密码验证

#### 2.4 小程序通知
- **订阅消息**: 微信小程序订阅消息推送
- **模板消息**: 预约状态变更通知

### 3. 预约功能增强 ⭐⭐⭐⭐
#### 3.1 智能预约
- **冲突检测**: 实时检测时间冲突
- **容量控制**: 实验室人数上限控制
- **连续预约**: 支持预约连续多个时间段
- **周期预约**: 支持按周/月周期性预约

#### 3.2 预约管理
- **预约审批流**: 多级审批机制
- **预约评价**: 使用后评价实验室和设备
- **预约统计**: 个人预约历史统计
- **黑名单机制**: 违规用户限制预约

#### 3.3 签到签退
- **扫码签到**: 二维码签到入场
- **自动签退**: 超时自动签退
- **使用记录**: 完整的使用时长记录

### 4. 实验室管理增强 ⭐⭐⭐⭐
#### 4.1 设备管理
- **设备清单**: 实验室设备详细信息
- **设备状态**: 可用/维护/报废状态
- **设备预约**: 指定设备预约
- **设备维护**: 维护记录和计划

#### 4.2 规则配置
- **预约规则**: 可配置预约时间限制
- **审核规则**: 自动审核/人工审核规则
- **开放时间**: 灵活配置实验室开放时间

#### 4.3 容量管理
- **动态容量**: 根据时段调整容量
- **分组管理**: 实验室分类分组
- **资源调度**: 智能推荐可用实验室

### 5. 数据分析与报表 ⭐⭐⭐⭐
#### 5.1 统计分析
- **使用率分析**: 实验室使用率统计
- **热力图**: 预约时间热力分布
- **用户画像**: 用户行为分析
- **趋势预测**: 预约趋势预测

#### 5.2 可视化报表
- **图表展示**: ECharts可视化
- **实时大屏**: 数据大屏展示
- **自定义报表**: 报表模板自定义

### 6. 系统管理功能 ⭐⭐⭐
#### 6.1 权限管理
- **角色管理**: 自定义角色权限
- **菜单管理**: 动态菜单配置
- **数据权限**: 行级数据权限控制

#### 6.2 日志管理
- **操作日志**: 记录用户操作
- **登录日志**: 登录记录和安全审计
- **系统日志**: 异常日志记录

#### 6.3 系统配置
- **参数配置**: 系统参数在线配置
- **字典管理**: 数据字典维护
- **公告管理**: 系统公告发布

### 7. 移动端功能 ⭐⭐⭐⭐
#### 7.1 小程序端
- **扫码预约**: 扫实验室二维码快速预约
- **扫码签到**: 扫码入场签到
- **消息推送**: 实时消息提醒
- **位置导航**: 实验室位置导航

#### 7.2 移动优化
- **响应式设计**: 适配各种屏幕
- **离线缓存**: 离线数据缓存
- **快捷操作**: 常用功能快捷入口

### 8. 安全增强 ⭐⭐⭐⭐
#### 8.1 数据安全
- **数据加密**: 敏感数据加密存储
- **接口加密**: API接口数据加密
- **SQL防注入**: 参数化查询

#### 8.2 访问控制
- **IP白名单**: 管理后台IP限制
- **请求限流**: 防止恶意请求
- **验证码**: 登录/注册验证码

#### 8.3 备份恢复
- **数据备份**: 定时自动备份
- **灾难恢复**: 快速恢复机制

---

## 邮箱验证功能实现方案

### 方案架构

```
用户注册 -> 发送验证邮件 -> 点击验证链接 -> 激活账号
用户登录 -> 忘记密码 -> 邮箱验证 -> 重置密码
```

### 技术选型

#### 方案一：使用Spring Boot Mail（推荐）
**优点**：
- Spring官方支持，集成简单
- 配置灵活，支持多种邮件服务器
- 支持HTML模板、附件等

**缺点**：
- 需要SMTP服务器配置
- 发送速度相对较慢

#### 方案二：使用第三方邮件服务（推荐用于生产环境）
如：阿里云邮件推送、腾讯云邮件服务、SendGrid

**优点**：
- 发送速度快，稳定性高
- 送达率高，避免进入垃圾箱
- 提供发送统计和分析

**缺点**：
- 需要付费（有免费额度）
- 需要域名验证

### 实现步骤

#### 1. 添加依赖（pom.xml）

```xml
<!-- Spring Boot Mail -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>

<!-- Thymeleaf模板引擎（用于邮件模板） -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- Redis（用于存储验证码） -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

#### 2. 配置邮件服务（application.properties）

```properties
# 邮件配置（以QQ邮箱为例）
spring.mail.host=smtp.qq.com
spring.mail.port=587
spring.mail.username=your-email@qq.com
spring.mail.password=your-auth-code
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.default-encoding=UTF-8

# Redis配置（用于存储验证码，5分钟过期）
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.database=0
spring.redis.timeout=5000

# 系统配置
system.email.from=your-email@qq.com
system.email.personal=实验室预约系统
system.base-url=http://localhost:8080
```

#### 3. 创建邮件服务类

```java
package com.example.shiyanshi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;
    private final TemplateEngine templateEngine;
    
    @Value("${system.email.from}")
    private String fromEmail;
    
    @Value("${system.email.personal}")
    private String personal;
    
    @Value("${system.base-url}")
    private String baseUrl;
    
    /**
     * 发送注册验证邮件
     */
    public void sendRegisterVerifyEmail(String toEmail, String username) {
        try {
            // 生成验证token
            String token = UUID.randomUUID().toString().replace("-", "");
            // 存储到Redis，30分钟过期
            redisTemplate.opsForValue().set("email:verify:" + token, toEmail, 30, TimeUnit.MINUTES);
            
            // 构建验证链接
            String verifyUrl = baseUrl + "/api/user/verify-email?token=" + token;
            
            // 准备邮件内容
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("verifyUrl", verifyUrl);
            String content = templateEngine.process("email-register", context);
            
            // 发送邮件
            sendHtmlEmail(toEmail, "【实验室预约系统】账号激活", content);
            
            log.info("注册验证邮件已发送至: {}", toEmail);
        } catch (Exception e) {
            log.error("发送注册验证邮件失败", e);
            throw new RuntimeException("发送验证邮件失败");
        }
    }
    
    /**
     * 发送验证码邮件
     */
    public String sendVerificationCode(String toEmail, String purpose) {
        try {
            // 生成6位验证码
            String code = String.format("%06d", new Random().nextInt(1000000));
            
            // 存储到Redis，5分钟过期
            String key = "email:code:" + purpose + ":" + toEmail;
            redisTemplate.opsForValue().set(key, code, 5, TimeUnit.MINUTES);
            
            // 准备邮件内容
            Context context = new Context();
            context.setVariable("code", code);
            context.setVariable("purpose", getPurposeText(purpose));
            String content = templateEngine.process("email-code", context);
            
            // 发送邮件
            sendHtmlEmail(toEmail, "【实验室预约系统】验证码", content);
            
            log.info("验证码邮件已发送至: {}, 用途: {}", toEmail, purpose);
            return code;
        } catch (Exception e) {
            log.error("发送验证码邮件失败", e);
            throw new RuntimeException("发送验证码失败");
        }
    }
    
    /**
     * 验证邮箱验证码
     */
    public boolean verifyCode(String email, String code, String purpose) {
        String key = "email:code:" + purpose + ":" + email;
        String savedCode = redisTemplate.opsForValue().get(key);
        if (savedCode != null && savedCode.equals(code)) {
            // 验证成功后删除验证码
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }
    
    /**
     * 发送预约通知邮件
     */
    public void sendReservationNotification(String toEmail, String username, 
                                           String labName, String reserveDate, 
                                           String timeSlot, String status) {
        try {
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("labName", labName);
            context.setVariable("reserveDate", reserveDate);
            context.setVariable("timeSlot", timeSlot);
            context.setVariable("status", status);
            
            String content = templateEngine.process("email-reservation", context);
            String subject = "【实验室预约系统】预约" + getStatusText(status);
            
            sendHtmlEmail(toEmail, subject, content);
            log.info("预约通知邮件已发送至: {}", toEmail);
        } catch (Exception e) {
            log.error("发送预约通知邮件失败", e);
        }
    }
    
    /**
     * 发送审核结果邮件
     */
    public void sendApprovalNotification(String toEmail, String username,
                                        String labName, String reserveDate,
                                        boolean approved, String comment) {
        try {
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("labName", labName);
            context.setVariable("reserveDate", reserveDate);
            context.setVariable("approved", approved);
            context.setVariable("comment", comment);
            
            String content = templateEngine.process("email-approval", context);
            String subject = approved ? 
                "【实验室预约系统】预约审核通过" : "【实验室预约系统】预约审核未通过";
            
            sendHtmlEmail(toEmail, subject, content);
            log.info("审核通知邮件已发送至: {}", toEmail);
        } catch (Exception e) {
            log.error("发送审核通知邮件失败", e);
        }
    }
    
    /**
     * 发送预约提醒邮件
     */
    public void sendReservationReminder(String toEmail, String username,
                                       String labName, String reserveDate,
                                       String timeSlot, int hoursUntil) {
        try {
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("labName", labName);
            context.setVariable("reserveDate", reserveDate);
            context.setVariable("timeSlot", timeSlot);
            context.setVariable("hoursUntil", hoursUntil);
            
            String content = templateEngine.process("email-reminder", context);
            sendHtmlEmail(toEmail, "【实验室预约系统】预约提醒", content);
            log.info("预约提醒邮件已发送至: {}", toEmail);
        } catch (Exception e) {
            log.error("发送预约提醒邮件失败", e);
        }
    }
    
    /**
     * 发送HTML邮件
     */
    private void sendHtmlEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail, personal);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("发送邮件失败", e);
            throw new RuntimeException("发送邮件失败: " + e.getMessage());
        }
    }
    
    private String getPurposeText(String purpose) {
        switch (purpose) {
            case "register": return "注册账号";
            case "login": return "登录验证";
            case "reset-password": return "重置密码";
            case "bind-email": return "绑定邮箱";
            default: return "身份验证";
        }
    }
    
    private String getStatusText(String status) {
        switch (status) {
            case "submitted": return "提交成功";
            case "approved": return "审核通过";
            case "rejected": return "审核未通过";
            case "cancelled": return "已取消";
            default: return "状态更新";
        }
    }
}
```

#### 4. 创建邮件模板

在 `src/main/resources/templates/` 目录下创建邮件模板：

**email-register.html（注册验证邮件）**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background: #4CAF50; color: white; padding: 20px; text-align: center; }
        .content { background: #f9f9f9; padding: 30px; }
        .button { display: inline-block; padding: 12px 30px; background: #4CAF50; 
                  color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>欢迎注册实验室预约系统</h2>
        </div>
        <div class="content">
            <p>尊敬的 <strong th:text="${username}"></strong>，您好！</p>
            <p>感谢您注册实验室预约系统。请点击下方按钮激活您的账号：</p>
            <p style="text-align: center;">
                <a th:href="${verifyUrl}" class="button">激活账号</a>
            </p>
            <p>或复制以下链接到浏览器打开：</p>
            <p style="word-break: break-all; color: #666;" th:text="${verifyUrl}"></p>
            <p style="color: #f44336;">此链接30分钟内有效，请尽快完成验证。</p>
        </div>
        <div class="footer">
            <p>此邮件由系统自动发送，请勿回复。</p>
            <p>© 2024 实验室预约系统</p>
        </div>
    </div>
</body>
</html>
```

**email-code.html（验证码邮件）**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background: #2196F3; color: white; padding: 20px; text-align: center; }
        .content { background: #f9f9f9; padding: 30px; }
        .code { font-size: 32px; font-weight: bold; color: #2196F3; 
                text-align: center; padding: 20px; background: white; 
                border: 2px dashed #2196F3; margin: 20px 0; }
        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h2>验证码</h2>
        </div>
        <div class="content">
            <p>您正在进行 <strong th:text="${purpose}"></strong> 操作，验证码为：</p>
            <div class="code" th:text="${code}"></div>
            <p style="color: #f44336;">验证码5分钟内有效，请勿泄露给他人。</p>
            <p>如非本人操作，请忽略此邮件。</p>
        </div>
        <div class="footer">
            <p>此邮件由系统自动发送，请勿回复。</p>
            <p>© 2024 实验室预约系统</p>
        </div>
    </div>
</body>
</html>
```

#### 5. 更新用户实体类

在User实体类中添加邮箱验证相关字段：
```java
private Integer emailVerified;  // 邮箱是否已验证：0-未验证 1-已验证
```

#### 6. 更新用户注册接口

```java
@PostMapping("/register")
public Result<User> register(@RequestBody User user) {
    try {
        // 设置邮箱未验证状态
        user.setEmailVerified(0);
        User newUser = userService.register(user);
        
        // 发送验证邮件
        if (newUser.getEmail() != null && !newUser.getEmail().isEmpty()) {
            emailService.sendRegisterVerifyEmail(newUser.getEmail(), newUser.getUsername());
        }
        
        return Result.success("注册成功，请查收验证邮件", newUser);
    } catch (Exception e) {
        return Result.error(e.getMessage());
    }
}
```

#### 7. 添加邮箱验证接口

```java
@GetMapping("/verify-email")
public Result<Void> verifyEmail(@RequestParam String token) {
    try {
        String email = redisTemplate.opsForValue().get("email:verify:" + token);
        if (email == null) {
            return Result.error("验证链接已失效");
        }
        
        // 更新用户邮箱验证状态
        userService.updateEmailVerified(email);
        
        // 删除token
        redisTemplate.delete("email:verify:" + token);
        
        return Result.success("邮箱验证成功", null);
    } catch (Exception e) {
        return Result.error(e.getMessage());
    }
}
```

#### 8. 添加忘记密码接口

```java
@PostMapping("/forgot-password")
public Result<Void> forgotPassword(@RequestBody Map<String, String> params) {
    try {
        String email = params.get("email");
        // 发送验证码
        emailService.sendVerificationCode(email, "reset-password");
        return Result.success("验证码已发送至邮箱", null);
    } catch (Exception e) {
        return Result.error(e.getMessage());
    }
}

@PostMapping("/reset-password-by-email")
public Result<Void> resetPasswordByEmail(@RequestBody Map<String, String> params) {
    try {
        String email = params.get("email");
        String code = params.get("code");
        String newPassword = params.get("newPassword");
        
        // 验证验证码
        if (!emailService.verifyCode(email, code, "reset-password")) {
            return Result.error("验证码错误或已过期");
        }
        
        // 重置密码
        userService.resetPasswordByEmail(email, newPassword);
        return Result.success("密码重置成功", null);
    } catch (Exception e) {
        return Result.error(e.getMessage());
    }
}
```

---

## 通知系统实现方案

### 通知类型对比

| 通知方式 | 实时性 | 成本 | 到达率 | 适用场景 |
|---------|--------|------|--------|----------|
| 站内消息 | 高 | 无 | 中 | 所有通知 |
| 邮件通知 | 中 | 低 | 高 | 重要通知 |
| 短信通知 | 高 | 高 | 极高 | 紧急通知 |
| 小程序推送 | 高 | 无 | 高 | 即时通知 |

### 方案一：站内消息系统（推荐优先实现）

#### 1. 创建消息实体类

```java
package com.example.shiyanshi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;           // 接收用户ID
    private String title;          // 消息标题
    private String content;        // 消息内容
    private Integer type;          // 消息类型：1-系统通知 2-预约通知 3-审核通知 4-提醒通知
    private Integer status;        // 状态：0-未读 1-已读
    private Long relatedId;        // 关联ID（如预约ID）
    private String relatedType;    // 关联类型（如reservation）
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    private LocalDateTime readTime;  // 阅读时间
}
```

#### 2. 创建消息服务类

```java
@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageMapper messageMapper;
    
    /**
     * 发送消息
     */
    public void sendMessage(Long userId, String title, String content, 
                          Integer type, Long relatedId, String relatedType) {
        Message message = new Message();
        message.setUserId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        message.setStatus(0);
        message.setRelatedId(relatedId);
        message.setRelatedType(relatedType);
        messageMapper.insert(message);
    }
    
    /**
     * 获取用户未读消息数
     */
    public int getUnreadCount(Long userId) {
        return messageMapper.selectCount(
            new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, userId)
                .eq(Message::getStatus, 0)
        );
    }
    
    /**
     * 标记已读
     */
    public void markAsRead(Long messageId) {
        Message message = new Message();
        message.setId(messageId);
        message.setStatus(1);
        message.setReadTime(LocalDateTime.now());
        messageMapper.updateById(message);
    }
    
    /**
     * 批量标记已读
     */
    public void markAllAsRead(Long userId) {
        messageMapper.update(null,
            new LambdaUpdateWrapper<Message>()
                .eq(Message::getUserId, userId)
                .eq(Message::getStatus, 0)
                .set(Message::getStatus, 1)
                .set(Message::getReadTime, LocalDateTime.now())
        );
    }
}
```

#### 3. 预约状态变更时发送通知

```java
// 在ReservationService中添加通知逻辑

@Service
@RequiredArgsConstructor
public class ReservationService {
    
    private final MessageService messageService;
    private final EmailService emailService;
    
    /**
     * 审核预约
     */
    public void approve(Long id, String approver, String comment, boolean approved) {
        Reservation reservation = findById(id);
        
        // 更新预约状态
        reservation.setStatus(approved ? 1 : 2);
        reservation.setApprover(approver);
        reservation.setApproveComment(comment);
        reservation.setApproveTime(LocalDateTime.now());
        reservationMapper.updateById(reservation);
        
        // 发送站内消息
        String title = approved ? "预约审核通过" : "预约审核未通过";
        String content = String.format("您的实验室预约（%s，%s）已%s。%s",
            reservation.getLabName(),
            reservation.getReserveDate(),
            approved ? "审核通过" : "审核未通过",
            comment != null ? "审核意见：" + comment : ""
        );
        messageService.sendMessage(
            reservation.getUserId(),
            title,
            content,
            3, // 审核通知
            id,
            "reservation"
        );
        
        // 发送邮件通知（如果用户绑定了邮箱）
        User user = userService.findById(reservation.getUserId());
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            emailService.sendApprovalNotification(
                user.getEmail(),
                user.getRealName(),
                reservation.getLabName(),
                reservation.getReserveDate().toString(),
                approved,
                comment
            );
        }
    }
}
```

### 方案二：小程序订阅消息（推荐）

微信小程序提供订阅消息功能，可以在特定场景向用户推送消息。

#### 实现步骤：

1. **在微信小程序后台配置消息模板**
2. **用户订阅消息**（小程序端调用wx.requestSubscribeMessage）
3. **后端调用微信API发送消息**

```java
@Service
public class WxNotificationService {
    
    @Value("${wx.appid}")
    private String appid;
    
    @Value("${wx.secret}")
    private String secret;
    
    /**
     * 发送订阅消息
     */
    public void sendSubscribeMessage(String openid, String templateId, Map<String, Object> data) {
        try {
            // 1. 获取access_token
            String accessToken = getAccessToken();
            
            // 2. 构建消息内容
            Map<String, Object> message = new HashMap<>();
            message.put("touser", openid);
            message.put("template_id", templateId);
            message.put("data", data);
            
            // 3. 发送消息
            String url = "https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken;
            // 使用HttpClient发送POST请求
            
        } catch (Exception e) {
            log.error("发送订阅消息失败", e);
        }
    }
    
    /**
     * 发送预约审核通知
     */
    public void sendApprovalNotification(String openid, String labName, 
                                        String reserveDate, String result) {
        Map<String, Object> data = new HashMap<>();
        data.put("thing1", Map.of("value", labName));
        data.put("date2", Map.of("value", reserveDate));
        data.put("phrase3", Map.of("value", result));
        
        sendSubscribeMessage(openid, "审核结果模板ID", data);
    }
}
```

### 方案三：WebSocket实时推送

实现浏览器端实时消息推送。

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MessageWebSocketHandler(), "/ws/message")
                .setAllowedOrigins("*");
    }
}

@Component
public class MessageWebSocketHandler extends TextWebSocketHandler {
    
    private static final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserIdFromSession(session);
        sessions.put(userId, session);
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserIdFromSession(session);
        sessions.remove(userId);
    }
    
    /**
     * 推送消息给指定用户
     */
    public void pushMessage(Long userId, String message) {
        WebSocketSession session = sessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("推送消息失败", e);
            }
        }
    }
}
```

### 定时任务：预约提醒

```java
@Component
@EnableScheduling
@RequiredArgsConstructor
public class ReservationReminderTask {
    
    private final ReservationService reservationService;
    private final MessageService messageService;
    private final EmailService emailService;
    
    /**
     * 每小时检查一次，提醒24小时内的预约
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void remind24Hours() {
        LocalDateTime start = LocalDateTime.now().plusHours(23);
        LocalDateTime end = LocalDateTime.now().plusHours(24);
        
        List<Reservation> reservations = reservationService.findByTimeRange(start, end);
        
        for (Reservation reservation : reservations) {
            // 发送站内消息
            messageService.sendMessage(
                reservation.getUserId(),
                "预约提醒",
                "您预约的" + reservation.getLabName() + "将在24小时后开始使用",
                4,
                reservation.getId(),
                "reservation"
            );
            
            // 发送邮件
            User user = userService.findById(reservation.getUserId());
            if (user.getEmail() != null) {
                emailService.sendReservationReminder(
                    user.getEmail(),
                    user.getRealName(),
                    reservation.getLabName(),
                    reservation.getReserveDate().toString(),
                    reservation.getTimeSlot(),
                    24
                );
            }
        }
    }
}
```

---

## 其他推荐功能

### 1. 二维码功能
- **实验室二维码**: 每个实验室生成唯一二维码，扫码快速预约
- **签到二维码**: 扫码签到入场
- **分享二维码**: 分享实验室信息

### 2. 评价系统
- **预约评价**: 使用后评价实验室和设备
- **星级评分**: 5星评分制度
- **评价统计**: 实验室评价统计展示

### 3. 数据导入导出
- **批量导入**: Excel批量导入用户、实验室信息
- **模板下载**: 提供标准导入模板
- **数据导出**: 支持多种格式导出

### 4. 移动端优化
- **扫码功能**: 实验室信息、预约信息扫码查看
- **位置导航**: 实验室位置地图导航
- **语音输入**: 支持语音输入预约信息

### 5. 智能推荐
- **实验室推荐**: 基于使用历史智能推荐
- **时间推荐**: 推荐最佳预约时间
- **设备匹配**: 根据需求匹配合适设备

---

## 实施优先级建议

### 第一阶段（核心功能）⭐⭐⭐⭐⭐
1. ✅ 邮箱验证功能（注册验证、找回密码）
2. ✅ 站内消息系统
3. ✅ 邮件通知（预约状态通知）
4. 预约提醒定时任务

### 第二阶段（增强功能）⭐⭐⭐⭐
1. 小程序订阅消息
2. 二维码签到
3. 预约评价系统
4. 数据统计大屏

### 第三阶段（扩展功能）⭐⭐⭐
1. 短信通知
2. 设备管理
3. 智能推荐
4. 第三方登录

---

## 开发时间估算

| 功能模块 | 开发时间 | 难度 |
|---------|---------|------|
| 邮箱验证 | 2-3天 | 中 |
| 站内消息 | 2-3天 | 中 |
| 邮件通知 | 1-2天 | 低 |
| 小程序推送 | 2-3天 | 中 |
| 短信通知 | 1-2天 | 低 |
| WebSocket | 2-3天 | 高 |
| 定时任务 | 1天 | 低 |
| 二维码功能 | 2天 | 低 |
| 评价系统 | 2-3天 | 中 |

---

## 总结

建议优先实现以下功能：

1. **邮箱验证功能**：提升账号安全性，支持找回密码
2. **站内消息系统**：最基础的通知方式，易于实现
3. **邮件通知**：重要通知的可靠传达方式
4. **预约提醒**：提升用户体验的关键功能

这些功能组合可以构建一个完整的通知体系，满足用户的基本需求。后续可根据实际使用情况和用户反馈，逐步添加其他增强功能。
