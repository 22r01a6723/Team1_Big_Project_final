package com.project_1.normalizer.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_1.normalizer.model.CanonicalMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ComplianceProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
   // put in application.yml/properties
    private String topic="compliance-topic";

    public ComplianceProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(CanonicalMessage message) {
        try {
            // Convert object to JSON
            String jsonMessage = objectMapper.writeValueAsString(message);

            // Send to Kafka
            kafkaTemplate.send(topic, message.getTenantId(), jsonMessage);
            System.out.println("✅ Sent message to Kafka: " + jsonMessage);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing CanonicalMessage", e);
        }
    }
}
