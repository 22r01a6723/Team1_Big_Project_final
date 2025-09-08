package com.smarsh.compliance.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class ComplianceGlobalExceptionHandler {

    @ExceptionHandler(ComplianceValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(ComplianceValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "type", "validation_error",
                        "title", "Validation Error",
                        "status", 400,
                        "detail", ex.getMessage(),
                        "timestamp", Instant.now()
                ));
    }

    @ExceptionHandler(ComplianceDuplicateException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(ComplianceDuplicateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "type", "duplicate_error",
                        "title", "Duplicate Resource",
                        "status", 409,
                        "detail", ex.getMessage(),
                        "timestamp", Instant.now()
                ));
    }

    @ExceptionHandler(ComplianceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ComplianceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "type", "not_found",
                        "title", "Resource Not Found",
                        "status", 404,
                        "detail", ex.getMessage(),
                        "timestamp", Instant.now()
                ));
    }

    @ExceptionHandler(CompliancePolicyException.class)
    public ResponseEntity<Map<String, Object>> handlePolicy(CompliancePolicyException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "type", "policy_error",
                        "title", "Policy Error",
                        "status", 400,
                        "detail", ex.getMessage(),
                        "timestamp", Instant.now()
                ));
    }

    @ExceptionHandler(ComplianceKafkaException.class)
    public ResponseEntity<Map<String, Object>> handleKafka(ComplianceKafkaException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "type", "kafka_error",
                        "title", "Kafka Error",
                        "status", 500,
                        "detail", ex.getMessage(),
                        "timestamp", Instant.now()
                ));
    }

    @ExceptionHandler(ComplianceMongoException.class)
    public ResponseEntity<Map<String, Object>> handleMongo(ComplianceMongoException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "type", "mongo_error",
                        "title", "MongoDB Error",
                        "status", 500,
                        "detail", ex.getMessage(),
                        "timestamp", Instant.now()
                ));
    }

    @ExceptionHandler(ComplianceException.class)
    public ResponseEntity<Map<String, Object>> handleCompliance(ComplianceException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "type", "compliance_error",
                        "title", "Compliance Error",
                        "status", 500,
                        "detail", ex.getMessage(),
                        "timestamp", Instant.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "type", "internal_error",
                        "title", "Internal Server Error",
                        "status", 500,
                        "detail", ex.getMessage(),
                        "timestamp", Instant.now()
                ));
    }
}

