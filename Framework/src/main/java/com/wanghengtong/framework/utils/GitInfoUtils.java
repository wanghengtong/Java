package com.wanghengtong.framework.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanghengtong.framework.exception.ResourceNotFoundException;
import com.wanghengtong.framework.factory.ObjectMapperFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Properties;

/**
 * @author wanghengtong
 * @desc 获取组件版本信息
 * @date 2024年12月26日 21:24
 */
public class GitInfoUtils {

    public static JsonNode getGitInfo() {
        try {
            Resource resource = new ClassPathResource("git.properties");
            Properties properties = new Properties();
            properties.load(resource.getInputStream());
            ObjectMapper objectMapper = ObjectMapperFactory.getINSTANCE().getObjectMapper();
            return objectMapper.readTree(objectMapper.writeValueAsString(properties));
        } catch (IOException e) {
            throw new ResourceNotFoundException("Failed to read git.properties file");
        }
    }

}
