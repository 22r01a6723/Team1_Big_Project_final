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
        try {
            System.out.println("Publishing Message to Kafka");
            kafkaProducerService.publishMessage(message);
        } catch (com.smarsh.compliance.exception.ComplianceKafkaException ke) {
            throw ke;
        } catch (Exception e) {
            // Log and wrap any unexpected exception
            org.slf4j.LoggerFactory.getLogger(MessageService.class).error("Error publishing message to Kafka", e);
            throw new com.smarsh.compliance.exception.ComplianceKafkaException("Error publishing message to Kafka: " + e.getMessage(), e);
        }
    }
}
