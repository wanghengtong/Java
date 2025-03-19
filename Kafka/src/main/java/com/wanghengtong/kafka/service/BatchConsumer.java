package com.wanghengtong.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BatchConsumer {

    @KafkaListener(topics = "my-topic", groupId = "my-group", autoStartup = "true", concurrency = "100", containerFactory = "batchFactory")
    public void batchListen(List<String> messages, Acknowledgment acknowledgment) {
        log.info("BatchConsumer - Received batch:  {}", messages);

        acknowledgment.acknowledge();
    }

}