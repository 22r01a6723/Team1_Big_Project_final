package com.Project1.Search.exception;

public class SearchDataAccessException extends RuntimeException {
    public SearchDataAccessException(String message) {
        super(message);
    }
    public SearchDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}

