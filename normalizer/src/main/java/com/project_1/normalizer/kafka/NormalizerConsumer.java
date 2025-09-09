package com.project_1.normalizer.kafka;


import java.util.HashMap;
import java.util.Map;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.complyvault.shared.client.AuditClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_1.normalizer.model.CanonicalMessage;
import com.project_1.normalizer.service.MessageService;
import com.project_1.normalizer.service.MongoStorageService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@EnableKafka
public class NormalizerConsumer {


    private final ObjectMapper objectMapper=new ObjectMapper();
    private final MongoStorageService mongoStorageService;
    private MessageService messageService;
    private AuditClient auditClient;

    public NormalizerConsumer(MessageService messageService, MongoStorageService mongoStorageService,AuditClient auditClient) {
        this.messageService = messageService;
        this.mongoStorageService = mongoStorageService;
        this.auditClient=auditClient;
    }

    @KafkaListener(topics = "normalizer-topic", groupId = "normalizer-services")
    public void consume(String messageJson) {
        try {
            log.info("Received Message from Kafka: {}", messageJson);

            // Parse message to extract actual values for audit logging
            JsonNode root = objectMapper.readTree(messageJson);
            String tenantId = root.has("tenantId") ? root.get("tenantId").asText() : "unknown";
            String messageId = root.has("stableMessageId") ? root.get("stableMessageId").asText() : 
                              root.has("messageId") ? root.get("messageId").asText() : "unknown";
            String network = root.has("network") ? root.get("network").asText() : "unknown";

            Map<String,Object> auditDetails = new HashMap<>();
            auditDetails.put("rawMessage", messageJson);
            auditClient.logEvent(
                    tenantId,
                    messageId,
                    network,
                    "MESSAGE_RECEIVED",
                    "normalizer-service",
                    auditDetails
            );
            log.debug("Audit event logged for message: {}", messageId);

            CanonicalMessage message = messageService.processMessage(messageJson);
            log.info("Successfully processed message: {} for tenant: {}", message.getMessageId(), message.getTenantId());
        }
        catch (JsonProcessingException e) {
            log.error("Error parsing Kafka message: {}", e.getMessage(), e);
        }
        catch (Exception e) {
            log.error("Error in processing messages: {}", e.getMessage(), e);
        }
    }
}
