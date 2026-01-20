package com.example.shiyanshi.controller;

import com.example.shiyanshi.annotation.RequirePermission;
import com.example.shiyanshi.common.Result;
import com.example.shiyanshi.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 * 
 * 功能说明：
 * 1. 支持单文件和多文件上传
 * 2. 自动生成唯一文件名，避免文件名冲突
 * 3. 按日期分类存储，便于管理
 * 4. 支持多种文件类型验证
 * 5. 返回文件访问URL
 * 
 * 重构说明：
 * 1. 将业务逻辑提取到FileUploadService
 * 2. 简化Controller，只处理HTTP请求和响应
 * 3. 使用统一的异常处理
 */
@RestController
@RequestMapping("/api/file")
@CrossOrigin
public class FileController {
    
    @Autowired
    private FileUploadService fileUploadService;
    
    /**
     * 上传单个文件
     * 
     * @param file 上传的文件
     * @param type 文件类型：avatar(头像)、lab(实验室图片)、document(文档)
     * @return 包含文件URL和路径的Result对象
     */
    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "document") String type) {
        
        try {
            Map<String, String> result = fileUploadService.uploadFile(file, type);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量上传文件
     * 
     * @param files 多个文件
     * @param type 文件类型
     * @return 包含所有文件信息的Result对象
     */
    @RequirePermission(value = 2, description = "批量上传文件需要管理员及以上权限")
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
        StringBuilder failMessages = new StringBuilder();
        
        for (MultipartFile file : files) {
            try {
                Map<String, String> fileResult = fileUploadService.uploadFile(file, type);
                if (fileResult != null && !fileResult.isEmpty()) {
                    successCount++;
                }
            } catch (RuntimeException e) {
                failCount++;
                if (failMessages.length() > 0) {
                    failMessages.append("; ");
                }
                failMessages.append(file.getOriginalFilename()).append(": ").append(e.getMessage());
            }
        }
        
        result.put("total", files.length);
        result.put("success", successCount);
        result.put("fail", failCount);
        if (failMessages.length() > 0) {
            result.put("failMessages", failMessages.toString());
        }
        
        return Result.success(result);
    }
    
    /**
     * 删除文件
     * 
     * @param filePath 文件路径
     * @return 删除结果
     */
    @RequirePermission(value = 2, description = "删除文件需要超级管理员权限")
    @DeleteMapping("/delete")
    public Result<Void> deleteFile(@RequestParam("path") String filePath) {
        try {
            fileUploadService.deleteFile(filePath);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}
