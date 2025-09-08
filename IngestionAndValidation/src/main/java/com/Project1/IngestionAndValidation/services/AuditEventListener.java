
package com.Project1.IngestionAndValidation.services;

import com.Project1.IngestionAndValidation.Models.AuditEvent;

/**
 * SOLID Principles:
 * - SRP: Only responsible for handling audit events.
 * - ISP: Focused interface for audit event handling.
 */
public interface AuditEventListener {
    void onAuditEvent(AuditEvent event);
}

