package com.wanghengtong.etcd.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "etcd")
@Data
public class EtcdConfigConstant {

    private String serverIp;

    private String scheme;

    private String user;

    private String pwd;

    private List<String> endpoints;

}
