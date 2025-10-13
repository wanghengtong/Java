package com.wanghengtong.framework.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wanghengtong.framework.common.ApiResponse;
import com.wanghengtong.framework.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月27日 21:23
 */
@RequestMapping("/api/cache")
@RestController
public class RedisCacheController {

    @Autowired
    private RedisUtils redisUtils;

    @RequestMapping("/set")
    public ApiResponse<String> test(@RequestBody ObjectNode objectNode) {
        // 参数校验
        if (objectNode == null || !objectNode.has("key") || !objectNode.has("val")) {
            return ApiResponse.error("参数不完整");
        }

        // 获取并校验key和value
        String key = objectNode.get("key").asText();
        String val = objectNode.get("val").asText();

        if (key == null || key.isEmpty() || val == null) {
            return ApiResponse.error("参数不能为空");
        }

        boolean result = redisUtils.set(key, val);
        return result ? ApiResponse.success() : ApiResponse.error("缓存设置失败！");
    }


    @GetMapping("/get")
    public ApiResponse<String> get(@RequestParam String key) {
        if (StringUtils.isBlank(key)) {
            return ApiResponse.error("参数key不能为空！");
        }
        try {
            Object value = redisUtils.get(key);
            if (value == null) {
                return ApiResponse.success();
            }
            if (!(value instanceof String)) {
                return ApiResponse.error("缓存值类型不正确！");
            }
            return ApiResponse.success((String) value);
        } catch (Exception e) {
            return ApiResponse.error("获取缓存失败: " + e.getMessage());
        }
    }

    @GetMapping("/delete")
    public ApiResponse<String> delete(@RequestParam String key) {
        if (StringUtils.isBlank(key)) {
            return ApiResponse.error("参数key不能为空！");
        }
        try {
            redisUtils.delete(key);
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error("删除缓存失败: " + e.getMessage());
        }
    }

}
