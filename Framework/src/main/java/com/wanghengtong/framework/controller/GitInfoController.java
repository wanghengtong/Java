package com.wanghengtong.framework.controller;

import com.wanghengtong.framework.common.ApiResponse;
import com.wanghengtong.framework.utils.GitInfoUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wanghengtong
 * @desc 获取Git信息
 * @date 2024年12月26日 21:46
 */
@RestController
@RequestMapping("/api/git")
public class GitInfoController {

    @GetMapping("/info")
    public ApiResponse<Object> getGitInfo() {
        return ApiResponse.success(GitInfoUtils.getGitInfo());
    }

}
