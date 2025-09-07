package com.Project1.IngestionAndValidation.exception;

public class AuditLoggingException extends CompanyVaultException {
    public AuditLoggingException(String message, Throwable cause) {
        super(message, cause);
    }
}