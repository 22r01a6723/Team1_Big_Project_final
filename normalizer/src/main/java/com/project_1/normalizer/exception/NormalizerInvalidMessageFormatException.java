package com.project_1.normalizer.exception;

public class NormalizerInvalidMessageFormatException extends NormalizerException {
    public NormalizerInvalidMessageFormatException(String message) {
        super(message);
    }
    public NormalizerInvalidMessageFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}

