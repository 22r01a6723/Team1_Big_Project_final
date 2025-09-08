package com.project_1.normalizer.exception;

public class NormalizerException extends RuntimeException {
    public NormalizerException(String message) { super(message); }
    public NormalizerException(String message, Throwable cause) { super(message, cause); }
}
