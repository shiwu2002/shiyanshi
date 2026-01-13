# 文件上传功能说明文档

## 概述

本系统实现了完整的文件上传功能，支持单文件上传、批量上传和文件删除。文件上传采用分类存储策略，按类型和日期组织目录结构，便于管理和维护。

## 目录结构

```
uploads/
├── avatar/              # 头像文件
│   └── 2026/
│       └── 01/
│           └── 11/
│               └── xxxxx-xxxx-xxxx.jpg
├── document/            # 文档文件
│   └── 2026/01/11/
│       └── xxxxx-xxxx-xxxx.pdf
└── laboratory/          # 实验室相关文件
    └── 2026/01/11/
        └── xxxxx-xxxx-xxxx.png
```

## API 接口说明

### 1. 单文件上传

**接口地址**: `POST /api/file/upload`

**请求参数**:
- `file`: 文件对象（必填）
- `type`: 文件类型（可选，默认为 "document"）
  - 可选值: `avatar`(头像)、`document`(文档)、`laboratory`(实验室)

**响应示例**:
```json
{
  "code": 200,
  "message": "文件上传成功",
  "data": {
    "fileName": "abc123.jpg",
    "filePath": "uploads/avatar/2026/01/11/abc123.jpg",
    "fileUrl": "http://localhost:8080/uploads/avatar/2026/01/11/abc123.jpg"
  }
}
```

### 2. 批量文件上传

**接口地址**: `POST /api/file/upload-batch`

**请求参数**:
- `files`: 文件数组（必填）
- `type`: 文件类型（可选，默认为 "document"）

**响应示例**:
```json
{
  "code": 200,
  "message": "成功上传2个文件",
  "data": [
    {
      "fileName": "file1.jpg",
      "filePath": "uploads/document/2026/01/11/file1.jpg",
      "fileUrl": "http://localhost:8080/uploads/document/2026/01/11/file1.jpg"
    },
    {
      "fileName": "file2.pdf",
      "filePath": "uploads/document/2026/01/11/file2.pdf",
      "fileUrl": "http://localhost:8080/uploads/document/2026/01/11/file2.pdf"
    }
  ]
}
```

### 3. 文件删除

**接口地址**: `DELETE /api/file/delete`

**请求参数**:
- `filePath`: 文件相对路径（必填）
  - 示例: `uploads/avatar/2026/01/11/abc123.jpg`

**响应示例**:
```json
{
  "code": 200,
  "message": "文件删除成功",
  "data": null
}
```

## 文件上传流程详解

### 工作流程图

```
┌─────────────┐
│ 1. 接收文件  │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ 2. 验证文件  │ ← 检查文件是否为空
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ 3. 大小验证  │ ← 最大10MB
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ 4. 类型验证  │ ← 允许的文件格式
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ 5. 生成文件名│ ← UUID + 原扩展名
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ 6. 构建路径  │ ← type/yyyy/MM/dd/
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ 7. 创建目录  │ ← 自动创建不存在的目录
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ 8. 保存文件  │ ← 写入磁盘
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ 9. 生成URL   │ ← 返回访问地址
└──────┬──────┘
       │
       ▼
┌─────────────┐
│10. 返回结果  │
└─────────────┘
```

### 核心逻辑说明

#### 步骤1-2：文件接收与验证
```java
if (file == null || file.isEmpty()) {
    return Result.error("文件不能为空");
}
```
- 检查上传的文件是否为空
- 防止无效请求浪费服务器资源

#### 步骤3：文件大小验证
```java
long maxSize = 10 * 1024 * 1024; // 10MB
if (file.getSize() > maxSize) {
    return Result.error("文件大小不能超过10MB");
}
```
- 限制单个文件最大为10MB
- 可在 application.properties 中配置
- 防止恶意上传超大文件

#### 步骤4：文件类型验证
```java
Set<String> allowedTypes = Set.of(
    "image/jpeg", "image/png", "image/gif",  // 图片
    "application/pdf",                        // PDF
    "application/msword",                     // Word
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
);
if (!allowedTypes.contains(contentType)) {
    return Result.error("不支持的文件类型");
}
```
- 白名单机制，只允许特定类型文件
- 防止上传可执行文件等危险文件

#### 步骤5：生成唯一文件名
```java
String originalFilename = file.getOriginalFilename();
String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
String newFilename = UUID.randomUUID().toString() + extension;
```
- 使用 UUID 生成唯一标识
- 保留原文件扩展名
- 避免文件名冲突
- 防止路径遍历攻击

#### 步骤6：构建存储路径
```java
LocalDate now = LocalDate.now();
String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
String relativePath = type + "/" + datePath + "/" + newFilename;
```
- 按类型分类：avatar、document、laboratory
- 按日期分层：年/月/日
- 便于文件管理和查找
- 避免单个目录文件过多

#### 步骤7-8：创建目录并保存文件
```java
Path targetPath = Paths.get(uploadDir, relativePath);
Files.createDirectories(targetPath.getParent());
file.transferTo(targetPath.toFile());
```
- 自动创建不存在的目录
- 使用 NIO 的高效文件操作
- transferTo 方法直接保存文件

#### 步骤9：生成访问URL
```java
String fileUrl = fileAccessUrl + "/uploads/" + relativePath.replace("\\", "/");
```
- 拼接完整的 HTTP 访问地址
- 统一使用正斜杠，兼容不同操作系统
- 前端可直接使用该 URL 访问文件

## 前端调用示例

### 小程序端上传示例

```javascript
// 单文件上传
wx.chooseImage({
  count: 1,
  sizeType: ['compressed'],
  sourceType: ['album', 'camera'],
  success(res) {
    const tempFilePath = res.tempFilePaths[0];
    
    wx.uploadFile({
      url: 'http://localhost:8080/api/file/upload',
      filePath: tempFilePath,
      name: 'file',
      formData: {
        'type': 'avatar'  // 头像类型
      },
      success(uploadRes) {
        const data = JSON.parse(uploadRes.data);
        if (data.code === 200) {
          console.log('文件URL:', data.data.fileUrl);
          // 使用 fileUrl 显示图片或保存到数据库
        }
      }
    });
  }
});

// 批量上传
wx.chooseImage({
  count: 9,
  success(res) {
    const tempFilePaths = res.tempFilePaths;
    
    // 依次上传每个文件
    tempFilePaths.forEach(filePath => {
      wx.uploadFile({
        url: 'http://localhost:8080/api/file/upload',
        filePath: filePath,
        name: 'file',
        formData: {
          'type': 'laboratory'
        }
      });
    });
  }
});
```

### Web前端上传示例

```html
<!-- 单文件上传 -->
<input type="file" id="fileInput" accept="image/*,.pdf,.doc,.docx">
<button onclick="uploadFile()">上传</button>

<script>
function uploadFile() {
  const fileInput = document.getElementById('fileInput');
  const file = fileInput.files[0];
  
  if (!file) {
    alert('请选择文件');
    return;
  }
  
  const formData = new FormData();
  formData.append('file', file);
  formData.append('type', 'document');
  
  fetch('http://localhost:8080/api/file/upload', {
    method: 'POST',
    body: formData
  })
  .then(res => res.json())
  .then(data => {
    if (data.code === 200) {
      console.log('文件URL:', data.data.fileUrl);
      // 可以将 fileUrl 保存到数据库或显示在页面上
    } else {
      alert(data.message);
    }
  });
}
</script>
```

```html
<!-- 批量上传 -->
<input type="file" id="filesInput" multiple accept="image/*">
<button onclick="uploadMultipleFiles()">批量上传</button>

<script>
function uploadMultipleFiles() {
  const filesInput = document.getElementById('filesInput');
  const files = filesInput.files;
  
  const formData = new FormData();
  for (let i = 0; i < files.length; i++) {
    formData.append('files', files[i]);
  }
  formData.append('type', 'laboratory');
  
  fetch('http://localhost:8080/api/file/upload-batch', {
    method: 'POST',
    body: formData
  })
  .then(res => res.json())
  .then(data => {
    if (data.code === 200) {
      console.log('上传成功:', data.data);
    }
  });
}
</script>
```

## 配置说明

### application.properties 配置项

```properties
# 单个文件最大大小（默认1MB，这里设置为10MB）
spring.servlet.multipart.max-file-size=10MB

# 单次请求最大大小（批量上传时的总大小限制）
spring.servlet.multipart.max-request-size=50MB

# 启用文件上传功能
spring.servlet.multipart.enabled=true

# 文件上传目录（相对路径或绝对路径）
file.upload-dir=./uploads

# 文件访问URL前缀（用于生成完整的文件访问地址）
file.access-url=http://localhost:8080
```

### 生产环境配置建议

```properties
# 使用绝对路径
file.upload-dir=/var/www/uploads

# 使用域名
file.access-url=https://your-domain.com

# 根据需求调整大小限制
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=100MB
```

## 安全性考虑

### 1. 文件类型验证
- 使用白名单机制，只允许特定类型文件
- 检查 Content-Type 和文件扩展名
- 防止上传可执行文件、脚本文件

### 2. 文件大小限制
- 防止恶意上传超大文件消耗服务器资源
- 单文件限制：10MB
- 单次请求限制：50MB

### 3. 文件名安全
- 使用 UUID 重命名文件
- 避免使用原始文件名
- 防止路径遍历攻击（如 `../../etc/passwd`）

### 4. 存储路径隔离
- 按类型和日期分类存储
- 不在 Web 根目录下存储
- 通过静态资源映射访问

### 5. 建议的额外安全措施
```java
// 1. 添加图片内容验证（防止伪装）
BufferedImage image = ImageIO.read(file.getInputStream());
if (image == null) {
    return Result.error("无效的图片文件");
}

// 2. 添加病毒扫描（可选）
// 使用 ClamAV 等工具扫描上传的文件

// 3. 添加访问权限控制
// 某些敏感文件只允许上传者或管理员访问

// 4. 添加水印（针对图片）
// 为上传的图片添加水印标识
```

## 常见问题

### Q1: 上传失败，提示"文件大小超限"
**A**: 检查以下配置：
- application.properties 中的 `spring.servlet.multipart.max-file-size`
- Tomcat/Servlet容器的请求大小限制
- 前端是否有文件大小预检查

### Q2: 上传成功但无法访问文件
**A**: 检查以下内容：
- ResourceConfig.java 是否正确配置
- 文件路径是否正确
- 文件权限是否正确（Linux系统）
- 防火墙/安全组是否开放端口

### Q3: 中文文件名乱码
**A**: 系统使用 UUID 重命名，不存在中文乱码问题。如需保留原文件名：
```java
// 可以在数据库中保存原始文件名
map.put("originalName", originalFilename);
map.put("fileName", newFilename);
```

### Q4: 如何集成到其他模块
**A**: 示例 - 用户头像上传：
```java
// UserController.java
@PostMapping("/avatar")
public Result<User> updateAvatar(@RequestParam("file") MultipartFile file) {
    // 1. 调用文件上传服务
    Result<Map<String, String>> uploadResult = fileController.uploadFile(file, "avatar");
    
    if (!uploadResult.isSuccess()) {
        return Result.error(uploadResult.getMessage());
    }
    
    // 2. 获取文件URL
    String avatarUrl = uploadResult.getData().get("fileUrl");
    
    // 3. 更新用户头像字段
    User user = userService.getCurrentUser();
    user.setAvatar(avatarUrl);
    userService.updateById(user);
    
    return Result.success(user);
}
```

### Q5: 如何实现文件下载
**A**: 添加下载接口：
```java
@GetMapping("/download")
public ResponseEntity<Resource> downloadFile(@RequestParam String filePath) {
    try {
        Path path = Paths.get(uploadDir, filePath);
        Resource resource = new UrlResource(path.toUri());
        
        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    } catch (Exception e) {
        return ResponseEntity.internalServerError().build();
    }
}
```

## 性能优化建议

### 1. 使用异步处理
对于大文件或批量上传，可以使用异步处理：
```java
@Async
public CompletableFuture<Result> uploadFileAsync(MultipartFile file, String type) {
    // 异步处理文件上传
    return CompletableFuture.completedFuture(uploadFile(file, type));
}
```

### 2. 使用对象存储
生产环境建议使用云对象存储（如阿里云OSS、腾讯云COS）：
- 更高的可靠性和可用性
- CDN加速访问
- 减轻应用服务器压力

### 3. 定期清理临时文件
```java
@Scheduled(cron = "0 0 2 * * ?") // 每天凌晨2点执行
public void cleanTempFiles() {
    // 清理超过30天的临时文件
}
```

## 总结

本文件上传系统具有以下特点：

✅ **功能完整**: 支持单文件、批量上传和删除  
✅ **安全可靠**: 多重验证机制，防止恶意上传  
✅ **易于使用**: RESTful API设计，前端调用简单  
✅ **易于管理**: 分类分日期存储，便于维护  
✅ **高度可配置**: 通过配置文件灵活调整参数  
✅ **良好扩展性**: 可轻松集成到其他业务模块  

如有任何问题或需要进一步的功能扩展，请随时联系。
