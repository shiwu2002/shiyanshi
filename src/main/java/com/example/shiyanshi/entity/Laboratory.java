package com.example.shiyanshi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 实验室实体类
 */
@Data
@TableName("laboratory")
public class Laboratory {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String labName;          // 实验室名称
    private String labNumber;        // 实验室编号
    private String location;         // 位置
    private String building;         // 楼栋
    private String floor;            // 楼层
    private Integer capacity;        // 容纳人数
    private String equipment;        // 设备清单（JSON格式）
    private String description;      // 实验室描述
    private String labType;          // 实验室类型：计算机/物理/化学/生物等
    private Integer status;          // 状态：0-停用 1-正常 2-维护中
    private String manager;          // 负责人
    private String managerPhone;     // 负责人电话
    private String images;           // 实验室图片（多张，逗号分隔）
    private String openTime;         // 开放时间说明
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
