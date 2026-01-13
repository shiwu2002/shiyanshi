package com.example.shiyanshi.aspect;

import com.example.shiyanshi.annotation.RequirePermission;
import com.example.shiyanshi.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 权限验证切面
 * 通过AOP拦截带有@RequirePermission注解的方法，验证用户权限
 */
@Aspect
@Component
public class PermissionAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(PermissionAspect.class);
    
    /**
     * 环绕通知，拦截所有带@RequirePermission注解的方法
     */
    @Around("@annotation(com.example.shiyanshi.annotation.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取@RequirePermission注解
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
        if (requirePermission == null) {
            // 如果没有注解，直接放行
            return joinPoint.proceed();
        }
        
        // 获取需要的最低权限级别
        int requiredLevel = requirePermission.value();
        String description = requirePermission.description();
        
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            logger.error("无法获取请求上下文");
            throw new SecurityException("权限验证失败：无法获取请求上下文");
        }
        
        HttpServletRequest request = attributes.getRequest();
        logger.info("权限验证开始：{} {}, 需要权限级别: {}", 
                    request.getMethod(), request.getRequestURI(), requiredLevel);
        
        // 从request中获取用户权限（由JWT拦截器存入）
        Object userTypeObj = request.getAttribute("userType");
        Object userIdObj = request.getAttribute("userId");
        Object usernameObj = request.getAttribute("username");
        
        logger.debug("Request attributes - userType: {}, userId: {}, username: {}", 
                     userTypeObj, userIdObj, usernameObj);
        
        if (userTypeObj == null) {
            logger.warn("用户未登录或token无效 - request: {} {}, userType attribute is null", 
                       request.getMethod(), request.getRequestURI());
            throw new SecurityException("权限验证失败：请先登录");
        }
        
        // 获取用户权限级别和用户信息
        Integer userType = (userTypeObj instanceof Integer) ? (Integer) userTypeObj : Integer.valueOf(userTypeObj.toString());
        Long userId = userIdObj != null ? 
            (userIdObj instanceof Long ? (Long) userIdObj : Long.valueOf(userIdObj.toString())) : null;
        String username = usernameObj != null ? usernameObj.toString() : "unknown";
        
        // 验证权限
        if (userType < requiredLevel) {
            String errorMsg = String.format(
                "权限不足：用户[%s(ID:%s)]的权限级别为%d，需要%d级或以上权限才能执行操作[%s]",
                username, userId, userType, requiredLevel, 
                description.isEmpty() ? method.getName() : description
            );
            logger.warn(errorMsg);
            throw new SecurityException("权限不足：您没有权限执行此操作");
        }
        
        // 权限验证通过，记录日志
        if (!description.isEmpty()) {
            logger.info("权限验证通过：用户[{}(ID:{})]执行操作[{}]，权限级别：{}", 
                username, userId, description, userType);
        }
        
        // 执行目标方法
        return joinPoint.proceed();
    }
}
