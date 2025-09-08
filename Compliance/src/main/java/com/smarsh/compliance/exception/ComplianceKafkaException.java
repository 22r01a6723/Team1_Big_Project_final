package com.smarsh.compliance.exception;

public class ComplianceKafkaException extends ComplianceException {
    public ComplianceKafkaException(String message) { super(message); }
    public ComplianceKafkaException(String message, Throwable cause) { super(message, cause); }
}

