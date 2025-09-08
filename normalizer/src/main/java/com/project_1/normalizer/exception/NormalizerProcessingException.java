package com.project_1.normalizer.exception;

public class NormalizerProcessingException extends NormalizerException {
    public NormalizerProcessingException(String message) {
        super(message);
    }
    public NormalizerProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
