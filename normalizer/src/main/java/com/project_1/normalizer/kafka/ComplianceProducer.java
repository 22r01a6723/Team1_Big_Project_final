package com.project_1.normalizer.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_1.normalizer.exception.NormalizerProducerException;
import com.project_1.normalizer.model.CanonicalMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ComplianceProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String topic = "compliance-topic";

    public ComplianceProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(CanonicalMessage message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topic, message.getTenantId(), jsonMessage);
        } catch (JsonProcessingException e) {
            throw new NormalizerProducerException("Error serializing CanonicalMessage", e);
        }
    }

}
