package com.smarsh.compliance.service;

import com.smarsh.compliance.models.Message;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private KafkaProducerService kafkaProducerService;

    public MessageService(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    public void publishMessage(Message message) {
        System.out.println("Publishing Message to Kafka");
        kafkaProducerService.publishMessage(message);
    }
}
