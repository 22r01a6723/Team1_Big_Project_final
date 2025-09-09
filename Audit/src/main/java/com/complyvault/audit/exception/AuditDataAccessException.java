package com.complyvault.audit.exception;

public class AuditDataAccessException extends AuditException {
    public AuditDataAccessException(String message) { super(message); }
    public AuditDataAccessException(String message, Throwable cause) { super(message, cause); }
}

