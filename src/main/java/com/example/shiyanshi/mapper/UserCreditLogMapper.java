package com.example.shiyanshi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.shiyanshi.entity.UserCreditLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信誉分变动记录 Mapper 接口
 */
@Mapper
public interface UserCreditLogMapper extends BaseMapper<UserCreditLog> {
}
