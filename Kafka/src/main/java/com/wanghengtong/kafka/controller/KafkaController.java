package com.wanghengtong.kafka.controller;

import com.wanghengtong.kafka.service.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {

    @Autowired
    private KafkaProducer kafkaProducer;

    @GetMapping("/send")
    public String send(@RequestParam("message") String message) {
        kafkaProducer.sendMessage("my-topic", message);
        return "Message sent to the Kafka Topic";
    }

}
