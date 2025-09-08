package com.project_1.normalizer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NormalizerStorageException.class)
    public ResponseEntity<Map<String, Object>> handleStorage(NormalizerStorageException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "type", "storage_error",
                        "title", "Storage Failure",
                        "status", 500,
                        "detail", ex.getMessage(),
                        "timestamp", Instant.now()
                ));
    }

    @ExceptionHandler(NormalizerProducerException.class)
    public ResponseEntity<Map<String, Object>> handleProducer(NormalizerProducerException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "type", "producer_error",
                        "title", "Kafka Producer Failure",
                        "status", 500,
                        "detail", ex.getMessage(),
                        "timestamp", Instant.now()
                ));
    }

    @ExceptionHandler(NormalizerUnsupportedNetworkException.class)
    public ResponseEntity<Map<String, Object>> handleUnsupportedNetwork(NormalizerUnsupportedNetworkException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "type", "unsupported_network",
                        "title", "Unsupported Network",
                        "status", 400,
                        "detail", ex.getMessage(),
                        "timestamp", Instant.now()
                ));
    }

    @ExceptionHandler(NormalizerInvalidPayloadException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidPayload(NormalizerInvalidPayloadException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "type", "invalid_payload",
                        "title", "Invalid Payload",
                        "status", 400,
                        "detail", ex.getMessage(),
                        "timestamp", Instant.now()
                ));
    }

    @ExceptionHandler(NormalizerInvalidMessageFormatException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidMessageFormat(NormalizerInvalidMessageFormatException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "type", "invalid_message_format",
                        "title", "Invalid Message Format",
                        "status", 400,
                        "detail", ex.getMessage(),
                        "timestamp", Instant.now()
                ));
    }

    @ExceptionHandler(NormalizerMappingException.class)
    public ResponseEntity<Map<String, Object>> handleMapping(NormalizerMappingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "type", "mapping_error",
                        "title", "Mapping Failure",
                        "status", 400,
                        "detail", ex.getMessage(),
                        "timestamp", Instant.now()
                ));
    }

    @ExceptionHandler(NormalizerException.class)
    public ResponseEntity<Map<String, Object>> handleNormalizer(NormalizerException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "type", "normalizer_error",
                        "title", "Normalizer Error",
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
