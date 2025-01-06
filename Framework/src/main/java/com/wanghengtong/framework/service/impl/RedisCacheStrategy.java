package com.wanghengtong.framework.service.impl;

import com.wanghengtong.framework.service.ICacheStrategy;
import com.wanghengtong.framework.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月27日 14:09
 */
@Component
public class RedisCacheStrategy implements ICacheStrategy {

    private final RedisUtils redisUtils;

    @Autowired
    public RedisCacheStrategy(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    /**
     * @param key
     * @param value
     */
    @Override
    public void set(String key, Object value) {
        redisUtils.set(key, value);
    }

    /**
     * @param key
     * @param value
     * @param expire
     */
    @Override
    public void set(String key, Object value, Long expire) {
        redisUtils.set(key, value, expire);
    }

    /**
     * @param key
     * @return
     */
    @Override
    public Object get(String key) {
        return redisUtils.get(key);
    }

    /**
     * @param key
     */
    @Override
    public void delete(String key) {
        redisUtils.delete(key);
    }

}
