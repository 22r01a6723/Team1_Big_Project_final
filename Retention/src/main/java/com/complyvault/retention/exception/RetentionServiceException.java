package com.complyvault.retention.exception;

public class RetentionServiceException extends RuntimeException {
    public RetentionServiceException(String message) {
        super(message);
    }
    public RetentionServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

