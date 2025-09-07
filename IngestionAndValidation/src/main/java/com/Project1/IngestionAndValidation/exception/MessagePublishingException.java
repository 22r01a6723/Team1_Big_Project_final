package com.Project1.IngestionAndValidation.exception;

public class MessagePublishingException extends CompanyVaultException {
    public MessagePublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}