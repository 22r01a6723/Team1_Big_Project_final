package com.Project1.Search.exception;

public class SearchUnsupportedOperationException extends RuntimeException {
    public SearchUnsupportedOperationException(String message) {
        super(message);
    }
    public SearchUnsupportedOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}

