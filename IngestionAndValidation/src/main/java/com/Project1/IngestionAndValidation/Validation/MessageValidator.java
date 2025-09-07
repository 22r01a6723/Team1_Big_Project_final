package com.Project1.IngestionAndValidation.Validation;

/**
 * SOLID Principles:
 * - SRP: Only responsible for providing validation contract.
 * - ISP: Focused interface for message validation.
 */
public interface MessageValidator {
    void validate(String payload);
    String getNetwork();  // e.g. "slack", "email", "whatsapp"
}
