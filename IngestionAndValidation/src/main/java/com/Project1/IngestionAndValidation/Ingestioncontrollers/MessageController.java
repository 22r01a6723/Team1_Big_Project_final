package com.Project1.IngestionAndValidation.Ingestioncontrollers;

import com.Project1.IngestionAndValidation.services.MessageValidationService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Controller + Design Patterns in one file:
 * - Strategy Pattern: MessageValidationStrategy interface
 * - Concrete Strategy: JsonValidationService
 * - Template Method: MessageProcessingTemplate
 * - Duplicate Detection (in-memory)
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private static final Logger log = LoggerFactory.getLogger("MessageLogger");

    private final MessageValidationService messageValidationService;

    @Autowired
    public MessageController(MessageValidationService messageValidationService) {
        this.messageValidationService = messageValidationService;
    }

    @PostMapping
    public ResponseEntity<String> ingestMessage(@RequestBody String payload) {
        try {
            String result = messageValidationService.processIncoming(payload);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("INVALID MESSAGE: Ingestion failed - {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body("Invalid Message, Ingestion failed due to " + e.getMessage());
        }
    }
}
