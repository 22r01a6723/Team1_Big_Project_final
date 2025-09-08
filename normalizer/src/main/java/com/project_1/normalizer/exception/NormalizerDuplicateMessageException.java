package com.project_1.normalizer.exception;

public class NormalizerDuplicateMessageException extends NormalizerException {
    public NormalizerDuplicateMessageException(String message) {
        super(message);
    }
    public NormalizerDuplicateMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
