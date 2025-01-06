package com.wanghengtong.framework.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月26日 22:04
 */
public class ObjectMapperFactory {

    // 提供一个静态方法获取实例
    // 饿汉式单例模式，类加载时就创建实例
    @Getter
    private static final ObjectMapperFactory INSTANCE = new ObjectMapperFactory();

    // 提供一个方法获取 ObjectMapper 实例
    @Getter
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 私有构造函数，防止外部实例化
    private ObjectMapperFactory() {

    }

}
