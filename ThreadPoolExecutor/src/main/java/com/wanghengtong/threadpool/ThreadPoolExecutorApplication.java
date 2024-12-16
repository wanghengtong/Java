package com.wanghengtong.threadpool;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.wanghengtong.threadpool.mapper")
@SpringBootApplication
public class ThreadPoolExecutorApplication {

    public static void main(String[] args)  {
        SpringApplication.run(ThreadPoolExecutorApplication.class, args);
    }

}
