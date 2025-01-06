package com.wanghengtong.framework.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author wanghengtong
 * @desc SystemJob
 * @date 2024年12月26日 22:49
 */
@Slf4j
@Component
public class SystemJob {

    @Scheduled(cron = "0 * * * * ?")
    public void execute() {
        log.info("SystemJob executing...");
    }

}
