package com.complyvault.retention.exception;

public class RetentionNotFoundException extends RuntimeException {
    public RetentionNotFoundException(String message) {
        super(message);
    }
}

