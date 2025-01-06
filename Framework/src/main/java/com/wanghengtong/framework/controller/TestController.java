package com.wanghengtong.framework.controller;

import com.wanghengtong.framework.common.ApiResponse;
import com.wanghengtong.framework.entity.User;
import com.wanghengtong.framework.exception.BusinessException;
import com.wanghengtong.framework.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author wanghengtong
 * @desc 测试控制器
 * @date 2024年12月25日 21:49
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> getDataById(@PathVariable String id) {
        if ("invalid".equals(id)) {
            throw new ResourceNotFoundException("未找到对应的数据");
        }
        return ResponseEntity.ok(ApiResponse.success("找到了数据: " + id));
    }

    @GetMapping("/business")
    public ApiResponse<String> business() {
        throw new BusinessException("业务处理异常");
    }

    @GetMapping("/error")
    public ApiResponse<String> error() {
        throw new RuntimeException("未知异常");
    }

    @PostMapping("/info")
    public ApiResponse<User> info(@RequestBody User user) {
        return ApiResponse.success(user);
    }

    @PostMapping("/user")
    public User user(@RequestBody User user) {
        return user;
    }

    @RequestMapping("/int")
    public int testInt() {
        return 1;
    }

    @RequestMapping("/string")
    public String testString() {
        return "1";
    }

    @RequestMapping("/bol")
    public Boolean testBol() {
        return Boolean.TRUE;
    }

}
