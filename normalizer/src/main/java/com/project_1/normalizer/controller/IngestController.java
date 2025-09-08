package com.project_1.normalizer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_1.normalizer.model.CanonicalMessage;
import com.project_1.normalizer.service.MessageService;

import com.project_1.normalizer.service.MongoStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project_1.normalizer.util.NormalizationFactory;

import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1")
public class IngestController {

//    private final NormalizationFactory factory;
    private final MessageService messageService;
    private final MongoStorageService mongoStorageService;
    private ObjectMapper objectMapper=new ObjectMapper();

    public IngestController(MessageService messageService, MongoStorageService mongoStorageService) {
//        this.factory = factory;
        this.messageService = messageService;
        this.mongoStorageService = mongoStorageService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> ingestMessage(@RequestBody String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode idNode = root.get("id");
            if (idNode == null || idNode.asText().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Missing or blank required field: id");
            }
            String id = idNode.asText();
            if (mongoStorageService.isDuplicate(id)) {
                return ResponseEntity.status(409).body("Duplicate ID " + id);
            }
            messageService.processMessage(json);
            return ResponseEntity.ok("Message Processed");
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            return ResponseEntity.status(500).body("invalid JSON: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
        }
    }
}
