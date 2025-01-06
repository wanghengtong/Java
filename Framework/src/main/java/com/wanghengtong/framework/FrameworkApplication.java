package com.wanghengtong.framework;

import com.wanghengtong.framework.utils.GitInfoUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

/**
 * @author wanghengtong
 * @desc IpUtils
 * @date 2024年12月25日 21:24
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication
@MapperScan("com.wanghengtong.framework.mapper")
public class FrameworkApplication {

    public static void main(String[] args) {
        if (Arrays.asList(args).contains("-v")) {
            System.out.println("Git Info: " + GitInfoUtils.getGitInfo());
            System.exit(0);
        }
        SpringApplication.run(FrameworkApplication.class, args);
    }

}
