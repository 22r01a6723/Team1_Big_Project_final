package com.complyvault.audit.exception;

public class AuditUnsupportedOperationException extends AuditException {
    public AuditUnsupportedOperationException(String message) { super(message); }
    public AuditUnsupportedOperationException(String message, Throwable cause) { super(message, cause); }
}

