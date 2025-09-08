// utils/MessageIdGenerator.java
package com.Project1.IngestionAndValidation.utils;

import com.Project1.IngestionAndValidation.services.AuditService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
    private final AuditService auditService;

    public MessageIdGenerator(AuditService auditService) {
        this.auditService = auditService;
    }


    public String generate(JsonNode payload) {
        try {
            // Serialize to stable JSON
            String json = objectMapper.writeValueAsString(payload);

            // Hash JSON
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(json.getBytes(StandardCharsets.UTF_8));

            String messageId = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
            auditService.logEvent(
                    payload.get("tenantId").asText(),
                    messageId,
                    payload.get("network").asText(),
                    "ID_GENERATED",
                    Map.of("stableMessageId", messageId)
            );


            // Encode in Base64 (URL safe, no padding)
            String messageId = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

            // Debug logging
            System.out.println("[MessageIdGenerator] Generated ID: " + messageId +
                    " for tenant=" + safeGet(payload, "tenantId") +
                    " network=" + safeGet(payload, "network") +
                    " thread=" + Thread.currentThread().getName());

            // Audit logging
            auditService.logEvent(
                    safeGet(payload, "tenantId"),
                    messageId,
                    safeGet(payload, "network"),
                    "ID_GENERATED",
                    Map.of("stableMessageId", messageId)
            );


            return messageId;

        } catch (JsonProcessingException e) {
            throw new MessageIdGenerationException("Failed to serialize payload to JSON", e);
        } catch (NoSuchAlgorithmException e) {
            throw new MessageIdGenerationException("SHA-256 algorithm not available", e);
        } catch (Exception e) {
            throw new MessageIdGenerationException("Unexpected error generating stableMessageId", e);
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
        }
    }

}
