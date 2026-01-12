package com.example.shiyanshi.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Arrays;

/**
 * JWT工具类
 * - 从配置文件读取密钥与过期时间
 * - 提供生成、解析、校验、刷新Token的能力
 *
 * 配置项：
 * - jwt.secret：签名密钥（字符串）
 * - jwt.expiration：过期时间（毫秒）
 */
@Component
public class JWTUtil {

    /**
     * 从配置文件读取的原始密钥字符串
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * 从配置文件读取的过期时间（毫秒），默认24小时
     */
    @Value("${jwt.expiration:86400000}")
    private long expirationMillis;

    /**
     * 供静态方法使用的签名密钥与过期时长
     */
    private static SecretKey SIGNING_KEY;
    private static long EXPIRATION_TIME;

    /**
     * 初始化静态密钥与过期时间
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes;
        String raw = secret != null ? secret.trim() : "";
        try {
            // 支持 base64: 前缀的密钥（例如：jwt.secret=base64:xxx），否则按UTF-8字节
            if (raw.startsWith("base64:")) {
                String b64 = raw.substring("base64:".length());
                keyBytes = Base64.getDecoder().decode(b64);
            } else {
                keyBytes = raw.getBytes(StandardCharsets.UTF_8);
            }
            // 若长度不足32字节（256bit），使用SHA-256对原始密钥派生得到固定长度的安全密钥
            if (keyBytes.length < 32) {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                keyBytes = md.digest(keyBytes);
            }
            SIGNING_KEY = Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            throw new IllegalStateException("JWT密钥初始化失败，请检查配置 jwt.secret", e);
        }
        EXPIRATION_TIME = expirationMillis;
    }

    /**
     * 生成Token
     */
    public static String generateToken(Long userId, String username, Integer userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("userType", userType);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从Token中获取Claims
     */
    public static Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SIGNING_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 从Token中获取用户ID
     */
    public static Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            Object userId = claims.get("userId");
            if (userId instanceof Integer) {
                return ((Integer) userId).longValue();
            } else if (userId instanceof Long) {
                return (Long) userId;
            } else if (userId instanceof Number) {
                return ((Number) userId).longValue();
            }
        }
        return null;
    }

    /**
     * 从Token中获取用户名
     */
    public static String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.get("username", String.class) : null;
    }

    /**
     * 从Token中获取用户类型
     */
    public static Integer getUserTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims != null ? claims.get("userType", Integer.class) : null;
    }

    /**
     * 验证Token是否有效
     */
    public static boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims == null) {
                return false;
            }
            // 检查是否过期
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查Token是否即将过期（1小时内）
     */
    public static boolean isTokenExpiringSoon(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims == null) {
                return true;
            }
            Date expiration = claims.getExpiration();
            long timeLeft = expiration.getTime() - System.currentTimeMillis();
            // 如果剩余时间小于1小时，返回true
            return timeLeft < 60 * 60 * 1000;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 刷新Token
     */
    public static String refreshToken(String token) {
        Claims claims = getClaimsFromToken(token);
        if (claims != null) {
            Long userId = getUserIdFromToken(token);
            String username = getUsernameFromToken(token);
            Integer userType = getUserTypeFromToken(token);
            return generateToken(userId, username, userType);
        }
        return null;
    }
}
