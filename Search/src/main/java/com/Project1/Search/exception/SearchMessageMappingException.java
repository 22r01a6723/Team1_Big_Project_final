package com.Project1.Search.exception;

public class SearchMessageMappingException extends RuntimeException {
    public SearchMessageMappingException(String message) {
        super(message);
    }
    public SearchMessageMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}

