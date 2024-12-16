package com.wanghengtong.threadpool.controller;

import cn.hutool.core.collection.ListUtil;
import com.wanghengtong.threadpool.entity.UserDO;
import com.wanghengtong.threadpool.service.IAsyncService;
import com.wanghengtong.threadpool.service.IUserService;
import com.wanghengtong.threadpool.task.UserBatchTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/async")
public class TestAsyncController {

    @Autowired
    private IAsyncService asyncService;

    @Autowired
    private IUserService userService;

    private static final int dataSize = 2000000;

    List<UserDO> userList = new ArrayList<>(dataSize);

    private static final int batchSize = 10000;

    Random rand = new Random();

    @RequestMapping("/asyncData1")
    public void asyncData1() {
        for (int i = 0; i < dataSize; i++) {
            UserDO user = new UserDO();
            user.setId(i);
            user.setName("小明：" + i);
            user.setAge(rand.nextInt(100));
            user.setMobile("151" + rand.nextInt(9) * 1000 + rand.nextInt(9) * 1000);
            user.setEmail(user.getMobile() + "@163.com");
            userList.add(user);
        }
        long startTime = System.currentTimeMillis();
        log.info("开始批量插入数据，开始时间：{}.........", startTime);
        List<List<UserDO>> subList = ListUtil.partition(userList, batchSize);
        for (List<UserDO> users : subList) {
            UserBatchTask userBatchTask = new UserBatchTask(userService, users);
            asyncService.executeAsyncTask(userBatchTask);
        }
        log.info("批量入库数据：{}", userList.size());
    }

    @RequestMapping("/asyncData2")
    public void asyncData2() {
        for (int i = 0; i < dataSize; i++) {
            UserDO user = new UserDO();
            user.setId(i);
            user.setName("小明：" + i);
            user.setAge(rand.nextInt(100));
            user.setMobile("151" + rand.nextInt(9) * 1000 + rand.nextInt(9) * 1000);
            user.setEmail(user.getMobile() + "@163.com");
            userList.add(user);
            if (userList.size() >= batchSize) {
                UserBatchTask userTask = new UserBatchTask(userService, new ArrayList<>(userList));
                asyncService.executeAsyncTask(userTask);
                userList.clear();
            }
        }
        if (!CollectionUtils.isEmpty(userList)) {
            UserBatchTask userTask = new UserBatchTask(userService, new ArrayList<>(userList));
            asyncService.executeAsyncTask(userTask);
        }
    }

    @RequestMapping("/asyncData3")
    public void asyncData3() {
        long startTime = System.currentTimeMillis();
        log.info("开始批量插入数据，开始时间：{}.........", startTime);
        for (int i = 0; i < dataSize; i++) {
            UserDO user = new UserDO();
            user.setId(i);
            user.setName("小明：" + i);
            user.setAge(rand.nextInt(100));
            user.setMobile("151" + rand.nextInt(9) * 1000 + rand.nextInt(9) * 1000);
            user.setEmail(user.getMobile() + "@163.com");
            userList.add(user);
            if (userList.size() >= batchSize) {
                userService.saveBatch(new ArrayList<>(userList));
                userList.clear();
            }
        }
        if (!CollectionUtils.isEmpty(userList)) {
            userService.saveBatch(new ArrayList<>(userList));
        }
        log.info("总计耗时：{}", System.currentTimeMillis() - startTime);
    }

    @RequestMapping("/asyncData4")
    public void asyncData4() {
        long startTime = System.currentTimeMillis();
        log.info("开始批量插入数据，开始时间：{}.........", startTime);
        for (int i = 0; i < dataSize; i++) {
            UserDO user = new UserDO();
            user.setId(i);
            user.setName("小明：" + i);
            user.setAge(rand.nextInt(100));
            user.setMobile("151" + rand.nextInt(9) * 1000 + rand.nextInt(9) * 1000);
            user.setEmail(user.getMobile() + "@163.com");
            userList.add(user);
        }
        List<List<UserDO>> subList = ListUtil.partition(userList, batchSize);
        for (List<UserDO> users : subList) {
            userService.saveBatch(users);
        }
        log.info("总计耗时：{}", System.currentTimeMillis() - startTime);
    }

}
