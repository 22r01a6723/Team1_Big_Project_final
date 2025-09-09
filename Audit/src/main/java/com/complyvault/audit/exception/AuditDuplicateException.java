package com.complyvault.audit.exception;

public class AuditDuplicateException extends AuditException {
    public AuditDuplicateException(String message) { super(message); }
    public AuditDuplicateException(String message, Throwable cause) { super(message, cause); }
}

