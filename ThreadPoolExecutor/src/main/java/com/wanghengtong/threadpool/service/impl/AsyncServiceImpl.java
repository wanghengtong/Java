package com.wanghengtong.threadpool.service.impl;

import com.wanghengtong.threadpool.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {

    @Autowired
    @Qualifier("asyncTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Async
    @Override
    public void executeAsyncTask(Runnable task) {
        taskExecutor.execute(task);
    }

}
