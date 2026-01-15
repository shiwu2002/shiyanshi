# 微信小程序登录注册绑定整合方案

## 概述

本文档描述了微信小程序登录、用户注册/登录、以及微信账号绑定的完整流程。

## 整合后的流程

### 流程图

```
用户打开小程序
    ↓
调用 wx.login() 获取 code
    ↓
POST /api/wx/login (传入 code)
    ↓
后端调用微信接口获取 openid/sessionKey
    ↓
    ├─→ 已绑定用户？
    │   └─→ 是：返回 token 和用户信息 (needBind=false)
    │       └─→ 登录成功，进入主页
    │
    └─→ 未绑定？
        └─→ 是：返回 openid/unionid (needBind=true)
            ↓
            前端提示用户选择：
            ├─→ 已有账号：登录并绑定
            │   └─→ POST /api/user/login
            │       {
            │         "username": "用户名",
            │         "password": "密码",
            │         "openid": "微信返回的openid",
            │         "unionid": "微信返回的unionid",
            │         "sessionKey": "不需要传，后端已保存"
            │       }
            │       └─→ 登录成功 + 自动绑定微信
            │           └─→ 返回 token 和 wechatBound=true
            │
            └─→ 没有账号：注册并绑定
                └─→ POST /api/user/register
                    {
                      "username": "用户名",
                      "password": "密码",
                      "email": "邮箱（可选）",
                      "realName": "真实姓名（可选）",
                      "openid": "微信返回的openid",
                      "unionid": "微信返回的unionid",
                      "sessionKey": "不需要传，后端已保存"
                    }
                    └─→ 注册成功 + 自动绑定微信
                        └─→ 返回 token 和 wechatBound=true
```

## API 接口详细说明

### 1. 微信登录接口

**接口地址：** `POST /api/wx/login`

**请求参数：**
```json
{
  "code": "wx.login()返回的临时登录凭证"
}
```

**响应示例（已绑定）：**
```json
{
  "code": 200,
  "message": "微信登录成功（已绑定）",
  "data": {
    "needBind": false,
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "zhangsan",
    "userType": 1,
    "realName": "张三",
    "openid": "oABC123456789",
    "unionid": "uDEF123456789"
  }
}
```

**响应示例（未绑定）：**
```json
{
  "code": 200,
  "message": "微信登录成功（未绑定）",
  "data": {
    "needBind": true,
    "openid": "oABC123456789",
    "unionid": "uDEF123456789"
  }
}
```

### 2. 用户登录接口（整合微信绑定）

**接口地址：** `POST /api/user/login`

**请求参数：**
```json
{
  "username": "用户名",
  "password": "密码",
  "openid": "微信openid（可选，用于自动绑定）",
  "unionid": "微信unionid（可选）",
  "sessionKey": "微信sessionKey（可选，一般不需要前端传）"
}
```

**响应示例（登录成功且绑定成功）：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "zhangsan",
    "userType": 1,
    "realName": "张三",
    "wechatBound": true,
    "openid": "oABC123456789",
    "unionid": "uDEF123456789"
  }
}
```

**响应示例（登录成功但微信已绑定其他账号）：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "zhangsan",
    "userType": 1,
    "realName": "张三",
    "bindWarning": "该微信已绑定其他账号"
  }
}
```

### 3. 用户注册接口（整合微信绑定）

**接口地址：** `POST /api/user/register`

**请求参数：**
```json
{
  "username": "用户名",
  "password": "密码",
  "email": "邮箱（可选）",
  "realName": "真实姓名（可选）",
  "openid": "微信openid（可选，用于自动绑定）",
  "unionid": "微信unionid（可选）",
  "sessionKey": "微信sessionKey（可选，一般不需要前端传）"
}
```

**响应示例（注册成功且绑定成功）：**
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 2,
    "username": "lisi",
    "userType": 1,
    "realName": "李四",
    "email": "lisi@example.com",
    "wechatBound": true,
    "openid": "oXYZ987654321",
    "unionid": null
  }
}
```

### 4. 独立微信绑定接口（可选）

如果用户已经登录，想要单独绑定微信，可以使用原有的绑定接口。

**接口地址：** `POST /api/wx/bind`

**请求参数：**
```json
{
  "userId": 1,
  "openid": "微信openid",
  "unionid": "微信unionid（可选）",
  "sessionKey": "微信sessionKey（可选）",
  "platform": "mini_program"
}
```

## 前端实现示例

### 小程序端代码示例

```javascript
// 1. 微信登录
async function wechatLogin() {
  try {
    // 获取微信登录凭证
    const { code } = await wx.login();
    
    // 调用后端接口
    const response = await wx.request({
      url: 'https://your-api.com/api/wx/login',
      method: 'POST',
      data: { code }
    });
    
    const { needBind, token, openid, unionid } = response.data.data;
    
    if (!needBind) {
      // 已绑定，保存token，跳转主页
      wx.setStorageSync('token', token);
      wx.redirectTo({ url: '/pages/index/index' });
    } else {
      // 未绑定，保存openid/unionid，跳转登录注册页
      wx.setStorageSync('wechat_openid', openid);
      wx.setStorageSync('wechat_unionid', unionid);
      wx.redirectTo({ url: '/pages/login/login' });
    }
  } catch (error) {
    console.error('微信登录失败', error);
    wx.showToast({ title: '登录失败', icon: 'none' });
  }
}

// 2. 用户登录（带微信绑定）
async function userLogin(username, password) {
  try {
    const openid = wx.getStorageSync('wechat_openid');
    const unionid = wx.getStorageSync('wechat_unionid');
    
    const response = await wx.request({
      url: 'https://your-api.com/api/user/login',
      method: 'POST',
      data: {
        username,
        password,
        openid,  // 携带openid自动绑定
        unionid
      }
    });
    
    const { token, wechatBound, bindWarning } = response.data.data;
    
    // 保存token
    wx.setStorageSync('token', token);
    
    // 清除临时存储的微信信息
    wx.removeStorageSync('wechat_openid');
    wx.removeStorageSync('wechat_unionid');
    
    if (wechatBound) {
      wx.showToast({ title: '登录成功，微信已绑定', icon: 'success' });
    } else if (bindWarning) {
      wx.showToast({ title: bindWarning, icon: 'none' });
    }
    
    // 跳转主页
    wx.redirectTo({ url: '/pages/index/index' });
  } catch (error) {
    console.error('登录失败', error);
    wx.showToast({ title: '登录失败', icon: 'none' });
  }
}

// 3. 用户注册（带微信绑定）
async function userRegister(username, password, email, realName) {
  try {
    const openid = wx.getStorageSync('wechat_openid');
    const unionid = wx.getStorageSync('wechat_unionid');
    
    const response = await wx.request({
      url: 'https://your-api.com/api/user/register',
      method: 'POST',
      data: {
        username,
        password,
        email,
        realName,
        openid,  // 携带openid自动绑定
        unionid
      }
    });
    
    const { token, wechatBound } = response.data.data;
    
    // 保存token
    wx.setStorageSync('token', token);
    
    // 清除临时存储的微信信息
    wx.removeStorageSync('wechat_openid');
    wx.removeStorageSync('wechat_unionid');
    
    if (wechatBound) {
      wx.showToast({ title: '注册成功，微信已绑定', icon: 'success' });
    }
    
    // 跳转主页
    wx.redirectTo({ url: '/pages/index/index' });
  } catch (error) {
    console.error('注册失败', error);
    wx.showToast({ title: '注册失败', icon: 'none' });
  }
}
```

## 安全注意事项

1. **sessionKey保护**：sessionKey不应该从前端传递，微信登录时后端已经保存
2. **openid验证**：后端会检查openid是否已被其他用户绑定
3. **绑定失败不影响主流程**：即使微信绑定失败，用户的登录/注册操作仍然会成功
4. **Token安全**：建议使用HTTPS传输，并设置合理的token过期时间

## 常见问题

### Q1: 用户已经登录，想要绑定微信怎么办？

A: 可以调用原有的 `/api/wx/bind` 接口进行独立绑定。

### Q2: 如果微信已经绑定了其他账号，用户登录会怎样？

A: 用户仍然可以正常登录，但不会绑定微信，响应中会包含 `bindWarning` 字段提示用户。

### Q3: sessionKey需要前端传吗？

A: 不需要。微信登录时后端已经获取并保存了sessionKey，登录/注册绑定时会自动使用。

### Q4: 如何解绑微信？

A: 可以在后端添加解绑接口，将 `user_wechat_auth` 表中的 `bind_status` 设置为 0。

## 数据库表结构

### user_wechat_auth 表

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID（外键关联user表） |
| platform | VARCHAR | 平台类型（mini_program） |
| openid | VARCHAR | 微信openid |
| unionid | VARCHAR | 微信unionid（可空） |
| session_key | VARCHAR | 微信session_key |
| bind_status | TINYINT | 绑定状态（0=未绑定，1=已绑定） |
| bind_time | DATETIME | 绑定时间 |
| last_login_time | DATETIME | 最后登录时间 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

## 测试建议

1. 测试正常流程：微信登录 → 注册绑定 → 再次微信登录（应直接进入）
2. 测试已有账号：微信登录 → 用户登录绑定 → 再次微信登录
3. 测试重复绑定：一个微信绑定账号A后，尝试在账号B登录时绑定同一个微信
4. 测试不绑定场景：用户选择不提供openid，正常注册/登录

## 更新日志

- 2026-01-15: 完成登录注册与微信绑定的整合
