package com.wanghengtong.framework.service;

/**
 * @author wanghengtong
 * @desc    缓存服务
 * @date 2024年12月27日 23:55
 */
public interface ICacheService {

    void set(String key, Object value);

    void set(String key, Object value, Long expire);

    Object get(String key);

    void delete(String key);

}
