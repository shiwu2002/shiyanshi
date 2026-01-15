package com.example.shiyanshi.controller;

import com.example.shiyanshi.annotation.RequirePermission;
import com.example.shiyanshi.annotation.RequireSelfOrAdmin;
import com.example.shiyanshi.common.Result;
import com.example.shiyanshi.entity.User;
import com.example.shiyanshi.entity.UserWechatAuth;
import com.example.shiyanshi.service.UserService;
import com.example.shiyanshi.service.EmailService;
import com.example.shiyanshi.service.UserWechatAuthService;
import com.example.shiyanshi.util.JWTUtil;
import com.example.shiyanshi.service.ReservationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
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

    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private UserWechatAuthService userWechatAuthService;
    
    private static final String PLATFORM_MINI_PROGRAM = "mini_program";
    
    /**
     * 用户登录（支持微信绑定）
     * 
     * 请求示例：
     * POST /api/user/login
     * {
     *   "username": "用户名",
     *   "password": "密码",
     *   "openid": "微信openid（可选，用于自动绑定）",
     *   "unionid": "微信unionid（可选）",
     *   "sessionKey": "微信sessionKey（可选）"
     * }
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        try {
            String username = params.get("username");
            String password = params.get("password");
            String openid = params.get("openid");
            String unionid = params.get("unionid");
            String sessionKey = params.get("sessionKey");
            
            // 用户名密码登录
            User user = userService.login(username, password);
            
            // 生成JWT token
            String token = JWTUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());
            
            // 返回token和关键用户信息
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userId", user.getId());
            result.put("username", user.getUsername());
            result.put("userType", user.getUserType());
            result.put("realName", user.getRealName());
            
            // 如果提供了openid，自动绑定微信
            if (openid != null && !openid.trim().isEmpty()) {
                try {
                    // 检查该openid是否已被其他用户绑定
                    UserWechatAuth existingAuth = userWechatAuthService.findByPlatformAndOpenid(PLATFORM_MINI_PROGRAM, openid);
                    
                    if (existingAuth != null && existingAuth.getBindStatus() == 1 && 
                        existingAuth.getUserId() != null && !existingAuth.getUserId().equals(user.getId())) {
                        // openid已被其他用户绑定
                        result.put("bindWarning", "该微信已绑定其他账号");
                    } else {
                        // 执行绑定
                        UserWechatAuth bindResult = userWechatAuthService.bind(
                            user.getId(), 
                            PLATFORM_MINI_PROGRAM, 
                            openid, 
                            unionid, 
                            sessionKey
                        );
                        result.put("wechatBound", true);
                        result.put("openid", bindResult.getOpenid());
                        result.put("unionid", bindResult.getUnionid());
                    }
                } catch (Exception e) {
                    // 绑定失败不影响登录，仅记录警告
                    result.put("bindWarning", "微信绑定失败：" + e.getMessage());
                }
            }
            
            return Result.success("登录成功", result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户注册（支持微信绑定）
     * 
     * 请求示例：
     * POST /api/user/register
     * {
     *   "username": "用户名",
     *   "password": "密码",
     *   "email": "邮箱（可选）",
     *   "realName": "真实姓名（可选）",
     *   "openid": "微信openid（可选，用于自动绑定）",
     *   "unionid": "微信unionid（可选）",
     *   "sessionKey": "微信sessionKey（可选）"
     * }
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody Map<String, Object> params) {
        try {
            // 构建User对象
            User user = new User();
            user.setUsername(params.get("username") != null ? params.get("username").toString() : null);
            user.setPassword(params.get("password") != null ? params.get("password").toString() : null);
            user.setEmail(params.get("email") != null ? params.get("email").toString() : null);
            user.setRealName(params.get("realName") != null ? params.get("realName").toString() : null);
            
            // 注册用户
            User newUser = userService.register(user);
            
            // 生成JWT token
            String token = JWTUtil.generateToken(newUser.getId(), newUser.getUsername(), newUser.getUserType());
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            result.put("userId", newUser.getId());
            result.put("username", newUser.getUsername());
            result.put("userType", newUser.getUserType());
            result.put("realName", newUser.getRealName());
            result.put("email", newUser.getEmail());
            
            // 如果提供了openid，自动绑定微信
            String openid = params.get("openid") != null ? params.get("openid").toString() : null;
            String unionid = params.get("unionid") != null ? params.get("unionid").toString() : null;
            String sessionKey = params.get("sessionKey") != null ? params.get("sessionKey").toString() : null;
            
            if (openid != null && !openid.trim().isEmpty()) {
                try {
                    // 检查该openid是否已被其他用户绑定
                    UserWechatAuth existingAuth = userWechatAuthService.findByPlatformAndOpenid(PLATFORM_MINI_PROGRAM, openid);
                    
                    if (existingAuth != null && existingAuth.getBindStatus() == 1 && 
                        existingAuth.getUserId() != null && !existingAuth.getUserId().equals(newUser.getId())) {
                        // openid已被其他用户绑定
                        result.put("bindWarning", "该微信已绑定其他账号");
                    } else {
                        // 执行绑定
                        UserWechatAuth bindResult = userWechatAuthService.bind(
                            newUser.getId(), 
                            PLATFORM_MINI_PROGRAM, 
                            openid, 
                            unionid, 
                            sessionKey
                        );
                        result.put("wechatBound", true);
                        result.put("openid", bindResult.getOpenid());
                        result.put("unionid", bindResult.getUnionid());
                    }
                } catch (Exception e) {
                    // 绑定失败不影响注册，仅记录警告
                    result.put("bindWarning", "微信绑定失败：" + e.getMessage());
                }
            }
            
            return Result.success("注册成功", result);
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
     * 更新用户信息（自己或超级管理员）
     * 注意：用户权限(userType)字段只能由超级管理员修改
     */
    @RequireSelfOrAdmin(idParam = "id", adminLevel = 3, description = "更新用户信息需要自己操作或超级管理员权限")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody User user, HttpServletRequest request) {
        try {
            // 确保更新的是指定ID的用户
            user.setId(id);
            
            // 获取当前用户信息
            Object currentUserTypeObj = request.getAttribute("userType");
            Integer currentUserType = null;
            if (currentUserTypeObj != null) {
                currentUserType = (currentUserTypeObj instanceof Integer) ? (Integer) currentUserTypeObj 
                    : Integer.valueOf(currentUserTypeObj.toString());
            }
            
            // 权限字段安全检查：只有超级管理员可以修改用户权限(userType)
            if (user.getUserType() != null) {
                // 当前用户不是超级管理员，但尝试修改用户权限
                if (currentUserType == null || currentUserType != 3) {
                    // 查找现有用户的权限信息
                    User existingUser = userService.findById(id);
                    if (existingUser != null && !existingUser.getUserType().equals(user.getUserType())) {
                        return Result.error("权限不足：只有超级管理员可以修改用户权限");
                    }
                }
            }
            
            userService.update(user);
            return Result.success("更新成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新用户状态（仅超级管理员）
     */
    @RequirePermission(value = 3, description = "更新用户状态需要超级管理员权限")
    @PutMapping("/status")
    public Result<Void> updateStatus(@RequestBody Map<String, Object> params) {
        try {
            String userId = params.get("userId").toString();
            Integer status = Integer.valueOf(params.get("status").toString());
            userService.updateStatus(userId, status);
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
     * 重置密码（仅超级管理员）
     */
    @RequirePermission(value = 3, description = "重置密码需要超级管理员权限")
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
     * 删除用户（仅超级管理员）
     */
    @RequirePermission(value = 3, description = "删除用户需要超级管理员权限")
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

    /**
     * 用户统计数据（从JWT拦截器注入的用户ID或请求参数获取）
     * 前端调用 /api/user/statistics 时无需传 userId，优先从 request.attribute("userId") 读取
     */
    @GetMapping("/statistics")
    public Result<Map<String, Integer>> getUserStatistics(HttpServletRequest request,
                                              @RequestParam(value = "userId", required = false) Long userId) {
        try {
            Object uidAttr = request.getAttribute("userId");
            Long uid = userId;
            if (uid == null && uidAttr != null) {
                uid = (uidAttr instanceof Long) ? (Long) uidAttr : Long.valueOf(uidAttr.toString());
            }
            if (uid == null) {
                return Result.error("缺少用户身份信息，请登录后重试");
            }
            Map<String, Integer> stats = reservationService.getUserReservationStats(uid);
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
