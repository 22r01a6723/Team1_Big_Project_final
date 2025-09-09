package com.Project1.IngestionAndValidation.services;

import com.Project1.IngestionAndValidation.Models.AuditEvent;
import org.springframework.stereotype.Component;

/**
 * Implementation of AuditEventListener using Observer pattern.
 * Follows SRP and ISP.
 */
@Component
public class AuditEventListenerImpl implements AuditEventListener {
    @Override
    public void onAuditEvent(AuditEvent event) {
        // Add logic to handle audit event, e.g., log or persist
        System.out.println("Audit event received: " + event);
        // You can add persistence or notification logic here
    }
}

