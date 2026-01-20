package com.example.shiyanshi;

import com.example.shiyanshi.exception.FileUploadException;
import com.example.shiyanshi.service.FileUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 文件上传服务测试类
 * 测试重构后的文件上传功能
 */
@SpringBootTest
@TestPropertySource(properties = {
    "file.upload-dir=${java.io.tmpdir}/test-uploads",
    "file.access-url=http://localhost:8080"
})
class FileUploadServiceTest {
    
    @Autowired
    private FileUploadService fileUploadService;
    
    @TempDir
    static Path tempDir;
    
    @BeforeEach
    void setUp() {
        // 确保测试目录存在
        System.setProperty("java.io.tmpdir", tempDir.toString());
    }
    
    @Test
    void testUploadValidImageFile() throws IOException {
        // 准备测试文件
        String fileName = "test-image.jpg";
        byte[] content = "fake image content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            fileName, 
            "image/jpeg", 
            content
        );
        
        // 执行上传
        Map<String, String> result = fileUploadService.uploadFile(file, "avatar");
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("url"));
        assertTrue(result.containsKey("path"));
        assertTrue(result.containsKey("fileName"));
        assertTrue(result.containsKey("originalName"));
        assertTrue(result.containsKey("size"));
        assertTrue(result.containsKey("uploadTime"));
        
        // 验证文件已保存
        String filePath = result.get("path");
        assertTrue(Files.exists(Path.of(filePath)));
        
        // 验证文件名包含UUID
        String savedFileName = result.get("fileName");
        assertTrue(savedFileName.endsWith(".jpg"));
        assertTrue(savedFileName.length() > 10); // UUID + 扩展名
        
        // 验证原始文件名
        assertEquals(fileName, result.get("originalName"));
    }
    
    @Test
    void testUploadEmptyFile() {
        // 准备空文件
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "empty.txt", 
            "text/plain", 
            new byte[0]
        );
        
        // 验证抛出异常
        FileUploadException exception = assertThrows(FileUploadException.class, 
            () -> fileUploadService.uploadFile(file, "document"));
        
        assertTrue(exception.getMessage().contains("文件不能为空"));
    }
    
    @Test
    void testUploadFileTooLarge() {
        // 准备大文件（超过10MB）
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "large-file.jpg", 
            "image/jpeg", 
            largeContent
        );
        
        // 验证抛出异常
        FileUploadException exception = assertThrows(FileUploadException.class, 
            () -> fileUploadService.uploadFile(file, "avatar"));
        
        assertTrue(exception.getMessage().contains("文件大小不能超过10MB"));
    }
    
    @Test
    void testUploadInvalidFileType() {
        // 准备不支持的文件类型
        byte[] content = "test content".getBytes();
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.exe", 
            "application/octet-stream", 
            content
        );
        
        // 验证抛出异常
        FileUploadException exception = assertThrows(FileUploadException.class, 
            () -> fileUploadService.uploadFile(file, "avatar"));
        
        assertTrue(exception.getMessage().contains("不支持的文件类型"));
    }
    
    @Test
    void testDeleteExistingFile() throws IOException {
        // 先创建一个文件
        Path testFile = tempDir.resolve("test-delete.txt");
        Files.write(testFile, "test content".getBytes());
        
        // 执行删除
        fileUploadService.deleteFile(testFile.toString());
        
        // 验证文件已删除
        assertFalse(Files.exists(testFile));
    }
    
    @Test
    void testDeleteNonExistingFile() {
        // 尝试删除不存在的文件
        String nonExistingPath = tempDir.resolve("non-existing.txt").toString();
        
        // 验证抛出异常
        FileUploadException exception = assertThrows(FileUploadException.class, 
            () -> fileUploadService.deleteFile(nonExistingPath));
        
        assertTrue(exception.getMessage().contains("文件不存在"));
    }
    
    @Test
    void testFileTypeValidation() throws IOException {
        // 测试头像类型只接受图片
        byte[] content = "test".getBytes();
        
        // 图片文件应该成功
        MockMultipartFile jpgFile = new MockMultipartFile(
            "file", "test.jpg", "image/jpeg", content);
        assertDoesNotThrow(() -> fileUploadService.uploadFile(jpgFile, "avatar"));
        
        // PDF文件应该失败
        MockMultipartFile pdfFile = new MockMultipartFile(
            "file", "test.pdf", "application/pdf", content);
        FileUploadException exception = assertThrows(FileUploadException.class,
            () -> fileUploadService.uploadFile(pdfFile, "avatar"));
        assertTrue(exception.getMessage().contains("不支持的文件类型"));
        
        // 文档类型应该接受PDF
        assertDoesNotThrow(() -> fileUploadService.uploadFile(pdfFile, "document"));
    }
}
