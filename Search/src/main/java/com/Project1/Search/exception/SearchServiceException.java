package com.Project1.Search.exception;

public class SearchServiceException extends RuntimeException {
    public SearchServiceException(String message) {
        super(message);
    }
    public SearchServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

