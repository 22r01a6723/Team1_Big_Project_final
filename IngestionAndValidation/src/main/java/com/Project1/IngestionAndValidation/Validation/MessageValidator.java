
package com.Project1.IngestionAndValidation.Validation;

public interface MessageValidator {
    void validate(String payload);
    String getNetwork();  // e.g. "slack", "email", "whatsapp"
}
