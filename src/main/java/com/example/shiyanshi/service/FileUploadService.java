package com.example.shiyanshi.service;

import com.example.shiyanshi.exception.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传服务
 * 
 * 重构说明：
 * 1. 将文件上传的核心业务逻辑从Controller中分离
 * 2. 提供更好的错误处理和异常管理
 * 3. 支持多种文件类型验证策略
 * 4. 易于扩展支持不同的存储策略
 */
@Service
public class FileUploadService {
    
    /**
     * 文件存储根路径
     */
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    /**
     * 文件访问URL前缀
     */
    @Value("${file.access-url:http://localhost:8080}")
    private String accessUrl;
    
    /**
     * 允许上传的图片格式
     */
    private static final String[] IMAGE_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp"};
    
    /**
     * 允许上传的文档格式
     */
    private static final String[] DOCUMENT_EXTENSIONS = {".pdf", ".doc", ".docx", ".xls", ".xlsx", ".txt"};
    
    /**
     * 单个文件最大大小（10MB）
     */
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    /**
     * 上传文件
     * 
     * @param file 上传的文件
     * @param type 文件类型：avatar(头像)、lab(实验室图片)、document(文档)
     * @return 文件上传结果信息
     * @throws FileUploadException 文件上传异常
     */
    public Map<String, String> uploadFile(MultipartFile file, String type) throws FileUploadException {
        try {
            // 1. 基础验证
            validateFile(file);
            
            // 2. 验证文件类型
            validateFileType(file, type);
            
            // 3. 生成文件存储信息
            FileStorageInfo storageInfo = generateStorageInfo(file, type);
            
            // 4. 保存文件
            saveFile(file, storageInfo);
            
            // 5. 返回结果
            return buildUploadResult(file, storageInfo);
            
        } catch (IOException e) {
            throw new FileUploadException("文件保存失败: " + e.getMessage(), "FILE_SAVE_ERROR", e);
        }
    }
    
    /**
     * 基础文件验证
     */
    private void validateFile(MultipartFile file) throws FileUploadException {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("文件不能为空", "FILE_EMPTY");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileUploadException("文件大小不能超过10MB", "FILE_TOO_LARGE");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new FileUploadException("文件名无效", "FILENAME_INVALID");
        }
    }
    
    /**
     * 验证文件类型
     */
    private void validateFileType(MultipartFile file, String type) throws FileUploadException {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        
        if (!isAllowedFileType(extension, type)) {
            throw new FileUploadException("不支持的文件类型: " + extension, "FILE_TYPE_NOT_ALLOWED");
        }
    }
    
    /**
     * 生成文件存储信息
     */
    private FileStorageInfo generateStorageInfo(MultipartFile file, String type) {
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        // 生成唯一文件名
        String uniqueFileName = UUID.randomUUID().toString() + extension;
        
        // 创建按日期分类的目录结构
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String typeDir = type;
        
        // 确保上传目录是绝对路径
        Path baseUploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path targetDir = baseUploadDir.resolve(typeDir).resolve(datePath);
        Path targetPath = targetDir.resolve(uniqueFileName);
        
        return new FileStorageInfo(uniqueFileName, targetDir, targetPath, typeDir, datePath);
    }
    
    /**
     * 保存文件到指定路径
     */
    private void saveFile(MultipartFile file, FileStorageInfo storageInfo) throws IOException {
        // 确保目录存在
        Files.createDirectories(storageInfo.getTargetDir());
        
        // 保存文件
        file.transferTo(storageInfo.getTargetPath().toFile());
    }
    
    /**
     * 构建上传结果
     */
    private Map<String, String> buildUploadResult(MultipartFile file, FileStorageInfo storageInfo) {
        String originalFilename = file.getOriginalFilename();
        
        // 构建访问URL
        String fileUrl = String.format("%s/uploads/%s/%s/%s", 
            accessUrl, storageInfo.getTypeDir(), storageInfo.getDatePath(), storageInfo.getFileName());
        
        Map<String, String> result = new HashMap<>();
        result.put("url", fileUrl);
        result.put("path", storageInfo.getTargetPath().toString());
        result.put("fileName", storageInfo.getFileName());
        result.put("originalName", originalFilename);
        result.put("size", String.valueOf(file.getSize()));
        result.put("uploadTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        
        return result;
    }
    
    /**
     * 验证文件类型是否允许上传
     */
    private boolean isAllowedFileType(String extension, String type) {
        switch (type) {
            case "avatar":
            case "lab":
                // 头像和实验室图片只允许图片格式
                return containsExtension(IMAGE_EXTENSIONS, extension);
            case "document":
                // 文档类型允许图片和文档格式
                return containsExtension(IMAGE_EXTENSIONS, extension) || 
                       containsExtension(DOCUMENT_EXTENSIONS, extension);
            default:
                return false;
        }
    }
    
    /**
     * 检查扩展名是否在允许列表中
     */
    private boolean containsExtension(String[] extensions, String extension) {
        for (String ext : extensions) {
            if (ext.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 删除文件
     * 
     * @param filePath 文件路径
     * @throws FileUploadException 删除文件异常
     */
    public void deleteFile(String filePath) throws FileUploadException {
        try {
            Path path = Paths.get(filePath).toAbsolutePath().normalize();
            if (Files.exists(path)) {
                Files.delete(path);
            } else {
                throw new FileUploadException("文件不存在: " + filePath, "FILE_NOT_FOUND");
            }
        } catch (IOException e) {
            throw new FileUploadException("文件删除失败: " + e.getMessage(), "FILE_DELETE_ERROR", e);
        }
    }
    
    /**
     * 文件存储信息内部类
     */
    private static class FileStorageInfo {
        private final String fileName;
        private final Path targetDir;
        private final Path targetPath;
        private final String typeDir;
        private final String datePath;
        
        public FileStorageInfo(String fileName, Path targetDir, Path targetPath, String typeDir, String datePath) {
            this.fileName = fileName;
            this.targetDir = targetDir;
            this.targetPath = targetPath;
            this.typeDir = typeDir;
            this.datePath = datePath;
        }
        
        public String getFileName() { return fileName; }
        public Path getTargetDir() { return targetDir; }
        public Path getTargetPath() { return targetPath; }
        public String getTypeDir() { return typeDir; }
        public String getDatePath() { return datePath; }
    }
}
