package com.Project1.IngestionAndValidation.exception;

public class DuplicateMessageException extends CompanyVaultException {
    public DuplicateMessageException(String message) {
        super(message);
    }

    public DuplicateMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}