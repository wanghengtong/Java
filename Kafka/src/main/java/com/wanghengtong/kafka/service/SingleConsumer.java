package com.wanghengtong.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

/**
 * @author wanghengtong
 * @desc
 * @date 2025年03月19日 17:10
 */
@Slf4j
@Service
public class SingleConsumer {

    @KafkaListener(topics = "my-topic", groupId = "my-group", autoStartup = "true", concurrency = "100", containerFactory = "singleFactory")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        log.info("SingleConsumer - Received: {}" , record.value());

        acknowledgment.acknowledge();
    }
}
