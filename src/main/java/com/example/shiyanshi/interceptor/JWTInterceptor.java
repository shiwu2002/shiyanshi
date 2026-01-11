package com.example.shiyanshi.interceptor;

import com.alibaba.fastjson2.JSON;
import com.example.shiyanshi.common.Result;
import com.example.shiyanshi.util.JWTUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * JWT拦截器
 * 用于验证请求中的Token是否有效
 */
public class JWTInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 处理跨域预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从请求头中获取Token
        String token = request.getHeader("Authorization");
        
        // 也支持从token参数获取
        if (token == null || token.isEmpty()) {
            token = request.getParameter("token");
        }
        
        // 如果Token以"Bearer "开头，去掉前缀
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证Token
        if (token == null || token.isEmpty() || !JWTUtil.validateToken(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            
            Result<?> result = Result.error(401, "未授权，请先登录");
            PrintWriter writer = response.getWriter();
            writer.write(JSON.toJSONString(result));
            writer.flush();
            writer.close();
            
            return false;
        }

        // 将用户信息存入request，供后续使用
        Long userId = JWTUtil.getUserIdFromToken(token);
        String username = JWTUtil.getUsernameFromToken(token);
        Integer userType = JWTUtil.getUserTypeFromToken(token);
        
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);
        request.setAttribute("userType", userType);
        request.setAttribute("token", token);

        return true;
    }
}
