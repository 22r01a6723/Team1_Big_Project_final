package com.complyvault.shared.client;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.complyvault.shared.dto.AuditEventDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class AuditClient {

    private final RestTemplate restTemplate;
    
    @Value("${audit.service.url:http://localhost:8093}")
    private String auditServiceUrl;

    public void logEvent(
            String tenantId,
            String messageId,
            String network,
            String eventType,
            String serviceName,
            Map<String, Object> details
    ) {
        try {
            AuditEventDTO auditEvent = AuditEventDTO.builder()
                    .tenantId(tenantId)
                    .messageId(messageId)
                    .network(network)
                    .eventType(eventType)
                    .performedBy(serviceName)
                    .serviceName(serviceName)
                    .timestamp(Instant.now())
                    .details(details)
                    .build();

            String url = auditServiceUrl + "/api/audit/log";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<AuditEventDTO> request = new HttpEntity<>(auditEvent, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.debug("Successfully sent audit event: {} for tenant {} message {}", eventType, tenantId, messageId);
            } else {
                log.warn("Audit service returned non-success status: {} for event {}", response.getStatusCode(), eventType);
            }
        } catch (Exception e) {
            log.error("Failed to send audit event to audit service: {}", e.getMessage(), e);
        }
    }

    public void logEvent(
            String tenantId,
            String messageId,
            String network,
            String eventType,
            String serviceName
    ) {
        logEvent(tenantId, messageId, network, eventType, serviceName, Map.of());
    }
    
    public boolean isDuplicate(String messageId) {
        try {
            String url = auditServiceUrl + "/api/audit/duplicate/" + messageId;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (Boolean) response.getBody().get("isDuplicate");
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to check duplicate status for messageId {}: {}", messageId, e.getMessage(), e);
            return false;
        }
    }
}
