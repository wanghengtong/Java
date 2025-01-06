package com.wanghengtong.framework.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wanghengtong.framework.common.ApiResponse;
import com.wanghengtong.framework.service.ICacheService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月27日 21:23
 */
@RequestMapping("/api/integration")
@RestController
public class IntegrationCacheController {

    @Autowired
    private ICacheService cacheService;

    @RequestMapping("/set")
    public ApiResponse<String> set(@RequestBody ObjectNode objectNode) {
        cacheService.set(objectNode.get("key").asText(), objectNode.get("val").asText());
        return ApiResponse.success();
    }

    @RequestMapping("/setExp")
    public ApiResponse<String> setExp(@RequestBody ObjectNode objectNode) {
        cacheService.set(objectNode.get("key").asText(), objectNode.get("val").asText(), objectNode.get("expire").asLong());
        return ApiResponse.success();
    }


    @GetMapping("/get")
    public ApiResponse<String> get(@RequestParam String key) {
        if (StringUtils.isBlank(key)) {
            return ApiResponse.success();
        }
        String value = (String) cacheService.get(key);
        return ApiResponse.success(value);
    }

    @GetMapping("/delete")
    public ApiResponse<String> delete(@RequestParam String key) {
        if (StringUtils.isBlank(key)) {
            return ApiResponse.success();
        }
        cacheService.delete(key);
        return ApiResponse.success();
    }

}
