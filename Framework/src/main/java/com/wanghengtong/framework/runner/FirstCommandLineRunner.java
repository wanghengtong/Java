package com.wanghengtong.framework.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author wanghengtong
 * @desc FirstCommandLineRunner
 * @date 2024年12月26日 21:59
 */
@Slf4j
@Order(1)
@Component
public class FirstCommandLineRunner implements CommandLineRunner {

    /**
     * @desc 服务启动时执行
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        log.info("FirstCommandLineRunner start...");
    }

}
