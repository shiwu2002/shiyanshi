package com.example.shiyanshi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.shiyanshi.entity.UserWechatAuth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户与微信绑定关系表 Mapper
 *
 * 表：user_wechat_auth
 * 提供基础CRUD与按 openid/platform 查询能力
 */
@Mapper
public interface UserWechatAuthMapper extends BaseMapper<UserWechatAuth> {

    /**
     * 根据平台与openid查询绑定记录
     */
    @Select("SELECT * FROM user_wechat_auth WHERE platform = #{platform} AND openid = #{openid} AND deleted = 0 LIMIT 1")
    UserWechatAuth findByPlatformAndOpenid(@Param("platform") String platform, @Param("openid") String openid);

    /**
     * 根据用户ID与平台查询绑定记录
     */
    @Select("SELECT * FROM user_wechat_auth WHERE user_id = #{userId} AND platform = #{platform} AND deleted = 0 LIMIT 1")
    UserWechatAuth findByUserIdAndPlatform(@Param("userId") Long userId, @Param("platform") String platform);
}
