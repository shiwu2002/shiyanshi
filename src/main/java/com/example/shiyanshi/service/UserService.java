package com.example.shiyanshi.service;

import com.example.shiyanshi.entity.User;
import com.example.shiyanshi.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.util.List;

/**
 * 用户服务层
 */
@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;


    public void updateStatus(String userId, Integer status) {
        userMapper.update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<User>()
                .set(User::getStatus, status)
                .eq(User::getId, userId));
    }

    /**
     * 用户登录
     */
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }
        String encryptedPassword = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        // 清空密码字段，不返回给前端
        user.setPassword(null);
        return user;
    }
    
    /**
     * 用户注册
     */
    public User register(User user) {
        // 检查用户名是否已存在
        User existUser = userMapper.findByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 加密密码
        String encryptedPassword = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(encryptedPassword);
        // 设置默认值
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        if (user.getUserType() == null) {
            user.setUserType(1); // 默认为学生
        }
        userMapper.insert(user);
        user.setPassword(null);
        return user;
    }
    
    /**
     * 根据ID查询用户
     */
    public User findById(Long id) {
        User user = userMapper.findById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }
    
    /**
     * 查询所有用户
     */
    public List<User> findAll() {
        List<User> users = userMapper.findAll();
        users.forEach(user -> user.setPassword(null));
        return users;
    }
    
    /**
     * 根据用户类型查询
     */
    public List<User> findByUserType(Integer userType) {
        List<User> users = userMapper.findByUserType(userType);
        users.forEach(user -> user.setPassword(null));
        return users;
    }
    
    /**
     * 更新用户信息
     */
    public void update(User user) {
        userMapper.updateById(user);
    }
    
    /**
     * 修改密码
     */
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String encryptedOldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!encryptedOldPassword.equals(user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        String encryptedNewPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        userMapper.updatePassword(id, encryptedNewPassword);
    }
    
    /**
     * 重置密码
     */
    public void resetPassword(Long id, String newPassword) {
        String encryptedPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        userMapper.updatePassword(id, encryptedPassword);
    }
    
    /**
     * 删除用户
     */
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }
    
    /**
     * 搜索用户
     */
    public List<User> search(String keyword, Integer userType) {
        List<User> users = userMapper.search(keyword, userType);
        users.forEach(user -> user.setPassword(null));
        return users;
    }
    
    /**
     * 检查邮箱是否已存在
     */
    public boolean existsByEmail(String email) {
        User user = userMapper.findByEmail(email);
        return user != null;
    }
    
    /**
     * 更新邮箱验证状态
     */
    public void updateEmailVerified(String email, int status) {
        userMapper.updateEmailVerified(email, status);
    }
    
    /**
     * 通过邮箱重置密码
     */
    public void resetPasswordByEmail(String email, String newPassword) {
        User user = userMapper.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("该邮箱未绑定任何账户");
        }
        String encryptedPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());
        userMapper.updatePassword(user.getId(), encryptedPassword);
    }
    
    /**
     * 绑定邮箱到用户
     */
    public void bindEmail(Long userId, String email) {
        // 检查邮箱是否已被其他用户使用
        User existUser = userMapper.findByEmail(email);
        if (existUser != null && !existUser.getId().equals(userId)) {
            throw new RuntimeException("该邮箱已被其他用户绑定");
        }
        // 更新用户邮箱
        userMapper.updateEmailByUserId(userId, email);
        // 设置邮箱为已验证状态
        userMapper.updateEmailVerified(email, 1);
    }
    
    /**
     * 根据邮箱查询用户
     */
    public User findByEmail(String email) {
        User user = userMapper.findByEmail(email);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }
}
