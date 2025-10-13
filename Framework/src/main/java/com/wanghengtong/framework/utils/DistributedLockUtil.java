package com.wanghengtong.framework.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁工具类
 *
 * @author wanghengtong
 */
@Slf4j
@Component
public class DistributedLockUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String RELEASE_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    /**
     * 尝试获取分布式锁
     *
     * @param lockKey    锁的key
     * @param expireTime 锁的过期时间(毫秒)
     * @return 锁标识，用于释放锁
     */
    public String tryLock(String lockKey, long expireTime) {
        try {
            String lockValue = UUID.randomUUID().toString();
            Boolean result = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, expireTime, TimeUnit.MILLISECONDS);
            return Boolean.TRUE.equals(result) ? lockValue : null;
        } catch (Exception e) {
            log.error("获取分布式锁失败", e);
            return null;
        }
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey   锁的key
     * @param lockValue 锁的值
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String lockValue) {
        try {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(RELEASE_LOCK_SCRIPT);
            redisScript.setResultType(Long.class);
            Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockKey), lockValue);
            return Long.valueOf(1).equals(result);
        } catch (Exception e) {
            log.error("释放分布式锁失败", e);
            return false;
        }
    }
    
}