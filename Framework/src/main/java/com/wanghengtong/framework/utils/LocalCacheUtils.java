package com.wanghengtong.framework.utils;

import com.wanghengtong.framework.entity.LocalCacheEntity;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月27日 23:52
 */
@Slf4j
public class LocalCacheUtils {

    // 缓存数据
    private final static Map<String, LocalCacheEntity> CACHE_MAP = new ConcurrentHashMap<>();

    // 定时器线程池，用于清理过期缓存
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    static {
        // 注册一个定时任务，服务启动 1000 毫秒后，每隔 500 毫秒执行一次
        Runnable task = LocalCacheUtils::clear;
        executorService.scheduleAtFixedRate(task, 1000L, 500L, TimeUnit.MILLISECONDS);
    }

    // 添加缓存
    public static void set(String key, Object value) {
        set(key, value, 0L);
    }

    // 添加缓存
    public static void set(String key, Object value, Long expire) {
        LocalCacheEntity cacheEntity = new LocalCacheEntity();
        cacheEntity.setKey(key);
        cacheEntity.setValue(value);
        if (expire > 0) {
            // 计算过期时间
            Long expireTime = System.currentTimeMillis() + Duration.ofSeconds(expire).toMillis();
            cacheEntity.setExpireTime(expireTime);
        }
        CACHE_MAP.put(key, cacheEntity);
    }

    // 获取
    public static Object get(String key) {
        if (CACHE_MAP.containsKey(key)) {
            return CACHE_MAP.get(key).getValue();
        }
        return null;
    }

    // 删除
    public static void delete(String key) {
        CACHE_MAP.remove(key);
    }

    // 清除过期缓存
    public static void clear() {
        if (CACHE_MAP.isEmpty()) {
            return;
        }
        CACHE_MAP.entrySet().removeIf(entityEntry -> {
            boolean isExpired = entityEntry.getValue().getExpireTime() != null && entityEntry.getValue().getExpireTime() < System.currentTimeMillis();
            if (isExpired) {
                log.info("Removing expired cache key: {}", entityEntry.getKey());
            }
            return isExpired;
        });
    }

}
