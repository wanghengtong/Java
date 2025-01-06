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
        redisUtils.set(objectNode.get("key").asText(), objectNode.get("val").asText());
        return ApiResponse.success();
    }


    @GetMapping("/get")
    public ApiResponse<String> get(@RequestParam String key) {
        if (StringUtils.isBlank(key)) {
            return ApiResponse.success();
        }
        String value = (String) redisUtils.get(key);
        return ApiResponse.success(value);
    }

    @GetMapping("/delete")
    public ApiResponse<String> delete(@RequestParam String key) {
        if (StringUtils.isBlank(key)) {
            return ApiResponse.success();
        }
        redisUtils.delete(key);
        return ApiResponse.success();
    }

}
