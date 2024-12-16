package com.wanghengtong.threadpool.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@Configuration
public class ThreadPoolConfig {

    @Value("${async.executor.thread.core_pool_size}")
    private int corePoolSize;

    @Value("${async.executor.thread.max_pool_size}")
    private int maxPoolSize;

    @Value("${async.executor.thread.queue_capacity}")
    private int queueCapacity;

    @Value("${async.executor.thread.keep_alive_seconds}")
    private int keepAliveSeconds;

    @Value("${async.executor.thread.name_prefix}")
    private String namePrefix;


    @Bean(name = "asyncTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        int cpuNum = Runtime.getRuntime().availableProcessors();
        System.out.println("CPU核数:" + cpuNum);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cpuNum);
        executor.setMaxPoolSize(cpuNum * 2);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix(namePrefix);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

}