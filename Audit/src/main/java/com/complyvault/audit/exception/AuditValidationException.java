package com.complyvault.audit.exception;

public class AuditValidationException extends AuditException {
    public AuditValidationException(String message) { super(message); }
    public AuditValidationException(String message, Throwable cause) { super(message, cause); }
}

