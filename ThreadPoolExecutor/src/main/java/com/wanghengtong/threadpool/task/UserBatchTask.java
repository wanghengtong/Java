package com.wanghengtong.threadpool.task;

import com.wanghengtong.threadpool.entity.UserDO;
import com.wanghengtong.threadpool.service.IUserService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class UserBatchTask implements Runnable {

    List<UserDO> users;

    IUserService userService;

    public UserBatchTask(IUserService userService, List<UserDO> users) {
        this.userService = userService;
        this.users = users;
    }

    @Override
    public void run() {
        log.info("【{}】线程正在运行......", Thread.currentThread().getName());
        userService.saveBatch(users);
    }

}
