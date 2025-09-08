package com.project_1.normalizer.exception;

public class NormalizerInvalidPayloadException extends NormalizerException {
    public NormalizerInvalidPayloadException(String message) {
        super(message);
    }
    public NormalizerInvalidPayloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
