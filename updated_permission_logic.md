# 更新后的用户信息修改接口权限逻辑

## 修改内容
已根据用户反馈优化UserController.java中的用户信息更新接口权限逻辑。

## 新的权限逻辑

### 总体原则
1. **用户权限(userType)字段**：只能由超级管理员修改
2. **其他用户信息字段**（如姓名、邮箱、电话等）：可以由用户自己或超级管理员修改

### 具体实现

#### 1. 权限检查层次
- **第一层**：`@RequireSelfOrAdmin`注解检查
  - 验证当前用户是否可以操作目标用户
  - 允许：超级管理员（userType=3）或用户自己
  - 拒绝：其他情况

- **第二层**：字段级别权限检查
  - 在方法内部检查是否尝试修改`userType`字段
  - 如果当前用户不是超级管理员，但尝试修改`userType`字段，则拒绝操作

#### 2. 代码实现细节

```java
@RequireSelfOrAdmin(idParam = "id", adminLevel = 3, description = "更新用户信息需要自己操作或超级管理员权限")
@PutMapping("/{id}")
public Result<Void> update(@PathVariable Long id, @RequestBody User user, HttpServletRequest request) {
    try {
        // 确保更新的是指定ID的用户
        user.setId(id);
        
        // 获取当前用户信息
        Object currentUserTypeObj = request.getAttribute("userType");
        Integer currentUserType = null;
        if (currentUserTypeObj != null) {
            currentUserType = (currentUserTypeObj instanceof Integer) ? (Integer) currentUserTypeObj 
                : Integer.valueOf(currentUserTypeObj.toString());
        }
        
        // 权限字段安全检查：只有超级管理员可以修改用户权限(userType)
        if (user.getUserType() != null) {
            // 当前用户不是超级管理员，但尝试修改用户权限
            if (currentUserType == null || currentUserType < 3) {
                // 查找现有用户的权限信息
                User existingUser = userService.findById(id);
                if (existingUser != null && !existingUser.getUserType().equals(user.getUserType())) {
                    return Result.error("权限不足：只有超级管理员可以修改用户权限");
                }
            }
        }
        
        userService.update(user);
        return Result.success("更新成功", null);
    } catch (Exception e) {
        return Result.error(e.getMessage());
    }
}
```

## 测试场景

### 场景1：用户修改自己的普通信息（应该成功）
- 当前登录用户ID：1001，用户类型：1（学生）
- 目标用户ID：1001
- 修改字段：realName = "张三"，email = "zhangsan@example.com"
- 不修改userType字段
- 预期结果：权限验证通过

### 场景2：用户尝试修改自己的权限（应该失败）
- 当前登录用户ID：1001，用户类型：1（学生）
- 目标用户ID：1001
- 修改字段：userType = 2（尝试将自己升级为管理员）
- 预期结果：权限验证失败，返回"权限不足：只有超级管理员可以修改用户权限"

### 场景3：超级管理员修改用户权限（应该成功）
- 当前登录用户ID：9999，用户类型：3（超级管理员）
- 目标用户ID：1001
- 修改字段：userType = 2（将用户升级为管理员）
- 预期结果：权限验证通过

### 场景4：超级管理员修改用户普通信息（应该成功）
- 当前登录用户ID：9999，用户类型：3（超级管理员）
- 目标用户ID：1001
- 修改字段：realName = "李四"
- 预期结果：权限验证通过

### 场景5：用户修改其他用户信息（应该失败）
- 当前登录用户ID：1001，用户类型：1（学生）
- 目标用户ID：1002
- 修改任何字段
- 预期结果：`@RequireSelfOrAdmin`注解检查失败，权限不足

### 场景6：管理员修改用户信息（应该失败）
- 当前登录用户ID：2001，用户类型：2（管理员）
- 目标用户ID：1001
- 修改任何字段
- 预期结果：`@RequireSelfOrAdmin`注解检查失败，权限不足（管理员权限级别2 < 3）

## 技术要点

### 1. 双重权限检查
- 注解级别：检查操作权限（谁可以操作）
- 方法级别：检查字段权限（可以操作哪些字段）

### 2. 智能权限验证
- 只有当用户尝试修改`userType`字段时才进行额外检查
- 如果`userType`字段为null或不改变现有值，允许操作
- 只有当`userType`字段有变化且当前用户不是超级管理员时才拒绝

### 3. 性能考虑
- 只有在需要时才查询现有用户信息
- 避免不必要的数据库查询

## 验证结果
项目已成功编译，无语法错误。新的权限逻辑满足用户需求：
- ✅ 超级管理员可以修改所有用户的所有字段
- ✅ 用户可以修改自己的普通信息（非权限字段）
- ❌ 用户不能修改自己的权限
- ❌ 用户不能修改其他用户的信息
- ❌ 管理员不能修改用户信息

## 扩展性
这种设计具有良好的扩展性，未来可以：
1. 添加更多受限制的字段（如status状态字段）
2. 支持不同级别的管理员权限
3. 实现更复杂的字段级权限控制
