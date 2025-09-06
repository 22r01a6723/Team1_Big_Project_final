
package com.Project1.IngestionAndValidation.Ingestioncontrollers;

import com.Project1.IngestionAndValidation.services.AuditService;
import com.Project1.IngestionAndValidation.services.MessageProducerService;
import com.Project1.IngestionAndValidation.services.MessageValidationService;
import com.Project1.IngestionAndValidation.utils.MessageIdGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger("MessageLogger");
    private final MessageValidationService messageValidationService;

    public MessageController(MessageValidationService messageValidationService
    ) {
        this.messageValidationService = messageValidationService;
    }

    @PostMapping
    public ResponseEntity<String> ingestMessage(@RequestBody String payload) {


        try {

            return ResponseEntity.ok(messageValidationService.processIncoming(payload));

        } catch (Exception e) {
            log.error("INVALID MESSAGE: Parsing failed - {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid Message, Parsing failed due to "+e.getMessage());
        }
    }
}
