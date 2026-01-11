package com.example.shiyanshi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 静态资源配置
 * 
 * 作用：配置上传文件的访问路径
 * 使得上传的文件可以通过HTTP URL直接访问
 * 
 * 示例：
 * 文件实际路径：D:/uploads/avatar/2026/01/11/xxx.jpg
 * 访问URL：http://localhost:8080/uploads/avatar/2026/01/11/xxx.jpg
 */
@Configuration
public class ResourceConfig implements WebMvcConfigurer {
    
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件上传目录的访问映射
        // /uploads/** 表示匹配所有以/uploads/开头的请求
        // file: 协议表示从文件系统读取
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
