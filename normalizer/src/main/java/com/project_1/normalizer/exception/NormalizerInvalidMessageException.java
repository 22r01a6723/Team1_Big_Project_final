package com.project_1.normalizer.exception;

public class NormalizerInvalidMessageException extends NormalizerException {
    public NormalizerInvalidMessageException(String message) {
        super(message);
    }
    public NormalizerInvalidMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
