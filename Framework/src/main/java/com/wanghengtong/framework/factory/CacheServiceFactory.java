package com.wanghengtong.framework.factory;

import com.wanghengtong.framework.config.CacheConfig;
import com.wanghengtong.framework.constant.CacheTypeConstant;
import com.wanghengtong.framework.service.ICacheStrategy;
import com.wanghengtong.framework.service.impl.LocalCacheStrategy;
import com.wanghengtong.framework.service.impl.RedisCacheStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author wanghengtong
 * @desc 单例工厂类，用于创建 CacheService 实例
 * @date 2024年12月27日 21:12
 */
@Slf4j
@Component
public class CacheServiceFactory {

    private final CacheConfig cacheConfig;

    private final RedisCacheStrategy redisCacheStrategy;

    private static final AtomicReference<ICacheStrategy> INSTANCE = new AtomicReference<>();

    @Autowired
    public CacheServiceFactory(CacheConfig cacheConfig, RedisCacheStrategy redisCacheStrategy) {
        this.cacheConfig = cacheConfig;
        this.redisCacheStrategy = redisCacheStrategy;
    }

    public ICacheStrategy getInstance() {
        ICacheStrategy currentInstance = INSTANCE.get();
        if (currentInstance == null) {
            synchronized (CacheServiceFactory.class) {
                currentInstance = INSTANCE.get();
                if (currentInstance == null) {
                    currentInstance = createCacheService();
                    INSTANCE.set(currentInstance);
                }
            }
        }
        return currentInstance;
    }

    private ICacheStrategy createCacheService() {
        String type = cacheConfig.getType();
        ICacheStrategy cacheStrategy;
        switch (type) {
            case CacheTypeConstant.REDIS:
                cacheStrategy = redisCacheStrategy;
                break;
            case CacheTypeConstant.LOCAL:
                cacheStrategy = new LocalCacheStrategy();
                break;
            default:
                log.warn("Unknown cache type: {}, defaulting to LocalCacheStrategy", type);
                cacheStrategy = new LocalCacheStrategy();
                break;
        }
        log.info("Cache strategy created: {}", cacheStrategy.getClass().getSimpleName());
        return cacheStrategy;
    }

}