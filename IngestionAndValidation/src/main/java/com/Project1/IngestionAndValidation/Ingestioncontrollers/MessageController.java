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
            String result = messageValidationService.processIncoming(payload);
            return ResponseEntity.ok(result);

        } catch (InvalidMessageException e) {
            log.error("INVALID MESSAGE: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body("InvalidMessageException: " + e.getMessage());

        } catch (CompanyVaultException e) {
            log.error("SYSTEM ERROR: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body("CompanyVaultException: " + e.getMessage());

        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
            // Show the exact exception class name + message
            return ResponseEntity.internalServerError()
                    .body(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}

