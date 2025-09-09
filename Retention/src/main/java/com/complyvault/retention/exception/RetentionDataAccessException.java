package com.complyvault.retention.exception;

public class RetentionDataAccessException extends RuntimeException {
    public RetentionDataAccessException(String message) {
        super(message);
    }
    public RetentionDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

