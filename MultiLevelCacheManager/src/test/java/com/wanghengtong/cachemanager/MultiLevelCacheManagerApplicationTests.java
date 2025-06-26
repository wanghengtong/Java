package com.wanghengtong.cachemanager;

import com.wanghengtong.cachemanager.bean.User;
import com.wanghengtong.cachemanager.service.IUserService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class MultiLevelCacheManagerApplicationTests {

    @Autowired
    private IUserService userService;

    @Test
    @Order(1)
    void testSelect() {
        System.out.println("=============第一次查询：=============");
        User user = userService.getById("1");
        System.out.println(user);
        System.out.println("=============第二次查询：=============");
        user = userService.getById("1");
        System.out.println(user);
        System.out.println("=============第三次查询：=============");
        try {
            //休眠7s后再次查询，主要是模拟Caffeine过期
            TimeUnit.SECONDS.sleep(7);
            user = userService.getById("1");
            System.out.println(user);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    void testAdd() {
        userService.add(new User("5", "田七"));
        System.out.println("=============第一次查询：=============");
        User user = userService.getById("5");
        System.out.println(user);
        System.out.println("=============第二次查询：=============");
        try {
            //休眠7s后再次查询，主要是模拟Caffeine过期
            TimeUnit.SECONDS.sleep(7);
            user = userService.getById("5");
            System.out.println(user);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    void testUpdate() {
        userService.update(new User("1", "zs"));
        System.out.println("=============第一次查询：=============");
        User user = userService.getById("1");
        System.out.println(user);
        System.out.println("=============第二次查询：=============");
        try {
            //休眠7s后再次查询，主要是模拟Caffeine过期
            TimeUnit.SECONDS.sleep(7);
            user = userService.getById("1");
            System.out.println(user);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(4)
    void testDelete() {
        System.out.println("=============第一次查询：=============");
        User user = userService.getById("1");
        System.out.println(user);
        userService.deleteById("1");
        System.out.println("=============第二次查询：=============");
        User user2 = userService.getById("1");
        System.out.println(user2);
    }

}
