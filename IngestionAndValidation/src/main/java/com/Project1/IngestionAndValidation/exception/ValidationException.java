package com.Project1.IngestionAndValidation.exception;

public class ValidationException extends CompanyVaultException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}