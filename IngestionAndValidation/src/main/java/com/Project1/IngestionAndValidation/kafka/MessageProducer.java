package com.Project1.IngestionAndValidation.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Refactored MessageProducer with:
 * - Adapter Pattern: wraps KafkaTemplate
 * - Strategy Pattern: flexible serialization
 * - Template Method: standardized send workflow
 */
@Service
public class MessageProducer {

    private final ProducerAdapter adapter;
    private final SerializerStrategy serializer;

    public MessageProducer(KafkaTemplate<String, Object> kafkaTemplate, ObjectMapper objectMapper) {
        this.adapter = new KafkaProducerAdapter(kafkaTemplate);
        this.serializer = new JsonSerializerStrategy(objectMapper);
    }

    public void sendMessage(Object message) {
        sendTemplate(message);
    }

    // Template Method: standard workflow
    private void sendTemplate(Object message) {
        try {
            String serialized = serializer.serialize(message);
            adapter.send(serialized);
            System.out.println("✅ Sent to Kafka: " + serialized);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }

    // ================== ADAPTER ==================
    interface ProducerAdapter {
        void send(String message);
    }

    static class KafkaProducerAdapter implements ProducerAdapter {
        private final KafkaTemplate<String, Object> kafkaTemplate;

        public KafkaProducerAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
            this.kafkaTemplate = kafkaTemplate;
        }

        @Override
        public void send(String message) {
            kafkaTemplate.send("normalizer-topic", message);
        }
    }

    // Example future adapter: RabbitMQProducerAdapter
    static class RabbitMqProducerAdapter implements ProducerAdapter {
        @Override
        public void send(String message) {
            System.out.println("Sent to RabbitMQ: " + message);
        }
    }

    // ================== STRATEGY ==================
    interface SerializerStrategy {
        String serialize(Object obj) throws Exception;
    }

    static class JsonSerializerStrategy implements SerializerStrategy {
        private final ObjectMapper objectMapper;

        public JsonSerializerStrategy(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public String serialize(Object obj) throws JsonProcessingException {
            return objectMapper.writeValueAsString(obj);
        }
    }

    // Example future strategy: AvroSerializerStrategy, ProtobufSerializerStrategy
}
