package com.project_1.normalizer.exception;

public class NormalizerExternalServiceException extends NormalizerException {
    public NormalizerExternalServiceException(String message) {
        super(message);
    }
    public NormalizerExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
