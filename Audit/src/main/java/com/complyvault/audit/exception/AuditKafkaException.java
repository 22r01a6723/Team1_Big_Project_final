package com.complyvault.audit.exception;

public class AuditKafkaException extends AuditException {
    public AuditKafkaException(String message) { super(message); }
    public AuditKafkaException(String message, Throwable cause) { super(message, cause); }
}

