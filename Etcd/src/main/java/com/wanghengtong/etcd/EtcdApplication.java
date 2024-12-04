package com.wanghengtong.etcd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EtcdApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtcdApplication.class, args);
    }

}
