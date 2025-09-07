package com.Project1.IngestionAndValidation.exception;

public class CompanyVaultException extends RuntimeException {
    public CompanyVaultException(String message) {
        super(message);
    }

    public CompanyVaultException(String message, Throwable cause) {
        super(message, cause);
    }
}