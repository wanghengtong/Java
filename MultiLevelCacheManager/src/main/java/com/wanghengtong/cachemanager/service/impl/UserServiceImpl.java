package com.wanghengtong.cachemanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.wanghengtong.cachemanager.bean.User;
import com.wanghengtong.cachemanager.config.ObjectMapperFactory;
import com.wanghengtong.cachemanager.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author wanghengtong
 * @desc
 * @date 2025年06月23日 22:24
 */
@Service
public class UserServiceImpl implements IUserService {

    /**
     * 模拟数据库存储数据
     */
    private static final HashMap<String, User> USER_MAP = new HashMap<>();

    static {
        USER_MAP.put("1", new User("1", "zhangsan"));
        USER_MAP.put("2", new User("2", "lisi"));
        USER_MAP.put("3", new User("3", "wangwu"));
        USER_MAP.put("4", new User("4", "zhaoliu"));
    }

    private final RedisTemplate<String, Object> redisTemplate;

    private final Cache<String, Object> caffeineCache;

    @Autowired
    public UserServiceImpl(RedisTemplate<String, Object> redisTemplate, @Qualifier("localCacheManager") Cache<String, Object> caffeineCache) {
        this.redisTemplate = redisTemplate;
        this.caffeineCache = caffeineCache;
    }

    ObjectMapper objectMapper = ObjectMapperFactory.getINSTANCE().getObjectMapper();

    @Override
    public void add(User user) {
        // 1.保存Caffeine缓存
        caffeineCache.put(user.getId(), user);

        // 2.保存redis缓存
        try {
            redisTemplate.opsForValue().set(user.getId(), objectMapper.writeValueAsString(user), 20, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 3.保存数据库(模拟)
        USER_MAP.put(user.getId(), user);
    }

    @Override
    public User getById(String id) {
        // 1.先从Caffeine缓存中读取
        Object o = caffeineCache.getIfPresent(id);
        if (Objects.nonNull(o)) {
            System.out.println("从Caffeine中查询到数据...");
            return (User) o;
        }

        // 2.如果缓存中不存在，则从Redis缓存中查找
        User user = null;
        String jsonString = (String) redisTemplate.opsForValue().get(id);
        if (StringUtils.isNotEmpty(jsonString)) {
            try {
                user = objectMapper.readValue(jsonString, User.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        if (Objects.nonNull(user)) {
            System.out.println("从Redis中查询到数据...");

            // 保存Caffeine缓存
            caffeineCache.put(user.getId(), user);
            return user;
        }

        // 3.如果Redis缓存中不存在，则从数据库中查询
        user = USER_MAP.get(id);
        if (Objects.nonNull(user)) {
            // 保存Caffeine缓存
            caffeineCache.put(user.getId(), user);

            // 保存Redis缓存,20s后过期
            try {
                redisTemplate.opsForValue().set(user.getId(), objectMapper.writeValueAsString(user), 20, TimeUnit.SECONDS);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("从数据库中查询到数据...");
        return user;
    }

    @Override
    public User update(User user) {
        User oldUser = USER_MAP.get(user.getId());
        if (oldUser == null) {
            throw new IllegalArgumentException("Old user not found");
        }
        oldUser.setName(user.getName());
        // 1.更新数据库
        USER_MAP.put(oldUser.getId(), oldUser);

        // 2.更新Caffeine缓存
        caffeineCache.put(oldUser.getId(), oldUser);

        // 3.更新Redis数据库
        try {
            redisTemplate.opsForValue().set(oldUser.getId(), objectMapper.writeValueAsString(oldUser), 20, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return oldUser;
    }

    @Override
    public void deleteById(String id) {
        // 1.删除数据库
        USER_MAP.remove(id);

        // 2.删除Caffeine缓存
        caffeineCache.invalidate(id);

        // 3.删除Redis缓存
        redisTemplate.delete(id);
    }

}
