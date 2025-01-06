package com.wanghengtong.framework.service.impl;

import com.wanghengtong.framework.service.ICacheStrategy;
import com.wanghengtong.framework.utils.LocalCacheUtils;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月27日 14:10
 */
public class LocalCacheStrategy implements ICacheStrategy {

    /**
     * @param key
     * @param value
     */
    @Override
    public void set(String key, Object value) {
        LocalCacheUtils.set(key, value);
    }

    /**
     * @param key
     * @param value
     * @param expire
     */
    @Override
    public void set(String key, Object value, Long expire) {
        LocalCacheUtils.set(key, value, expire);
    }

    /**
     * @param key
     * @return
     */
    @Override
    public Object get(String key) {
        return LocalCacheUtils.get(key);
    }

    /**
     * @param key
     */
    @Override
    public void delete(String key) {
        LocalCacheUtils.delete(key);
    }

}
