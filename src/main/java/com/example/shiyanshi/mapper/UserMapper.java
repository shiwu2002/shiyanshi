package com.example.shiyanshi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.shiyanshi.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户数据访问层
 * 继承BaseMapper后，自动拥有基础的CRUD方法
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据用户名查询用户
     * MyBatis-Plus提供的selectOne方法可替代此方法
     * 保留此方法以保持Service层接口不变
     */
    default User findByUsername(String username) {
        return selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
    }
    
    /**
     * 根据ID查询用户
     * 可使用继承的selectById(id)方法
     */
    default User findById(Long id) {
        return selectById(id);
    }
    
    /**
     * 查询所有用户
     * 可使用继承的selectList()方法
     */
    default java.util.List<User> findAll() {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .orderByDesc(User::getCreateTime));
    }
    
    /**
     * 根据用户类型查询
     */
    default java.util.List<User> findByUserType(Integer userType) {
        return selectList(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUserType, userType)
                .orderByDesc(User::getCreateTime));
    }
    
    /**
     * 更新密码
     */
    default int updatePassword(@Param("id") Long id, @Param("password") String password) {
        User user = new User();
        user.setId(id);
        user.setPassword(password);
        return updateById(user);
    }
    
    /**
     * 搜索用户
     */
    default java.util.List<User> search(@Param("keyword") String keyword, @Param("userType") Integer userType) {
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User> wrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w
                    .like(User::getUsername, keyword)
                    .or().like(User::getRealName, keyword)
                    .or().like(User::getPhone, keyword));
        }
        
        if (userType != null) {
            wrapper.eq(User::getUserType, userType);
        }
        
        wrapper.orderByDesc(User::getCreateTime);
        
        return selectList(wrapper);
    }
    
    /**
     * 根据邮箱查询用户
     */
    default User findByEmail(String email) {
        return selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
    }
    
    /**
     * 更新邮箱验证状态
     */
    default int updateEmailVerified(@Param("email") String email, @Param("status") int status) {
        return update(null, new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<User>()
                .set(User::getEmailVerified, status)
                .eq(User::getEmail, email));
    }
    
    /**
     * 根据用户ID更新邮箱
     */
    default int updateEmailByUserId(@Param("userId") Long userId, @Param("email") String email) {
        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setEmailVerified(0); // 新绑定的邮箱默认未验证
        return updateById(user);
    }
}
