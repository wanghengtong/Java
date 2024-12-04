package com.wanghengtong.etcd.common;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wanghengtong.etcd.utils.Yaml2JsonUtil;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.options.WatchOption;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Charsets.UTF_8;

@Order(1)
@Component
@Slf4j
public class EtcdApplicationRunner implements ApplicationRunner {

    @Resource
    private Environment environment;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ETCD_SERVER_NAME = "etcdServer";

    private static final String PRODUCT_SERVER_NAME = "productServer";

    // 服务配置key
    private static String SERVER_CONFIG_KEY;

    // 监听以某个key前缀开始的配置变动
    private static String SERVER_BASE_URL_KEY_PREFIX;

    private static ObjectNode serverConfig = objectMapper.createObjectNode();

    @Value("${namespace}")
    private String NAMESPACE;

    @Value("${etcd.serverIp}")
    private String SERVER_IP;

    private static boolean etcdAvailable = Boolean.TRUE;

    @Override
    public void run(ApplicationArguments args) {
        init();
    }

    /**
     * 服务启动后执行的初始化方法
     */
    private void init() {
        // 初始化服务配置至etcd配置中心并监听配置变化
        SERVER_CONFIG_KEY = "/".concat(NAMESPACE).concat("/conf/").concat(SERVER_IP).concat("/").concat(ETCD_SERVER_NAME);
        initEtcdConfig(SERVER_CONFIG_KEY);
        watchEtcdKey(SERVER_CONFIG_KEY);

        // 初始化监听服务列表变化
        SERVER_BASE_URL_KEY_PREFIX = NAMESPACE.concat("/server/").concat(PRODUCT_SERVER_NAME);
        watchEtcdPrefix(SERVER_BASE_URL_KEY_PREFIX);
    }

    /**
     * 初始化配置至etcd配置中心，如果etcd配置中心存在配置，则以etcd配置中心配置为准
     */
    private void initEtcdConfig(String key) {
        String serverConfigValue = "";
        try {
            serverConfigValue = EtcdClientFactory.getEtcdValueByKey(key);
            log.debug("Etcd configuration:{}", serverConfigValue);
            if (StringUtils.isEmpty(serverConfigValue)) {
                ObjectNode logConfig = objectMapper.createObjectNode();
                logConfig.put("level", environment.getProperty("logging.level.root"));
                serverConfig.set("log", logConfig);

                String yaml = Yaml2JsonUtil.jsonConverToYaml(objectMapper.writeValueAsString(serverConfig));
                log.info("初始化服务配置Yaml至ETCD配置中心:{}", yaml);
                EtcdClientFactory.setEtcdValueByKey(SERVER_CONFIG_KEY, yaml);
            } else {
                List<String> yaml = Yaml2JsonUtil.yamlConverToJson(serverConfigValue);
                if (!CollectionUtils.isEmpty(yaml)) {
                    serverConfig = (ObjectNode) objectMapper.readTree(yaml.get(0));
                    reloadServerConfig(serverConfig);
                }
            }
        } catch (JsonProcessingException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            log.debug("读取本地配置文件！");
            ObjectNode logConfig = objectMapper.createObjectNode();
            logConfig.put("level", environment.getProperty("logging.level.root"));
            serverConfig.set("log", logConfig);
            etcdAvailable = Boolean.FALSE;
        }
    }

    private void watchEtcdKey(String key) {
        ByteSequence watchKey = ByteSequence.from(key, UTF_8);
        WatchOption watchOpts = WatchOption.builder().build();
        Watch watch = EtcdClientFactory.getEtcdClient().getWatchClient();
        watch.watch(watchKey, watchOpts, onNext -> {
            onNext.getEvents().forEach(event -> {
                log.info("监听到key变动: type=" + event.getEventType().toString() + ",  key=" + Optional.ofNullable(event.getKeyValue().getKey()).map(bs -> bs.toString(UTF_8)).orElse("") + ",  value=" + Optional.ofNullable(event.getKeyValue().getValue()).map(bs -> bs.toString(UTF_8)).orElse(""));
                if (Objects.nonNull(event.getKeyValue().getKey()) && SERVER_CONFIG_KEY.equals(event.getKeyValue().getKey().toString())) {
                    try {
                        if (StringUtils.isNotEmpty(String.valueOf(event.getKeyValue().getValue()))) {
                            List<String> yaml = Yaml2JsonUtil.yamlConverToJson(String.valueOf(event.getKeyValue().getValue()));
                            serverConfig = (ObjectNode) objectMapper.readTree(yaml.get(0));
                            reloadServerConfig(serverConfig);
                        }
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }
            });
        });
    }

    private void watchEtcdPrefix(String keyPrefix) {
        log.debug("Watching keyPrefix:{}", keyPrefix);
        ByteSequence watchKey = ByteSequence.from(keyPrefix, StandardCharsets.UTF_8);
        WatchOption watchOpts = WatchOption.builder().withPrefix(watchKey).build();
        Watch watch = EtcdClientFactory.getEtcdClient().getWatchClient();
        watch.watch(watchKey, watchOpts, onNext -> {
            onNext.getEvents().forEach(event -> {
                switch (event.getEventType()) {
                    case PUT: {
                        log.info("监听到keyPrefix更新：type=" + event.getEventType().toString() + ", key=" + Optional.ofNullable(event.getKeyValue().getKey()).map(bs -> bs.toString(UTF_8)).orElse("") + ", value=" + Optional.ofNullable(event.getKeyValue().getValue()).map(bs -> bs.toString(UTF_8)).orElse(""));
                    }
                    break;
                    case DELETE: {
                        log.info("监听到keyPrefix刪除：type=" + event.getEventType().toString() + ", key=" + Optional.ofNullable(event.getKeyValue().getKey()).map(bs -> bs.toString(UTF_8)).orElse("") + ", value=" + Optional.ofNullable(event.getKeyValue().getValue()).map(bs -> bs.toString(UTF_8)).orElse(""));
                    }
                    break;
                    default:
                        break;
                }
                String key = event.getKeyValue().getKey().toString();
                String value = event.getKeyValue().getValue().toString();
                if (key.contains(PRODUCT_SERVER_NAME)) {
                    // 做监听到服务变动后的操作...
                    log.info(value);
                }
            });
        });
    }


    /**
     * 更新服务配置
     *
     * @param serverConfig
     */
    private void reloadServerConfig(ObjectNode serverConfig) {
        if (!serverConfig.isEmpty()) {
            // 更新日志级别
            JsonNode configLog = serverConfig.get("log");
            String configLevel = configLog.get("level").asText();
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger logger = loggerContext.getLogger("root");
            logger.setLevel(Level.toLevel(configLevel));
        }
    }

    // 服务注册
    private void initServiceRegistry() {
        String id = UUID.randomUUID().toString();
        String serviceRegistryKey = "/".concat(NAMESPACE).concat("/server/").concat(id);

        ObjectNode serviceRegistryValue = objectMapper.createObjectNode();
        serviceRegistryValue.put("id", id);
        serviceRegistryValue.put("name", "name");
        serviceRegistryValue.put("version", "version");


        log.debug("serviceRegistryKey：{}，serviceRegistryValue：{}", serviceRegistryKey, serviceRegistryValue);
        try {
            EtcdClientFactory.serviceRegistry(serviceRegistryKey, objectMapper.writeValueAsString(serviceRegistryValue), 10);
        } catch (ExecutionException | JsonProcessingException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * etcd连接状态探测定时任务
     */
    @Scheduled(cron = "* * * * * *")
    public void etcdConnectStatusDetect() {
        if (!etcdAvailable) {
            try {
                EtcdClientFactory.getEtcdValueByKey(SERVER_CONFIG_KEY);
                etcdAvailable = Boolean.TRUE;
                init();
            } catch (TimeoutException | ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}