package com.Project1.IngestionAndValidation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Object> buildResponse(Exception ex, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", ex.getClass().getSimpleName());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(InvalidMessageException.class)
    public ResponseEntity<Object> handleInvalidMessage(InvalidMessageException ex) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedNetworkException.class)
    public ResponseEntity<Object> handleUnsupportedNetwork(UnsupportedNetworkException ex) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateMessageException.class)
    public ResponseEntity<Object> handleDuplicateMessage(DuplicateMessageException ex) {
        return buildResponse(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CompanyVaultException.class)
    public ResponseEntity<Object> handleGeneralException(CompanyVaultException ex) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // fallback for all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherExceptions(Exception ex) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
