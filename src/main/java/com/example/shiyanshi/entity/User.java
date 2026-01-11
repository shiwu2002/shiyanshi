package com.example.shiyanshi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String username;      // 用户名/学号/工号
    private String password;      // 密码
    private String realName;      // 真实姓名
    private String phone;         // 手机号
    private String email;         // 邮箱
    private Integer userType;     // 用户类型：1-学生 2-教师 3-管理员
    private String college;       // 学院
    private String major;         // 专业
    private String studentId;     // 学号（学生）
    private String teacherId;     // 工号（教师）
    private Integer status;       // 状态：0-禁用 1-正常
    private Integer emailVerified; // 邮箱验证状态：0-未验证 1-已验证
    private String avatar;        // 头像
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
