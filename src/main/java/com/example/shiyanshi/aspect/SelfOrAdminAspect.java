package com.example.shiyanshi.aspect;

import com.example.shiyanshi.annotation.RequireSelfOrAdmin;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 用户自己操作或管理员操作权限验证切面
 * 通过AOP拦截带有@RequireSelfOrAdmin注解的方法，验证用户权限
 */
@Aspect
@Component
public class SelfOrAdminAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(SelfOrAdminAspect.class);
    
    /**
     * 环绕通知，拦截所有带@RequireSelfOrAdmin注解的方法
     */
    @Around("@annotation(com.example.shiyanshi.annotation.RequireSelfOrAdmin)")
    public Object checkSelfOrAdminPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取@RequireSelfOrAdmin注解
        RequireSelfOrAdmin requireSelfOrAdmin = method.getAnnotation(RequireSelfOrAdmin.class);
        if (requireSelfOrAdmin == null) {
            // 如果没有注解，直接放行
            return joinPoint.proceed();
        }
        
        // 获取注解参数
        String idParam = requireSelfOrAdmin.idParam();
        int adminLevel = requireSelfOrAdmin.adminLevel();
        String description = requireSelfOrAdmin.description();
        
        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            logger.error("无法获取请求上下文");
            throw new SecurityException("权限验证失败：无法获取请求上下文");
        }
        
        HttpServletRequest request = attributes.getRequest();
        logger.info("SelfOrAdmin权限验证开始：{} {}, ID参数: {}", 
                    request.getMethod(), request.getRequestURI(), idParam);
        
        // 从request中获取当前用户信息（由JWT拦截器存入）
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
        
        // 获取当前用户信息
        Integer currentUserType = (userTypeObj instanceof Integer) ? (Integer) userTypeObj : Integer.valueOf(userTypeObj.toString());
        Long currentUserId = userIdObj != null ? 
            (userIdObj instanceof Long ? (Long) userIdObj : Long.valueOf(userIdObj.toString())) : null;
        String currentUsername = usernameObj != null ? usernameObj.toString() : "unknown";
        
        // 获取方法参数中的目标用户ID
        Long targetUserId = extractTargetUserId(joinPoint, signature, method, idParam);
        
        if (targetUserId == null) {
            String errorMsg = String.format(
                "参数解析失败：无法从参数[%s]中获取用户ID",
                idParam
            );
            logger.error(errorMsg);
            throw new SecurityException("权限验证失败：参数错误");
        }
        
        logger.debug("权限验证 - 当前用户: {}(ID:{}, Type:{}), 目标用户ID: {}", 
                     currentUsername, currentUserId, currentUserType, targetUserId);
        
        // 权限验证逻辑：
        // 1. 如果是超级管理员（adminLevel或以上），允许操作
        // 2. 如果当前用户要操作的是自己，允许操作
        // 3. 其他情况拒绝
        
        boolean hasPermission = false;
        String permissionType = "";
        
        if (currentUserType != null && currentUserType >= adminLevel) {
            // 超级管理员权限
            hasPermission = true;
            permissionType = "管理员权限";
        } else if (currentUserId != null && currentUserId.equals(targetUserId)) {
            // 用户操作自己
            hasPermission = true;
            permissionType = "自我操作权限";
        }
        
        if (!hasPermission) {
            String errorMsg = String.format(
                "权限不足：用户[%s(ID:%s,权限级别:%d)]无法操作用户[ID:%s]，需要管理员权限(%d级)或为自己操作",
                currentUsername, currentUserId, currentUserType, targetUserId, adminLevel
            );
            logger.warn(errorMsg);
            throw new SecurityException("权限不足：您只能操作自己的信息或需要管理员权限");
        }
        
        // 权限验证通过，记录日志
        if (!description.isEmpty()) {
            logger.info("SelfOrAdmin权限验证通过：用户[{}(ID:{})]执行操作[{}]，权限类型：{}", 
                currentUsername, currentUserId, description, permissionType);
        }
        
        // 执行目标方法
        return joinPoint.proceed();
    }
    
    /**
     * 从方法参数中提取目标用户ID
     */
    private Long extractTargetUserId(ProceedingJoinPoint joinPoint, MethodSignature signature, Method method, String idParam) {
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();
        
        // 处理简单的参数名（如: "id", "userId"）
        if (!idParam.contains(".")) {
            for (int i = 0; i < parameters.length; i++) {
                String paramName = parameters[i].getName();
                if (idParam.equals(paramName)) {
                    Object arg = args[i];
                    if (arg instanceof Long) {
                        return (Long) arg;
                    } else if (arg instanceof Number) {
                        return ((Number) arg).longValue();
                    } else if (arg != null) {
                        try {
                            return Long.valueOf(arg.toString());
                        } catch (NumberFormatException e) {
                            logger.warn("参数[{}]的值[{}]无法转换为Long类型", paramName, arg);
                            return null;
                        }
                    }
                }
            }
        }
        
        // 处理嵌套参数（如: "user.id"）
        if (idParam.contains(".")) {
            String[] parts = idParam.split("\\.");
            String objectParamName = parts[0];
            String fieldName = parts[1];
            
            for (int i = 0; i < parameters.length; i++) {
                String paramName = parameters[i].getName();
                if (objectParamName.equals(paramName)) {
                    Object arg = args[i];
                    if (arg != null) {
                        try {
                            // 使用反射获取字段值
                            java.lang.reflect.Field field = arg.getClass().getDeclaredField(fieldName);
                            field.setAccessible(true);
                            Object fieldValue = field.get(arg);
                            
                            if (fieldValue instanceof Long) {
                                return (Long) fieldValue;
                            } else if (fieldValue instanceof Number) {
                                return ((Number) fieldValue).longValue();
                            } else if (fieldValue != null) {
                                return Long.valueOf(fieldValue.toString());
                            }
                        } catch (Exception e) {
                            logger.warn("无法从参数[{}]的字段[{}]中获取ID值", objectParamName, fieldName, e);
                        }
                    }
                }
            }
        }
        
        // 如果以上方法都失败，尝试从PathVariable中获取
        // 这里可以扩展支持其他参数类型
        
        logger.warn("无法从参数[{}]中提取用户ID，参数列表: {}", idParam, parameters);
        return null;
    }
}
