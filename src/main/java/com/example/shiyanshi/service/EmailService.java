package com.example.shiyanshi.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 邮件服务类
 */
@Slf4j
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${system.email.from}")
    private String from;

    @Value("${system.email.from-name}")
    private String fromName;

    @Value("${system.base-url}")
    private String baseUrl;

    @Value("${verification.code.expire-minutes}")
    private int codeExpireMinutes;

    @Value("${verification.code.length}")
    private int codeLength;

    /**
     * 发送注册验证邮件
     */
    public void sendRegisterVerifyEmail(String email, String username) throws UnsupportedEncodingException {
        // 生成验证token
        String token = UUID.randomUUID().toString();
        
        // 存储token到Redis，30分钟有效
        redisTemplate.opsForValue().set(
            "email:register:token:" + token,
            email,
            30,
            TimeUnit.MINUTES
        );
        
        // 构建验证链接
        String verifyUrl = baseUrl + "/api/user/verify-email?token=" + token;
        
        // 准备邮件模板参数
        Context context = new Context();
        context.setVariable("username", username != null ? username : "用户");
        context.setVariable("verifyUrl", verifyUrl);
        
        // 渲染并发送邮件
        String content = templateEngine.process("email-register", context);
        sendHtmlEmail(email, "邮箱验证 - " + fromName, content);
        
        log.info("注册验证邮件已发送至：{}", email);
    }

    /**
     * 发送验证码邮件
     */
    public void sendVerificationCode(String email, String purpose) {
        try {
            // 生成验证码
            String code = generateCode();
            
            // 存储到Redis
            String key = "email:code:" + email + ":" + purpose;
            redisTemplate.opsForValue().set(key, code, codeExpireMinutes, TimeUnit.MINUTES);

            // 准备邮件模板参数
            Context context = new Context();
            context.setVariable("code", code);
            context.setVariable("purpose", getPurposeText(purpose));
            context.setVariable("expireMinutes", codeExpireMinutes);

            // 渲染并发送邮件
            String content = templateEngine.process("email-code", context);
            sendHtmlEmail(email, "验证码 - " + fromName, content);
            
            log.info("验证码邮件已发送至：{}, 用途：{}", email, purpose);
        } catch (Exception e) {
            log.error("发送验证码邮件失败：{}", e.getMessage(), e);
            throw new RuntimeException("发送验证码失败");
        }
    }

    /**
     * 验证验证码
     */
    public boolean verifyCode(String email, String code) {
        // 尝试多个用途的验证码
        String[] purposes = {"register", "reset-password", "bind-email", "verify"};
        
        for (String purpose : purposes) {
            String key = "email:code:" + email + ":" + purpose;
            String savedCode = redisTemplate.opsForValue().get(key);
            
            if (savedCode != null && savedCode.equals(code)) {
                // 验证成功后删除验证码
                redisTemplate.delete(key);
                return true;
            }
        }
        return false;
    }

    /**
     * 验证邮箱验证token
     */
    public String verifyEmailToken(String token) {
        String key = "email:register:token:" + token;
        String email = redisTemplate.opsForValue().get(key);
        
        if (email != null) {
            // 验证成功后删除token
            redisTemplate.delete(key);
            return email;
        }
        return null;
    }

    /**
     * 发送预约通知邮件
     */
    public void sendReservationNotification(String email, String username, Map<String, Object> reservationInfo) {
        try {
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("labName", reservationInfo.get("labName"));
            context.setVariable("reservationDate", reservationInfo.get("reservationDate"));
            context.setVariable("timeSlot", reservationInfo.get("timeSlot"));
            context.setVariable("status", reservationInfo.get("status"));
            context.setVariable("baseUrl", baseUrl);

            String content = templateEngine.process("email-reservation", context);
            String status = (String) reservationInfo.get("status");
            sendHtmlEmail(email, "预约通知 - " + getStatusText(status), content);
            
            log.info("预约通知邮件已发送至：{}", email);
        } catch (Exception e) {
            log.error("发送预约通知邮件失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 发送审核结果通知邮件
     */
    public void sendApprovalNotification(String email, String username, Map<String, Object> approvalInfo) {
        try {
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("labName", approvalInfo.get("labName"));
            context.setVariable("reservationDate", approvalInfo.get("reservationDate"));
            context.setVariable("timeSlot", approvalInfo.get("timeSlot"));
            context.setVariable("approved", approvalInfo.get("approved"));
            context.setVariable("reason", approvalInfo.get("reason"));
            context.setVariable("baseUrl", baseUrl);

            String content = templateEngine.process("email-approval", context);
            boolean approved = (Boolean) approvalInfo.get("approved");
            String subject = approved ? "预约审核通过" : "预约审核未通过";
            sendHtmlEmail(email, subject, content);
            
            log.info("审核结果通知邮件已发送至：{}", email);
        } catch (Exception e) {
            log.error("发送审核结果通知邮件失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 发送预约提醒邮件
     */
    public void sendReservationReminder(String email, String username, Map<String, Object> reminderInfo) {
        try {
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("labName", reminderInfo.get("labName"));
            context.setVariable("reservationDate", reminderInfo.get("reservationDate"));
            context.setVariable("timeSlot", reminderInfo.get("timeSlot"));
            context.setVariable("baseUrl", baseUrl);

            String content = templateEngine.process("email-reminder", context);
            sendHtmlEmail(email, "预约提醒 - 实验室预约即将开始", content);
            
            log.info("预约提醒邮件已发送至：{}", email);
        } catch (Exception e) {
            log.error("发送预约提醒邮件失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 发送HTML邮件
     */
    private void sendHtmlEmail(String to, String subject, String content) throws UnsupportedEncodingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(from, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("发送邮件失败：{}", e.getMessage(), e);
            throw new RuntimeException("发送邮件失败");
        }
    }

    /**
     * 生成验证码
     */
    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < codeLength; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 获取用途文本
     */
    private String getPurposeText(String purpose) {
        switch (purpose) {
            case "register":
                return "注册验证";
            case "reset-password":
                return "重置密码";
            case "bind-email":
                return "绑定邮箱";
            default:
                return "身份验证";
        }
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(String status) {
        switch (status) {
            case "pending":
                return "待审核";
            case "approved":
                return "已通过";
            case "rejected":
                return "已拒绝";
            case "cancelled":
                return "已取消";
            case "completed":
                return "已完成";
            default:
                return "状态变更";
        }
    }
}
