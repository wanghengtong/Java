package com.wanghengtong.framework.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("/api/config")
public class NacosConfigController {

    @Value("${logging.level.root}")
    private String logLevel;

    @RequestMapping("/logLevel")
    public String getLogLevel() {
        return "当前日志级别: " + logLevel;
    }

}