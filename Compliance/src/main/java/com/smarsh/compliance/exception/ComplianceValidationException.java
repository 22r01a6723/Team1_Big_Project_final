package com.smarsh.compliance.exception;

public class ComplianceValidationException extends ComplianceException {
    public ComplianceValidationException(String message) { super(message); }
    public ComplianceValidationException(String message, Throwable cause) { super(message, cause); }
}

