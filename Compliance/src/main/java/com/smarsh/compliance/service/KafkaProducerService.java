package com.smarsh.compliance.service;
import com.smarsh.compliance.entity.Flag;
import com.smarsh.compliance.exception.ComplianceKafkaException;
import com.smarsh.compliance.models.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic = "search-topic";

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishMessage(Message message) {
        try {
            kafkaTemplate.send(topic, message.getMessageId(), message);
        } catch (Exception e) {
            log.error("Failed to publish message to Kafka", e);
            throw new ComplianceKafkaException("Failed to publish message to Kafka: " + e.getMessage(), e);
        }
    }
}
