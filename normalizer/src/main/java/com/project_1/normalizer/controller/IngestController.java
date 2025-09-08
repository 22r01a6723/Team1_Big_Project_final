//package com.project_1.normalizer.controller;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project_1.normalizer.service.MessageService;
//
//import com.project_1.normalizer.service.MongoStorageService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//
//@Slf4j
//@RestController
//@RequestMapping("/v1")
//public class IngestController {
//
////    private final NormalizationFactory factory;
//    private final MessageService messageService;
//    private final MongoStorageService mongoStorageService;
//    private ObjectMapper objectMapper=new ObjectMapper();
//
//    public IngestController(MessageService messageService, MongoStorageService mongoStorageService) {
////        this.factory = factory;
//        this.messageService = messageService;
//        this.mongoStorageService = mongoStorageService;
//    }
//
////    @PostMapping("/norm/ingest")
////    public String ingestMessage(@RequestBody String json) throws IOException {
////        JsonNode root = objectMapper.readTree(json);
////        String Id=root.get("id").asText();
////         if(mongoStorageService.isDuplicate(Id)){
////             return "Message Dropped. Reason: found duplicate";
////         }
////         log.info("Message not found duplicate and sent for processing");
////         messageService.processMessage(String.valueOf(root));
////         return "Message Processed";
////    }
//}
//
//


package com.project_1.normalizer.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_1.normalizer.model.CanonicalMessage;
import com.project_1.normalizer.service.MessageService;
import com.project_1.normalizer.service.MongoStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1")
public class IngestController {

    private final MessageService messageService;
    private final MongoStorageService mongoStorageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public IngestController(MessageService messageService, MongoStorageService mongoStorageService) {
        this.messageService = messageService;
        this.mongoStorageService = mongoStorageService;
    }

    @PostMapping("/ingest")
    public ResponseEntity<String> ingestMessage(@RequestBody String body) {
        try {
            if (body == null || body.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ invalid JSON: empty body");
            }

            JsonNode root = objectMapper.readTree(body);

            if (!root.has("id") || root.get("id").isNull() || root.get("id").asText().trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Missing or blank required field: id");
            }

            String id = root.get("id").asText().trim();

            if (mongoStorageService.isDuplicate(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("❌ Duplicate ID " + id);
            }

            CanonicalMessage msg = messageService.processMessage(body);

            return ResponseEntity.ok("✅ Message Processed successfully with id=" + msg.getMessageId());

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.error("Invalid JSON", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ invalid JSON");
        } catch (Exception e) {
            log.error("Failed to process message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Failed to process message: " + e.getMessage());
        }
    }
}
