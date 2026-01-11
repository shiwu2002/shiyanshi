package com.example.shiyanshi.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类
 */
public class MD5Util {

    /**
     * 对字符串进行MD5加密
     */
    public static String encrypt(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] byteDigest = md.digest();
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : byteDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证密码
     */
    public static boolean verify(String rawPassword, String encryptedPassword) {
        if (rawPassword == null || encryptedPassword == null) {
            return false;
        }
        
        String encrypted = encrypt(rawPassword);
        return encryptedPassword.equals(encrypted);
    }

    /**
     * 带盐值的加密
     */
    public static String encryptWithSalt(String str, String salt) {
        if (str == null || salt == null) {
            return null;
        }
        
        return encrypt(str + salt);
    }

    /**
     * 验证带盐值的密码
     */
    public static boolean verifyWithSalt(String rawPassword, String salt, String encryptedPassword) {
        if (rawPassword == null || salt == null || encryptedPassword == null) {
            return false;
        }
        
        String encrypted = encryptWithSalt(rawPassword, salt);
        return encryptedPassword.equals(encrypted);
    }
}
