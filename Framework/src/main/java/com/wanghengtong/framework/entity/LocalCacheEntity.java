package com.wanghengtong.framework.entity;

import lombok.Data;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月27日 23:52
 */
@Data
public class LocalCacheEntity {

    // 缓存键
    private String key;

    // 缓存键
    private Object value;

    // 过期时间
    private Long expireTime;

}
