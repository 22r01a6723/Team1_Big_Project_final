package com.Project1.IngestionAndValidation.kafka;

import com.Project1.IngestionAndValidation.exception.MessagePublishingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public MessageProducer(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(Object message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            kafkaTemplate.send("normalizer-topic", jsonMessage);
            System.out.println("✅ Sent to Kafka: " + jsonMessage);
        } catch (JsonProcessingException e) {
            throw new MessagePublishingException("Failed to serialize message", e);
        } catch (Exception e) {
            throw new MessagePublishingException("Failed to send message to Kafka", e);
        }
    }
}
