package com.wanghengtong.framework;

import com.wanghengtong.framework.utils.GitInfoUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

/**
 * @author wanghengtong
 * @desc FrameworkApplication
 * @date 2024年12月25日 21:24
 */
@EnableDiscoveryClient
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
        System.out.println("启动成功...");
        // 自定义Banner：https://patorjk.com/software/taag/#p=display&f=Rounded&t=your+framework&x=rainbow3&v=4&h=4&w=80&we=false
        System.out.println(
                        "                                  ██████                                                                                  █████     \n" +
                        "                                 ███░░███                                                                                ░░███      \n" +
                        " █████████████   █████ ████     ░███ ░░░  ████████   ██████   █████████████    ██████  █████ ███ █████  ██████  ████████  ░███ █████\n" +
                        "░░███░░███░░███ ░░███ ░███     ███████   ░░███░░███ ░░░░░███ ░░███░░███░░███  ███░░███░░███ ░███░░███  ███░░███░░███░░███ ░███░░███ \n" +
                        " ░███ ░███ ░███  ░███ ░███    ░░░███░     ░███ ░░░   ███████  ░███ ░███ ░███ ░███████  ░███ ░███ ░███ ░███ ░███ ░███ ░░░  ░██████░  \n" +
                        " ░███ ░███ ░███  ░███ ░███      ░███      ░███      ███░░███  ░███ ░███ ░███ ░███░░░   ░░███████████  ░███ ░███ ░███      ░███░░███ \n" +
                        " █████░███ █████ ░░███████      █████     █████    ░░████████ █████░███ █████░░██████   ░░████░████   ░░██████  █████     ████ █████\n" +
                        "░░░░░ ░░░ ░░░░░   ░░░░░███     ░░░░░     ░░░░░      ░░░░░░░░ ░░░░░ ░░░ ░░░░░  ░░░░░░     ░░░░ ░░░░     ░░░░░░  ░░░░░     ░░░░ ░░░░░ \n" +
                        "                  ███ ░███                                                                                                          \n" +
                        "                 ░░██████                                                                                                           \n" +
                        "                  ░░░░░░                                                                                                            ");
    }

}
