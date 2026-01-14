package com.example.shiyanshi.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户与微信等第三方平台的绑定关系实体
 *
 * 设计目标：
 * - 支持多平台/多账号绑定（如微信小程序/公众号/企业微信等）
 * - 与业务用户（user表）解耦，避免在 user 表直接塞 openid/unionid
 * - 便于扩展和审计（记录绑定时间、最近登录时间等）
 *
 * 建议对应数据表：user_wechat_auth
 */
@Data
@TableName("user_wechat_auth")
public class UserWechatAuth {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务用户ID（user表主键）
     */
    private Long userId;

    /**
     * 平台标识：
     * - mini_program（微信小程序）
     * - mp（微信公众号）
     * - enterprise_wechat（企业微信）
     * 也可根据需求扩展
     */
    private String platform;

    /**
     * 微信openid（平台下的唯一用户标识）
     */
    private String openid;

    /**
     * 微信unionid（统一身份标识，某些情况下可能为空）
     */
    private String unionid;

    /**
     * 最近一次会话密钥（可选，仅用于调试或校验，不建议长期存储）
     */
    private String sessionKey;

    /**
     * 绑定状态：0-未绑定 1-已绑定
     */
    private Integer bindStatus;

    /**
     * 最近登录时间（通过第三方登录成功后更新）
     */
    private LocalDateTime lastLoginTime;

    /**
     * 是否删除：0-未删除 1-已删除
     */
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
