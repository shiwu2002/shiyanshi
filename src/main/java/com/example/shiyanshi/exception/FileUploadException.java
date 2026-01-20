package com.example.shiyanshi.exception;

/**
 * 文件上传异常类
 * 
 * 继承自RuntimeException，用于处理文件上传过程中的各种异常情况
 * 包括文件验证失败、存储失败、类型不支持等
 */
public class FileUploadException extends RuntimeException {
    
    /**
     * 错误代码
     */
    private final String errorCode;
    
    /**
     * 构造方法 - 仅包含错误消息
     * 
     * @param message 错误消息
     */
    public FileUploadException(String message) {
        super(message);
        this.errorCode = "FILE_UPLOAD_ERROR";
    }
    
    /**
     * 构造方法 - 包含错误消息和错误代码
     * 
     * @param message 错误消息
     * @param errorCode 错误代码
     */
    public FileUploadException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * 构造方法 - 包含错误消息和异常原因
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "FILE_UPLOAD_ERROR";
    }
    
    /**
     * 构造方法 - 包含错误消息、错误代码和异常原因
     * 
     * @param message 错误消息
     * @param errorCode 错误代码
     * @param cause 异常原因
     */
    public FileUploadException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * 获取错误代码
     * 
     * @return 错误代码
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * 获取完整的错误信息（包含错误代码）
     * 
     * @return 完整的错误信息
     */
    public String getFullMessage() {
        return String.format("[%s] %s", errorCode, getMessage());
    }
}
