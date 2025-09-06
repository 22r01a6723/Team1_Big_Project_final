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

@Service
public class MessageIdGenerator {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private AuditService auditService;

    public MessageIdGenerator(AuditService auditService)
    {
        this.auditService=auditService;
    }

    public  String generate(JsonNode payload) {
        try {
            String json = objectMapper.writeValueAsString(payload); // stable JSON
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            System.out.println(json);
            byte[] hash = digest.digest(json.getBytes(StandardCharsets.UTF_8));
            String messageId=Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
            auditService.logEvent(
                        payload.get("tenantId").asText(),
                    messageId,
                        payload.get("network").asText(),
                        "ID_GENERATED",
                        Map.of("stableMessageId", messageId)
                );

            return messageId;
        } catch (Exception e) {
            throw new RuntimeException("Error generating stableMessageId", e);
        }
    }
}

