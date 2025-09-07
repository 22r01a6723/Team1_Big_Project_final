package com.Project1.IngestionAndValidation.exception;

public class UnsupportedNetworkException extends ValidationException {
    public UnsupportedNetworkException(String message) {
        super(message);
    }

    public UnsupportedNetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}