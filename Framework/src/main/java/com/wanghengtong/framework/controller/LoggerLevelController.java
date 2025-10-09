package com.wanghengtong.framework.controller;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanghengtong.framework.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Set;

/**
 * @author wanghengtong
 * @desc 修改日志级别
 */
@RestController
@RequestMapping("/log")
@Slf4j
public class LoggerLevelController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Set<String> ALLOWED_LOG_LEVELS = new HashSet<String>() {{
        add("ALL");
        add("TRACE");
        add("DEBUG");
        add("INFO");
        add("WARN");
        add("ERROR");
        add("OFF");
    }};

    @RequestMapping("/updateLevel")
    public ApiResponse<String> updateLevel(HttpServletRequest request) {
        try {
            String logLevel = request.getParameter("logLevel");
            if (logLevel == null) {
                return ApiResponse.error("日志级别为空！");
            }
            if (!ALLOWED_LOG_LEVELS.contains(logLevel.toUpperCase())) {
                return ApiResponse.error("日志级别错误！");
            }
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger logger = loggerContext.getLogger("root");
            logger.setLevel(Level.toLevel(logLevel.toUpperCase()));
            log.info("日志级别已修改为：{}", logger.getLevel());
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

}
