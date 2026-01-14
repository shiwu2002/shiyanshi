package com.example.shiyanshi.service;

import com.example.shiyanshi.entity.UserWechatAuth;
import com.example.shiyanshi.mapper.UserWechatAuthMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 微信绑定关系服务
 *
 * 能力：
 * - 根据 platform + openid 查询绑定记录
 * - 根据 userId + platform 查询绑定记录
 * - 绑定/解绑 openid
 * - 更新最近登录时间与会话密钥
 */
@Service
public class UserWechatAuthService {

    @Autowired
    private UserWechatAuthMapper userWechatAuthMapper;

    /**
     * 根据平台与openid查询绑定记录
     */
    public UserWechatAuth findByPlatformAndOpenid(String platform, String openid) {
        return userWechatAuthMapper.findByPlatformAndOpenid(platform, openid);
    }

    /**
     * 根据用户ID与平台查询绑定记录
     */
    public UserWechatAuth findByUserIdAndPlatform(Long userId, String platform) {
        return userWechatAuthMapper.findByUserIdAndPlatform(userId, platform);
    }

    /**
     * 进行绑定（若存在记录则更新；否则插入新记录）
     */
    public UserWechatAuth bind(Long userId, String platform, String openid, String unionid, String sessionKey) {
        UserWechatAuth existing = userWechatAuthMapper.findByPlatformAndOpenid(platform, openid);
        if (existing != null) {
            existing.setUserId(userId);
            existing.setUnionid(unionid);
            existing.setSessionKey(sessionKey);
            existing.setBindStatus(1);
            existing.setDeleted(0);
            existing.setUpdateTime(LocalDateTime.now());
            userWechatAuthMapper.updateById(existing);
            return existing;
        }

        UserWechatAuth record = new UserWechatAuth();
        record.setUserId(userId);
        record.setPlatform(platform);
        record.setOpenid(openid);
        record.setUnionid(unionid);
        record.setSessionKey(sessionKey);
        record.setBindStatus(1);
        record.setDeleted(0);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        userWechatAuthMapper.insert(record);
        return record;
    }

    /**
     * 解绑（逻辑删除或置 bindStatus 为未绑定）
     */
    public boolean unbind(Long userId, String platform) {
        UserWechatAuth existing = userWechatAuthMapper.findByUserIdAndPlatform(userId, platform);
        if (existing == null) {
            return false;
        }
        existing.setBindStatus(0);
        existing.setDeleted(1);
        existing.setUpdateTime(LocalDateTime.now());
        return userWechatAuthMapper.updateById(existing) > 0;
    }

    /**
     * 更新最近登录时间与会话密钥
     */
    public void touchLogin(String platform, String openid, String sessionKey) {
        UserWechatAuth existing = userWechatAuthMapper.findByPlatformAndOpenid(platform, openid);
        if (existing != null) {
            existing.setSessionKey(sessionKey);
            existing.setLastLoginTime(LocalDateTime.now());
            existing.setUpdateTime(LocalDateTime.now());
            userWechatAuthMapper.updateById(existing);
        }
    }
}
