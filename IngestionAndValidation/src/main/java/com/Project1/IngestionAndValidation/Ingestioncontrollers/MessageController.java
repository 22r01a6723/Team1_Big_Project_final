package com.Project1.IngestionAndValidation.Ingestioncontrollers;

import com.Project1.IngestionAndValidation.exception.CompanyVaultException;
import com.Project1.IngestionAndValidation.exception.InvalidMessageException;
import com.Project1.IngestionAndValidation.services.MessageValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger("MessageLogger");
    private final MessageValidationService messageValidationService;

    public MessageController(MessageValidationService messageValidationService) {
        this.messageValidationService = messageValidationService;
    }

    @PostMapping
    public ResponseEntity<String> ingestMessage(@RequestBody String payload) {
        try {
           ingestion-validation
            // Parse payload to extract network
            String network = null;
            try {
                com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(payload);
                network = node.has("network") ? node.get("network").asText() : null;
            } catch (Exception e) {
                log.error("Failed to parse network from payload: {}", e.getMessage(), e);
                return ResponseEntity.badRequest().body("Missing or invalid network field");
            }
            if (network == null || network.isEmpty()) {
                return ResponseEntity.badRequest().body("Network field is required");
            }
            String result = messageValidationService.processIncoming(payload, network);
            return ResponseEntity.ok(result);

            String result = messageValidationService.processIncoming(payload);
            return ResponseEntity.ok(result);

         main
        } catch (InvalidMessageException e) {
            log.error("INVALID MESSAGE: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body("InvalidMessageException: " + e.getMessage());  
          ingestion-validation


         main
        } catch (CompanyVaultException e) {
            log.error("SYSTEM ERROR: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("CompanyVaultException: " + e.getMessage());
         ingestion-validation
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);


        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            // Show the exact exception class name + message
           main
            return ResponseEntity.internalServerError()
                    .body(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}

