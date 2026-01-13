package com.example.shiyanshi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限验证注解
 * 用于标记需要特定权限才能访问的方法
 * 
 * 权限级别说明：
 * 0 - 学生
 * 1 - 教师
 * 2 - 管理员
 * 3 - 超级管理员
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    /**
     * 需要的最低权限级别
     * 默认为0（所有用户都可访问）
     */
    int value() default 0;
    
    /**
     * 权限描述（用于日志记录）
     */
    String description() default "";
}
