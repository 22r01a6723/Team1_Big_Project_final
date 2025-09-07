package com.Project1.IngestionAndValidation.exception;

public class InvalidMessageException extends ValidationException {
    public InvalidMessageException(String message) {
        super(message);
    }

    public InvalidMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}