package com.example.shiyanshi.controller;

import com.example.shiyanshi.common.Result;
import com.example.shiyanshi.entity.User;
import com.example.shiyanshi.service.UserService;
import com.example.shiyanshi.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map<String, String> params) {
        try {
            String username = params.get("username");
            String password = params.get("password");
            User user = userService.login(username, password);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        try {
            User newUser = userService.register(user);
            return Result.success("注册成功", newUser);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<User> findById(@PathVariable Long id) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return Result.error("用户不存在");
            }
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 查询所有用户
     */
    @GetMapping("/list")
    public Result<List<User>> findAll() {
        try {
            List<User> users = userService.findAll();
            return Result.success(users);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据用户类型查询
     */
    @GetMapping("/type/{userType}")
    public Result<List<User>> findByUserType(@PathVariable Integer userType) {
        try {
            List<User> users = userService.findByUserType(userType);
            return Result.success(users);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping
    public Result<Void> update(@RequestBody User user) {
        try {
            userService.update(user);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, Object> params) {
        try {
            Long id = Long.valueOf(params.get("id").toString());
            String oldPassword = params.get("oldPassword").toString();
            String newPassword = params.get("newPassword").toString();
            userService.updatePassword(id, oldPassword, newPassword);
            return Result.success("密码修改成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 重置密码（管理员功能）
     */
    @PutMapping("/reset-password")
    public Result<Void> resetPassword(@RequestBody Map<String, Object> params) {
        try {
            Long id = Long.valueOf(params.get("id").toString());
            String newPassword = params.get("newPassword").toString();
            userService.resetPassword(id, newPassword);
            return Result.success("密码重置成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable Long id) {
        try {
            userService.deleteById(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public Result<List<User>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer userType) {
        try {
            List<User> users = userService.search(keyword, userType);
            return Result.success(users);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 发送注册验证邮件
     */
    @PostMapping("/send-register-email")
    public Result<Void> sendRegisterEmail(@RequestBody Map<String, String> params) {
        try {
            String email = params.get("email");
            String username = params.get("username");
            
            if (email == null || email.trim().isEmpty()) {
                return Result.error("邮箱地址不能为空");
            }
            
            // 检查邮箱是否已被注册
            if (userService.existsByEmail(email)) {
                return Result.error("该邮箱已被注册");
            }
            
            emailService.sendRegisterVerifyEmail(email, username);
            return Result.success("验证邮件已发送，请查收", null);
        } catch (Exception e) {
            return Result.error("发送失败：" + e.getMessage());
        }
    }
    
    /**
     * 验证邮箱注册token
     */
    @GetMapping("/verify-email")
    public Result<Void> verifyEmail(@RequestParam String token) {
        try {
            String email = emailService.verifyEmailToken(token);
            if (email == null) {
                return Result.error("验证链接已失效或不存在");
            }
            
            // 更新用户邮箱验证状态
            userService.updateEmailVerified(email, 1);
            return Result.success("邮箱验证成功", null);
        } catch (Exception e) {
            return Result.error("验证失败：" + e.getMessage());
        }
    }
    
    /**
     * 发送验证码邮件
     */
    @PostMapping("/send-code")
    public Result<Void> sendVerificationCode(@RequestBody Map<String, String> params) {
        try {
            String email = params.get("email");
            String purpose = params.get("purpose"); // register, reset-password, bind-email
            
            if (email == null || email.trim().isEmpty()) {
                return Result.error("邮箱地址不能为空");
            }
            
            if (purpose == null || purpose.trim().isEmpty()) {
                purpose = "verify";
            }
            
            // 根据用途检查邮箱状态
            if ("register".equals(purpose) && userService.existsByEmail(email)) {
                return Result.error("该邮箱已被注册");
            }
            
            if ("reset-password".equals(purpose) && !userService.existsByEmail(email)) {
                return Result.error("该邮箱未注册");
            }
            
            emailService.sendVerificationCode(email, purpose);
            return Result.success("验证码已发送，请查收邮件", null);
        } catch (Exception e) {
            return Result.error("发送失败：" + e.getMessage());
        }
    }
    
    /**
     * 验证验证码
     */
    @PostMapping("/verify-code")
    public Result<Void> verifyCode(@RequestBody Map<String, String> params) {
        try {
            String email = params.get("email");
            String code = params.get("code");
            
            if (email == null || email.trim().isEmpty()) {
                return Result.error("邮箱地址不能为空");
            }
            
            if (code == null || code.trim().isEmpty()) {
                return Result.error("验证码不能为空");
            }
            
            boolean result = emailService.verifyCode(email, code);
            if (result) {
                return Result.success("验证成功", null);
            } else {
                return Result.error("验证码错误或已失效");
            }
        } catch (Exception e) {
            return Result.error("验证失败：" + e.getMessage());
        }
    }
    
    /**
     * 通过邮箱验证码重置密码
     */
    @PostMapping("/reset-password-by-email")
    public Result<Void> resetPasswordByEmail(@RequestBody Map<String, String> params) {
        try {
            String email = params.get("email");
            String code = params.get("code");
            String newPassword = params.get("newPassword");
            
            if (email == null || email.trim().isEmpty()) {
                return Result.error("邮箱地址不能为空");
            }
            
            if (code == null || code.trim().isEmpty()) {
                return Result.error("验证码不能为空");
            }
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return Result.error("新密码不能为空");
            }
            
            // 验证验证码
            boolean verified = emailService.verifyCode(email, code);
            if (!verified) {
                return Result.error("验证码错误或已失效");
            }
            
            // 重置密码
            userService.resetPasswordByEmail(email, newPassword);
            return Result.success("密码重置成功", null);
        } catch (Exception e) {
            return Result.error("重置失败：" + e.getMessage());
        }
    }
    
    /**
     * 绑定邮箱
     */
    @PostMapping("/bind-email")
    public Result<Void> bindEmail(@RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            String email = params.get("email").toString();
            String code = params.get("code").toString();
            
            if (email == null || email.trim().isEmpty()) {
                return Result.error("邮箱地址不能为空");
            }
            
            if (code == null || code.trim().isEmpty()) {
                return Result.error("验证码不能为空");
            }
            
            // 检查邮箱是否已被其他用户使用
            if (userService.existsByEmail(email)) {
                return Result.error("该邮箱已被其他用户使用");
            }
            
            // 验证验证码
            boolean verified = emailService.verifyCode(email, code);
            if (!verified) {
                return Result.error("验证码错误或已失效");
            }
            
            // 绑定邮箱
            userService.bindEmail(userId, email);
            return Result.success("邮箱绑定成功", null);
        } catch (Exception e) {
            return Result.error("绑定失败：" + e.getMessage());
        }
    }
    
    /**
     * 重新发送邮箱验证邮件
     */
    @PostMapping("/resend-verify-email")
    public Result<Void> resendVerifyEmail(@RequestBody Map<String, Object> params) {
        try {
            Long userId = Long.valueOf(params.get("userId").toString());
            User user = userService.findById(userId);
            
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                return Result.error("用户未设置邮箱");
            }
            
            if (user.getEmailVerified() != null && user.getEmailVerified() == 1) {
                return Result.error("邮箱已验证，无需重复验证");
            }
            
            emailService.sendRegisterVerifyEmail(user.getEmail(), user.getUsername());
            return Result.success("验证邮件已重新发送", null);
        } catch (Exception e) {
            return Result.error("发送失败：" + e.getMessage());
        }
    }
}
