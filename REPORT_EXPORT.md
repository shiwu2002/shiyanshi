# 预约报表导出功能使用说明

## 功能概述

实验室预约系统提供了管理员端的报表导出功能，支持导出预约明细报表和统计报表，导出格式为Excel(.xlsx)文件。

## 接口说明

### 1. 导出预约明细报表

#### 接口地址
```
GET /api/report/export-reservations
```

#### 功能描述
导出指定条件下的预约明细数据到Excel文件，包含完整的预约信息和审核信息。

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| startDate | LocalDate | 否 | 开始日期(预约日期) | 2024-01-01 |
| endDate | LocalDate | 否 | 结束日期(预约日期) | 2024-12-31 |
| laboratoryId | Long | 否 | 实验室ID | 1 |
| status | Integer | 否 | 预约状态<br>0-待审核<br>1-已通过<br>2-已拒绝<br>3-已取消<br>4-已完成 | 1 |

#### 请求示例

```bash
# 导出所有预约
GET http://localhost:8080/api/report/export-reservations

# 导出指定日期范围的预约
GET http://localhost:8080/api/report/export-reservations?startDate=2024-01-01&endDate=2024-12-31

# 导出指定实验室的已通过预约
GET http://localhost:8080/api/report/export-reservations?laboratoryId=1&status=1

# 组合条件导出
GET http://localhost:8080/api/report/export-reservations?startDate=2024-06-01&endDate=2024-06-30&laboratoryId=1&status=1
```

#### 响应说明

成功时直接下载Excel文件，文件名格式：
- 有日期范围：`预约明细报表_2024-01-01至2024-12-31.xlsx`
- 无日期范围：`预约明细报表.xlsx`

#### Excel表格字段

| 列名 | 说明 |
|------|------|
| 预约ID | 预约记录的唯一标识 |
| 用户姓名 | 预约人姓名 |
| 实验室名称 | 预约的实验室名称 |
| 预约日期 | 预约使用日期 |
| 时间段 | 预约的时间段 |
| 使用人数 | 预约使用人数 |
| 实验名称 | 实验项目名称 |
| 使用目的 | 预约使用目的说明 |
| 使用设备 | 需要使用的设备 |
| 状态 | 预约状态(待审核/已通过/已拒绝/已取消/已完成) |
| 审核人 | 审核管理员姓名 |
| 审核意见 | 审核备注说明 |
| 提交时间 | 预约提交时间 |
| 审核时间 | 审核操作时间 |

### 2. 导出统计报表

#### 接口地址
```
GET /api/report/export-statistics
```

#### 功能描述
导出指定日期范围内的预约统计数据，包含各状态的预约数量和占比。

#### 请求参数

| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| startDate | LocalDate | 否 | 开始日期(预约日期) | 2024-01-01 |
| endDate | LocalDate | 否 | 结束日期(预约日期) | 2024-12-31 |

#### 请求示例

```bash
# 导出所有时间段的统计
GET http://localhost:8080/api/report/export-statistics

# 导出指定日期范围的统计
GET http://localhost:8080/api/report/export-statistics?startDate=2024-01-01&endDate=2024-12-31
```

#### 响应说明

成功时直接下载Excel文件，文件名格式：
- 有日期范围：`预约统计报表_2024-01-01至2024-12-31.xlsx`
- 无日期范围：`预约统计报表.xlsx`

#### Excel表格字段

| 列名 | 说明 |
|------|------|
| 状态 | 预约状态名称 |
| 数量 | 该状态的预约数量 |
| 占比 | 该状态占总数的百分比 |

统计报表包含以下行：
1. 待审核预约统计
2. 已通过预约统计
3. 已拒绝预约统计
4. 已取消预约统计
5. 已完成预约统计
6. 总计行

## 使用场景

### 场景1：导出月度预约明细
管理员需要导出某月的所有预约记录用于归档。

```bash
GET /api/report/export-reservations?startDate=2024-06-01&endDate=2024-06-30
```

### 场景2：导出特定实验室的已完成预约
查看某个实验室的使用情况。

```bash
GET /api/report/export-reservations?laboratoryId=1&status=4
```

### 场景3：导出季度统计报表
统计本季度的预约情况，分析各状态占比。

```bash
GET /api/report/export-statistics?startDate=2024-04-01&endDate=2024-06-30
```

### 场景4：导出待审核预约列表
快速导出所有待审核的预约进行批量处理。

```bash
GET /api/report/export-reservations?status=0
```

## 前端集成示例

### Vue.js示例

```javascript
// 导出预约明细
exportReservations() {
  const params = {
    startDate: this.searchForm.startDate,
    endDate: this.searchForm.endDate,
    laboratoryId: this.searchForm.laboratoryId,
    status: this.searchForm.status
  };
  
  // 构建URL参数
  const queryString = Object.entries(params)
    .filter(([key, value]) => value !== null && value !== undefined && value !== '')
    .map(([key, value]) => `${key}=${value}`)
    .join('&');
  
  // 打开下载链接
  const url = `/api/report/export-reservations${queryString ? '?' + queryString : ''}`;
  window.open(url, '_blank');
}

// 导出统计报表
exportStatistics() {
  const params = {
    startDate: this.searchForm.startDate,
    endDate: this.searchForm.endDate
  };
  
  const queryString = Object.entries(params)
    .filter(([key, value]) => value !== null && value !== undefined && value !== '')
    .map(([key, value]) => `${key}=${value}`)
    .join('&');
  
  const url = `/api/report/export-statistics${queryString ? '?' + queryString : ''}`;
  window.open(url, '_blank');
}
```

### 微信小程序示例

```javascript
// 导出预约明细（小程序需要先下载文件）
exportReservations() {
  const params = {
    startDate: this.data.startDate,
    endDate: this.data.endDate,
    laboratoryId: this.data.laboratoryId,
    status: this.data.status
  };
  
  // 构建URL参数
  let queryString = '';
  for (let key in params) {
    if (params[key] !== null && params[key] !== undefined && params[key] !== '') {
      queryString += `${queryString ? '&' : ''}${key}=${params[key]}`;
    }
  }
  
  wx.showLoading({
    title: '正在导出...'
  });
  
  wx.downloadFile({
    url: `${baseUrl}/api/report/export-reservations${queryString ? '?' + queryString : ''}`,
    header: {
      'Authorization': wx.getStorageSync('token')
    },
    success: (res) => {
      wx.hideLoading();
      if (res.statusCode === 200) {
        // 打开文件
        wx.openDocument({
          filePath: res.tempFilePath,
          fileType: 'xlsx',
          success: () => {
            console.log('文件打开成功');
          },
          fail: (err) => {
            wx.showToast({
              title: '文件打开失败',
              icon: 'none'
            });
          }
        });
      } else {
        wx.showToast({
          title: '导出失败',
          icon: 'none'
        });
      }
    },
    fail: (err) => {
      wx.hideLoading();
      wx.showToast({
        title: '导出失败',
        icon: 'none'
      });
    }
  });
}

// 导出统计报表
exportStatistics() {
  // 类似上面的实现，只是URL改为 /api/report/export-statistics
  // ...
}
```

## 注意事项

1. **权限控制**：报表导出功能仅限管理员使用，需要在前端和后端都进行权限验证。

2. **日期格式**：日期参数格式为 `yyyy-MM-dd`，例如：`2024-01-01`。

3. **数据量限制**：大量数据导出可能需要较长时间，建议：
   - 添加日期范围限制
   - 前端显示加载提示
   - 考虑分页导出大数据集

4. **浏览器兼容性**：
   - 现代浏览器支持直接下载
   - 部分移动端浏览器可能需要特殊处理

5. **中文文件名**：
   - 文件名采用UTF-8编码
   - 部分浏览器可能显示乱码，这是正常现象
   - 下载后的文件名是正确的

6. **Excel版本**：
   - 导出格式为 Excel 2007+ (.xlsx)
   - 兼容 Microsoft Excel、WPS、LibreOffice 等软件

7. **小程序集成**：
   - 小程序需要配置下载域名白名单
   - 需要添加文件下载权限
   - 建议提供"在浏览器中打开"选项

## 错误处理

### 常见错误

| 错误情况 | 原因 | 解决方法 |
|---------|------|----------|
| 下载失败 | 网络问题或服务器错误 | 检查网络连接，重试下载 |
| 文件无法打开 | 文件损坏或格式不支持 | 重新下载文件，确保Excel软件版本支持 |
| 数据为空 | 查询条件未匹配到数据 | 调整查询条件，确认数据存在 |
| 权限不足 | 非管理员用户访问 | 使用管理员账号登录 |

## 扩展功能建议

1. **自定义导出字段**：允许用户选择要导出的列
2. **导出格式选择**：支持PDF、CSV等多种格式
3. **定时导出任务**：设置定期自动导出报表
4. **邮件发送**：导出后自动发送到管理员邮箱
5. **数据透视表**：在Excel中添加数据透视分析

## 技术实现

- **后端框架**：Spring Boot 4.0.1
- **Excel处理**：Apache POI 5.2.5
- **日期处理**：Java 8 LocalDate/LocalDateTime
- **编码处理**：UTF-8文件名编码
- **数据查询**：MyBatis-Plus条件查询

## 更新日志

### v1.0.0 (2024-01-11)
- ✅ 实现预约明细报表导出
- ✅ 实现统计报表导出
- ✅ 支持多条件筛选
- ✅ 支持动态文件名生成
- ✅ 优化Excel样式设置
