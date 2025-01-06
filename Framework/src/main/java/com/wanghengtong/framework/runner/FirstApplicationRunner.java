package com.wanghengtong.framework.runner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author wanghengtong
 * @desc FirstApplicationRunner
 * @date 2024年12月26日 16:54
 */
@Slf4j
@Order(1)
@Component
public class FirstApplicationRunner implements ApplicationRunner {

    /**
     * @desc 服务启动后执行
     * @param args
     * @throws Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("FirstApplicationRunner start...");
    }

}
