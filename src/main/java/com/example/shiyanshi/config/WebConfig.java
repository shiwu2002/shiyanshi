package com.example.shiyanshi.config;

import com.example.shiyanshi.interceptor.JWTInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 配置拦截器和CORS跨域
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptor())
                .addPathPatterns("/api/**")  // 拦截所有API请求
                .excludePathPatterns(
                        "/api/user/login",           // 登录接口不拦截
                        "/api/user/register",        // 注册接口不拦截
                        "/api/user/send-code",       // 发送验证码接口不拦截（公开接口）
                        "/api/user/verify-code",     // 验证验证码接口不拦截（公开接口）
                        "/api/user/send-register-email", // 发送注册邮件接口不拦截（公开接口）
                        "/api/user/verify-email",    // 验证邮箱接口不拦截（公开接口）
                        "/api/user/reset-password-by-email", // 通过邮箱重置密码接口不拦截（公开接口）
                        "/api/laboratory/list",      // 实验室列表查询不拦截（游客可查看）
                        "/api/timeslot/list",        // 时间段列表查询不拦截（游客可查看）
                        "/api/timeslot/enabled"      // 启用时间段查询不拦截（游客可查看）
                );
    }

    /**
     * 配置CORS跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")  // 允许所有域
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 允许的请求方法
                .allowedHeaders("*")  // 允许所有请求头
                .allowCredentials(true)  // 允许携带凭证
                .maxAge(3600);  // 预检请求缓存时间（秒）
    }
}
