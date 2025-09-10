package com.smarsh.compliance.service;

import com.complyvault.shared.client.AuditClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuditClientProxy {

    private final AuditClient auditClient;

    public AuditClientProxy(AuditClient auditClient) {
        this.auditClient = auditClient;
    }

    public void logEvent(String tenantId, String messageId, String network, String eventType, String source, Map<String, Object> payload) {
        auditClient.logEvent(tenantId, messageId, network, eventType, source, payload);
    }
}
