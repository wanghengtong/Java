package com.wanghengtong.logback.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
@Slf4j
public class LogcackApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        init();
    }

    /**
     * 服务启动后执行的初始化方法
     */
    private void init() {
        log.trace("TRACE：最详细的日志信息，主要用于开发和调试阶段，记录程序运行过程中的每一个细节。");
        log.debug("调试信息，比TRACE少一些，但仍然详细，用于开发过程中帮助开发者了解程序执行情况。");
        log.info("信息性消息，表示系统正常运行时的关键事件，如系统启动、服务初始化等。");
        log.warn("WARN：警告信息，表示潜在的问题，虽然当前没有造成错误，但可能需要关注。");
        log.error("ERROR：错误信息，表示程序运行中出现了异常或错误，但系统仍可继续运行。");
    }

}