package com.wanghengtong.framework.service;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月27日 21:08
 */
public interface ICacheStrategy {

    void set(String key, Object value);

    void set(String key, Object value, Long expire);

    Object get(String key);

    void delete(String key);

}
