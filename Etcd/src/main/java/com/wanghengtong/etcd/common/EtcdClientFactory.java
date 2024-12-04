package com.wanghengtong.etcd.common;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Charsets.UTF_8;

@Component
@Slf4j
public class EtcdClientFactory {

    private static final long TIME_OUT = 2000L;

    private static final TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

    @Resource
    private EtcdConfigConstant etcdConfigConstant;

    private static Client client;

    private static String ETCD_ENDPOINTS = "";

    private static String ETCD_USERNAME = "";

    private static String ETCD_PASSWORD = "";

    @PostConstruct
    private void initialize() {
        String scheme = etcdConfigConstant.getScheme();

        List<String> endpoints = etcdConfigConstant.getEndpoints();
        for (String endpoint : endpoints) {
            ETCD_ENDPOINTS = ETCD_ENDPOINTS.concat(scheme).concat(endpoint).concat(",");
        }
        ETCD_ENDPOINTS = ETCD_ENDPOINTS.substring(0, ETCD_ENDPOINTS.length() - 1);
        log.info("ETCD_ENDPOINTS:{}", ETCD_ENDPOINTS);

        ETCD_USERNAME = etcdConfigConstant.getUser();
        log.info("ETCD_USERNAME:{}", ETCD_USERNAME);

        ETCD_PASSWORD = etcdConfigConstant.getPwd();
        log.info("ETCD_PASSWORD:{}", ETCD_PASSWORD);
    }

    public static synchronized Client getEtcdClient() {
        if (Objects.isNull(client)) {
            if (StringUtils.isEmpty(ETCD_USERNAME) || StringUtils.isEmpty(ETCD_PASSWORD)) {
                client = Client.builder().endpoints(ETCD_ENDPOINTS.split(",")).build();
            } else {
                client = Client.builder().endpoints(ETCD_ENDPOINTS.split(",")).user(ByteSequence.from(ETCD_USERNAME, UTF_8)).password(ByteSequence.from(ETCD_PASSWORD, UTF_8)).build();
            }
        }
        return client;
    }

    public static String getEtcdValueByKey(String key) throws ExecutionException, InterruptedException, TimeoutException {
        GetResponse getResponse = getEtcdClient().getKVClient().get(ByteSequence.from(key, UTF_8), GetOption.builder().build()).get(TIME_OUT, TIME_UNIT);
        if (getResponse.getKvs().isEmpty()) {
            return null;
        }
        return getResponse.getKvs().get(0).getValue().toString(UTF_8);
    }

    public static void setEtcdValueByKey(String key, String value) throws ExecutionException, InterruptedException, TimeoutException {
        getEtcdClient().getKVClient().put(ByteSequence.from(key, UTF_8), ByteSequence.from(value, UTF_8)).get(TIME_OUT, TIME_UNIT);
    }

    public static GetResponse getEtcdValueByKeyPrefix(String keyPrefix) throws ExecutionException, InterruptedException, TimeoutException {
        KV kvClient = getEtcdClient().getKVClient();
        ByteSequence watchKey = ByteSequence.from(keyPrefix, StandardCharsets.UTF_8);
        GetOption prefixOption = GetOption.builder().withPrefix(watchKey).build();
        return kvClient.get(watchKey, prefixOption).get(TIME_OUT, TIME_UNIT);
    }

    public static void serviceRegistry(String key, String value, long ttl) throws ExecutionException, InterruptedException {
        Lease lease = getEtcdClient().getLeaseClient();
        long leaseId = lease.grant(ttl).get().getID();
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        getEtcdClient().getKVClient().put(ByteSequence.from(key, UTF_8), ByteSequence.from(value, UTF_8), putOption);

        StreamObserver<LeaseKeepAliveResponse> observer = new StreamObserver<LeaseKeepAliveResponse>() {
            @Override
            public void onNext(LeaseKeepAliveResponse value) {
                log.debug("租约时间到达，开始续期！");
            }

            @Override
            public void onError(Throwable t) {
                log.error("租约续期发生异常：{}", t.getMessage());
            }

            @Override
            public void onCompleted() {
            }
        };
        lease.keepAlive(leaseId, observer);
    }

}
