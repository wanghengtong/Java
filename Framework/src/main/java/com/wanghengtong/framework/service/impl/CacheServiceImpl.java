package com.wanghengtong.framework.service.impl;

import com.wanghengtong.framework.service.ICacheService;
import com.wanghengtong.framework.factory.CacheServiceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月27日 13:56
 */
@Slf4j
@Service
public class CacheServiceImpl implements ICacheService {

    @Autowired
    private CacheServiceFactory cacheServiceFactory;

    /**
     * @param key
     * @param value
     */
    @Override
    public void set(String key, Object value) {
        cacheServiceFactory.getInstance().set(key, value);
    }

    /**
     * @param key
     * @param value
     * @param expire
     */
    @Override
    public void set(String key, Object value, Long expire) {
        cacheServiceFactory.getInstance().set(key, value, expire);
    }

    /**
     * @param key
     * @return
     */
    @Override
    public Object get(String key) {
        return cacheServiceFactory.getInstance().get(key);
    }

    /**
     * @param key
     */
    @Override
    public void delete(String key) {
        cacheServiceFactory.getInstance().delete(key);
    }

}
