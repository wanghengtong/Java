package com.wanghengtong.framework.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wanghengtong.framework.entity.User;
import com.wanghengtong.framework.factory.ObjectMapperFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author wanghengtong
 * @desc
 * @date 2024年12月26日 20:17
 */
@Slf4j
public class RunnableTask implements Runnable {

    private List<User> users;

    public RunnableTask(List<User> users) {
        this.users = users;
    }

    @Override
    public void run() {
        log.info("【{}】线程正在运行......", Thread.currentThread().getName());
        for (User user : users) {
            try {
                log.info(ObjectMapperFactory.getINSTANCE().getObjectMapper().writeValueAsString(user));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
