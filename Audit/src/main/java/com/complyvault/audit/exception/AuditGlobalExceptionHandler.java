package com.complyvault.audit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.apache.kafka.common.KafkaException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AuditGlobalExceptionHandler {
    @ExceptionHandler({AuditNotFoundException.class})
    public ResponseEntity<Object> handleNotFound(AuditNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }
    @ExceptionHandler({AuditDataAccessException.class})
    public ResponseEntity<Object> handleDataAccess(AuditDataAccessException ex, WebRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
    @ExceptionHandler({AuditKafkaException.class})
    public ResponseEntity<Object> handleKafka(AuditKafkaException ex, WebRequest request) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }
    @ExceptionHandler({AuditValidationException.class})
    public ResponseEntity<Object> handleValidation(AuditValidationException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
    @ExceptionHandler({AuditDuplicateException.class})
    public ResponseEntity<Object> handleDuplicate(AuditDuplicateException ex, WebRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
    }
    @ExceptionHandler({AuditUnsupportedOperationException.class})
    public ResponseEntity<Object> handleUnsupported(AuditUnsupportedOperationException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_IMPLEMENTED, ex.getMessage());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce("", (a, b) -> a + "; " + b);
        return buildResponse(HttpStatus.BAD_REQUEST, errorMsg);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed JSON request: " + ex.getMessage());
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Missing request parameter: " + ex.getParameterName());
    }
    @ExceptionHandler(KafkaException.class)
    public ResponseEntity<Object> handleKafkaException(KafkaException ex, WebRequest request) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Kafka error: " + ex.getMessage());
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntime(RuntimeException ex, WebRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.");
    }
    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}
