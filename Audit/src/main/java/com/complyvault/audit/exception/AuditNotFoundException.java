package com.complyvault.audit.exception;

public class AuditNotFoundException extends AuditException {
    public AuditNotFoundException(String message) { super(message); }
    public AuditNotFoundException(String message, Throwable cause) { super(message, cause); }
}

