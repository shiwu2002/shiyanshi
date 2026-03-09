package com.example.shiyanshi.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置类
 * 用于分布式锁、限流等并发控制场景
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.database:0}")
    private int redisDatabase;

    /**
     * 配置 RedissonClient
     * 使用单节点模式（生产环境可配置集群或哨兵模式）
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        
        // 构建 Redis 地址
        String address = String.format("redis://%s:%d", redisHost, redisPort);
        
        // 配置单节点模式
        config.useSingleServer()
              .setAddress(address)
              .setDatabase(redisDatabase)
              .setConnectionMinimumIdleSize(10)
              .setConnectionPoolSize(64);
        
        // 如果有密码，设置密码
        if (redisPassword != null && !redisPassword.isEmpty()) {
            config.useSingleServer().setPassword(redisPassword);
        }
        
        return Redisson.create(config);
    }
}
