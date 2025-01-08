package com.wanghengtong.jobhandler.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author wanghengtong
 * @desc
 * @date 2025年01月08日 21:37
 */
@Slf4j
@Component
public class MyJobHandler {

    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob(value = "demoJobHandler", init = "init", destroy = "destroy")
    public ReturnT<String> demoJobHandler() throws Exception {
        log.info("XXL-JOB, Hello World!" + LocalDateTime.now());
        return ReturnT.SUCCESS;
    }

    /**
     * 2、带参数的任务示例（Bean模式）
     */
    @XxlJob(value = "paramJobHandler", init = "init", destroy = "destroy")
    public ReturnT<String> paramJobHandler(String param) throws Exception {
        log.info("XXL-JOB, Hello World!" + LocalDateTime.now());
        log.info("XXL-JOB, param: " + param);
        return ReturnT.SUCCESS;
    }

    /**
     * 3、阻塞任务示例（Bean模式）
     * 阻塞任务会导致调度线程阻塞，直到任务执行完成，适合耗时任务。
     */
    @XxlJob(value = "blockJobHandler", init = "init", destroy = "destroy")
    public ReturnT<String> blockJobHandler() throws Exception {
        log.info("XXL-JOB, Hello World!" + LocalDateTime.now());
        TimeUnit.SECONDS.sleep(3);
        log.info("XXL-JOB, Block job executed.");
        return ReturnT.SUCCESS;
    }

    /**
     * 4、初始化方法
     */
    public void init() {
        log.info("XXL-JOB,init调用成功.");
    }

    /**
     * 5、销毁方法
     */
    public void destroy() {
        log.info("XXL-JOB,destroy调用成功.");
    }

}