package com.wanghengtong.framework.controller;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.wanghengtong.framework.entity.User;
import com.wanghengtong.framework.service.IAsyncService;
import com.wanghengtong.framework.task.CallableTask;
import com.wanghengtong.framework.task.RunnableTask;
import com.wanghengtong.framework.task.ThreadTask;
import com.wanghengtong.framework.factory.ObjectMapperFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

@Slf4j
@RestController
@RequestMapping("/api/threadPool")
public class AsyncController {

    private static final int DATA_SIZE = 200000;

    private final List<User> userList = new ArrayList<>(DATA_SIZE);

    private static final int BATCH_SIZE = 1000;

    private final Random random = new Random();

    private final int cpuNum = Runtime.getRuntime().availableProcessors();

    @Autowired
    private IAsyncService asyncService;

    /**
     * 生成测试数据
     *
     * @return
     */
    private List<List<User>> getTestData() {
        if (!userList.isEmpty()) {
            return ListUtil.partition(userList, BATCH_SIZE);
        }
        for (int i = 0; i < DATA_SIZE; i++) {
            User user = new User();
            user.setId(i);
            user.setName("小明：" + i);
            user.setAge(random.nextInt(100));
            user.setMobile("151" + random.nextInt(9) * 1000 + random.nextInt(9) * 1000);
            user.setEmail(user.getMobile() + "@163.com");
            userList.add(user);
        }
        return ListUtil.partition(userList, BATCH_SIZE);
    }

    /**
     * @Async异步线程池
     */
    @RequestMapping("/test1")
    public void test1() {
        List<List<User>> subList = getTestData();
        for (List<User> users : subList) {
            RunnableTask runnableTask = new RunnableTask(users);
            asyncService.executeAsyncTask(runnableTask);
        }
    }

    /**
     * Future异步
     */
    @RequestMapping("/test2")
    public void test2() {
        log.info("CPU核数:{}", cpuNum);
        List<List<User>> subList = getTestData();

        // 创建一个列表来存储Future对象
        List<Future<Integer>> futures = new ArrayList<>();
        // 创建一个固定大小的线程池
        ExecutorService executor = Executors.newFixedThreadPool(cpuNum);
        for (List<User> users : subList) {
            // 提交任务并获取Future对象
            Future<Integer> future = executor.submit(new CallableTask(users));
            futures.add(future);
        }
        // 等待所有任务完成并获取结果
        int count = 0;
        for (Future<Integer> future : futures) {
            try {
                count += future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("异步任务执行异常", e);
            }
        }
        // 关闭线程池（注意：这不会立即停止正在执行的任务）
        executor.shutdown();
        // 如果我们想立即停止所有正在执行的任务，可以使用shutdownNow()，但这通常不是推荐的做法
        // executor.shutdownNow();
        log.info("共计消费数据数量:{}", count);
    }

    /**
     * Future异步
     */
    @RequestMapping("/test3")
    public void test3() {
        log.info("CPU核数:{}", cpuNum);
        List<List<User>> subList = getTestData();

        // 创建一个列表来存储Future对象
        List<Future<Integer>> futures = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(cpuNum);
        for (List<User> users : subList) {
            Future<Integer> future = executor.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    log.info("【{}】线程正在运行......", Thread.currentThread().getName());
                    for (User user : users) {
                        try {
                            log.info(ObjectMapperFactory.getINSTANCE().getObjectMapper().writeValueAsString(user));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return users.size();
                }
            });
            futures.add(future);
        }
        // 等待所有异步任务完成并获取结果
        int count = 0;
        for (Future<Integer> future : futures) {
            try {
                count += future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("异步任务执行异常", e);
            }
        }
        // 关闭线程池（注意：这不会立即停止正在执行的任务）
        executor.shutdown();
        // 如果我们想立即停止所有正在执行的任务，可以使用shutdownNow()，但这通常不是推荐的做法
        // executor.shutdownNow();
        log.info("共计消费数据数量:{}", count);
    }

    /**
     * CompletableFuture实现异步
     */
    @RequestMapping("/test4")
    public void test4() {
        List<List<User>> subList = getTestData();

        // 创建一个列表来存储Future对象
        List<CompletableFuture<Integer>> futures = new ArrayList<>();
        for (List<User> users : subList) {
            // 使用supplyAsync方法提交一个异步任务，并获取Future对象
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return new CallableTask(users).call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            futures.add(future);
        }
        // 等待所有异步任务完成并获取结果
        int count = 0;
        for (CompletableFuture<Integer> future : futures) {
            try {
                count += future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("异步任务执行异常", e);
            }
        }
        log.info("共计消费数据数量:{}", count);
    }

    /**
     * ThreadUtil异步工具类
     */
    @RequestMapping("/test5")
    public void test5() throws InterruptedException {
        List<List<User>> subList = getTestData();

        // 创建一个列表来存储Future对象
        List<Future<Integer>> futures = new ArrayList<>();
        for (List<User> users : subList) {
            Future<Integer> future = ThreadUtil.execAsync(() -> {
                log.info("【{}】线程正在运行......", Thread.currentThread().getName());
                for (User user : users) {
                    try {
                        log.info(ObjectMapperFactory.getINSTANCE().getObjectMapper().writeValueAsString(user));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }
                return users.size();
            });
            futures.add(future);
        }
        // 等待所有异步任务完成并获取结果
        int count = 0;
        for (Future<Integer> future : futures) {
            try {
                count += future.get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("异步任务执行异常", e);
            }
        }
        log.info("共计消费数据数量:{}", count);
    }

    /**
     * @Async异步线程池
     */
    @RequestMapping("/test6")
    public void test6() {
        List<List<User>> subList = getTestData();
        for (List<User> users : subList) {
            ThreadTask threadTask = new ThreadTask(users);
            asyncService.executeAsyncTask(threadTask);
        }
    }

}
