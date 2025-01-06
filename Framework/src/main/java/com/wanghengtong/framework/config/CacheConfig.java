package com.wanghengtong.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月27日 21:20
 */
@Data
@Component
@ConfigurationProperties(prefix = "cache")
public class CacheConfig {

    private String type;

    private RedisConfig redis;

    private LocalCacheConfig local;

    @Data
    public static class RedisConfig {
        private String host;
        private Integer port;
        private String pwd;
        private Integer database;
    }

    @Data
    public static class LocalCacheConfig {
        private String period;
    }

}
