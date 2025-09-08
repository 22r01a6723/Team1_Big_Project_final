// utils/MessageIdGenerator.java
package com.Project1.IngestionAndValidation.utils;

import com.Project1.IngestionAndValidation.Models.BaseMessageDTO;
import com.Project1.IngestionAndValidation.services.AuditService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;

/**
 * SOLID Principles:
 * - SRP: Only responsible for generating message IDs.
 * - OCP: Can be extended for new ID generation strategies.
 * - DIP: Depends on AuditService abstraction.
 */
@Service
public class MessageIdGenerator {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static MessageIdGenerator instance;
    private AuditService auditService;

    private MessageIdGenerator(AuditService auditService) {
        this.auditService = auditService;
    }

    public static MessageIdGenerator getInstance(AuditService auditService) {
        if (instance == null) {
            instance = new MessageIdGenerator(auditService);
        }
        return instance;
    }


    public String generate(JsonNode payload) {
        try {
            String json = objectMapper.writeValueAsString(payload); // stable JSON
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            System.out.println(json);
            byte[] hash = digest.digest(json.getBytes(StandardCharsets.UTF_8));


            // Encode in Base64 (URL safe, no padding)
            String messageId = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
            auditService.logEvent(
                    payload.get("tenantId").asText(),
                    messageId,
                    payload.get("network").asText(),
                    "ID_GENERATED",
                    Map.of("stableMessageId", messageId)
            );
            return messageId;
        } catch (Exception e) {

            throw new RuntimeException("Failed to generate message ID", e);
        }
    }


    private String safeGet(JsonNode node, String field) {
        if (node != null && node.hasNonNull(field)) {
            return node.get(field).asText();
        }
        return "UNKNOWN_" + field;
    }

    public static class MessageIdGenerationException extends RuntimeException {
        public MessageIdGenerationException(String message, Throwable cause) {
            super(message, cause);
            // If you want to throw another exception using 'e', you must pass it as 'cause' from the catch block
            // Example usage in catch block:
            // throw new MessageIdGenerationException("Error generating stableMessageId", e);
        }
    }

}
