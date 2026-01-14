package com.example.shiyanshi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限验证注解 - 允许用户自己操作或超级管理员操作
 * 用于标记需要用户自己操作或超级管理员权限才能访问的方法
 * 
 * 使用场景：
 * 1. 用户修改自己的信息
 * 2. 用户查看自己的数据
 * 3. 其他需要用户自己或管理员操作的场景
 * 
 * 参数说明：
 * - idParam: 方法参数中用户ID参数的名称，默认为"id"
 * - adminLevel: 管理员权限级别，默认为3（超级管理员）
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireSelfOrAdmin {
    /**
     * 方法参数中用户ID参数的名称
     * 例如：如果方法签名是 update(@PathVariable Long userId, ...)，则idParam应为"userId"
     * 如果方法参数是对象中包含ID，如 update(@RequestBody User user)，则idParam应为"user.id"
     * 默认为"id"
     */
    String idParam() default "id";
    
    /**
     * 管理员权限级别
     * 默认为3（超级管理员）
     */
    int adminLevel() default 3;
    
    /**
     * 权限描述（用于日志记录）
     */
    String description() default "";
}
