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

//    @PostMapping("/norm/ingest")
//    public String ingestMessage(@RequestBody String json) throws IOException {
//        JsonNode root = objectMapper.readTree(json);
//        String Id=root.get("id").asText();
//         if(mongoStorageService.isDuplicate(Id)){
//             return "Message Dropped. Reason: found duplicate";
//         }
//         log.info("Message not found duplicate and sent for processing");
//         messageService.processMessage(root);
//         return "Message Processed";
//    }
}


