package com.example.shiyanshi.controller;

import com.example.shiyanshi.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
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
 * 文件上传控制器
 * 
 * 功能说明：
 * 1. 支持单文件和多文件上传
 * 2. 自动生成唯一文件名，避免文件名冲突
 * 3. 按日期分类存储，便于管理
 * 4. 支持多种文件类型验证
 * 5. 返回文件访问URL
 */
@RestController
@RequestMapping("/api/file")
@CrossOrigin
public class FileController {
    
    /**
     * 文件存储根路径
     * 可在application.properties中配置：file.upload-dir=D:/uploads
     * 默认值为项目根目录下的uploads文件夹
     */
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    /**
     * 文件访问URL前缀
     * 示例：http://localhost:8080
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
     * 上传单个文件
     * 
     * @param file 上传的文件
     * @param type 文件类型：avatar(头像)、lab(实验室图片)、document(文档)
     * @return 包含文件URL和路径的Result对象
     * 
     * 工作流程：
     * 1. 验证文件是否为空
     * 2. 验证文件大小是否超限
     * 3. 验证文件类型是否允许
     * 4. 生成唯一文件名（UUID + 原始扩展名）
     * 5. 按日期创建存储目录（如：uploads/2026/01/11/）
     * 6. 保存文件到指定目录
     * 7. 返回文件的访问URL
     */
    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "document") String type) {
        
        try {
            // 1. 验证文件
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            
            // 2. 验证文件大小
            if (file.getSize() > MAX_FILE_SIZE) {
                return Result.error("文件大小不能超过10MB");
            }
            
            // 3. 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                return Result.error("文件名无效");
            }
            
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            
            // 4. 验证文件类型
            if (!isAllowedFileType(extension, type)) {
                return Result.error("不支持的文件类型：" + extension);
            }
            
            // 5. 生成唯一文件名
            // 使用UUID确保文件名唯一性，避免覆盖
            String uniqueFileName = UUID.randomUUID().toString() + extension;
            
            // 6. 创建按日期分类的目录结构
            // 示例：uploads/2026/01/11/
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String typeDir = type; // avatar、lab、document
            Path targetDir = Paths.get(uploadDir, typeDir, datePath);
            
            // 确保目录存在，不存在则创建
            Files.createDirectories(targetDir);
            
            // 7. 完整的文件路径
            Path targetPath = targetDir.resolve(uniqueFileName);
            
            // 8. 保存文件
            file.transferTo(targetPath.toFile());
            
            // 9. 构建访问URL
            // 示例：http://localhost:8080/uploads/avatar/2026/01/11/xxxx-xxxx-xxxx.jpg
            String fileUrl = String.format("%s/uploads/%s/%s/%s", 
                accessUrl, typeDir, datePath, uniqueFileName);
            
            // 10. 返回结果
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("path", targetPath.toString());
            result.put("fileName", uniqueFileName);
            result.put("originalName", originalFilename);
            result.put("size", String.valueOf(file.getSize()));
            
            return Result.success(result);
            
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量上传文件
     * 
     * @param files 多个文件
     * @param type 文件类型
     * @return 包含所有文件信息的Result对象
     */
    @PostMapping("/upload-batch")
    public Result<Map<String, Object>> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "type", defaultValue = "document") String type) {
        
        if (files == null || files.length == 0) {
            return Result.error("请选择要上传的文件");
        }
        
        // 批量上传限制：最多10个文件
        if (files.length > 10) {
            return Result.error("单次最多上传10个文件");
        }
        
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        
        for (MultipartFile file : files) {
            Result<Map<String, String>> uploadResult = uploadFile(file, type);
            if (uploadResult.isSuccess()) {
                successCount++;
            } else {
                failCount++;
            }
        }
        
        result.put("total", files.length);
        result.put("success", successCount);
        result.put("fail", failCount);
        
        return Result.success(result);
    }
    
    /**
     * 删除文件
     * 
     * @param filePath 文件路径
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Result<Void> deleteFile(@RequestParam("path") String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
            return Result.success(null);
        } catch (IOException e) {
            return Result.error("文件删除失败：" + e.getMessage());
        }
    }
    
    /**
     * 验证文件类型是否允许上传
     * 
     * @param extension 文件扩展名
     * @param type 上传类型
     * @return 是否允许
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
}
