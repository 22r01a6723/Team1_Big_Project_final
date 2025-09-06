package com.project_1.normalizer.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_1.normalizer.model.CanonicalMessage;
import com.project_1.normalizer.service.AuditService;
import com.project_1.normalizer.service.MessageService;
import com.project_1.normalizer.service.MongoStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@EnableKafka
public class NormalizerConsumer {


    private final ObjectMapper objectMapper=new ObjectMapper();
    private final MessageService messageService;
    private final AuditService auditService;

    public NormalizerConsumer(MessageService messageService,AuditService auditService) {
        this.messageService = messageService;
        this.auditService=auditService;
    }

    @KafkaListener(topics = "normalizer-topic", groupId = "normalizer-services")
    public void consume(String messageJson) {
        try {
            log.info("Received Message from Kafka,{}", messageJson);

            Map<String,Object> mp=new HashMap<>();

            JsonNode root = objectMapper.readTree(messageJson);

            CanonicalMessage message = messageService.processMessage(messageJson);
             System.out.println(message);
        }
        catch (JsonProcessingException e) {
            auditService.logEvent("","","","JSON_PARSE_ERROR",Map.of("error",e.getMessage()));
        }
        catch (Exception e) {
            auditService.logEvent("","","","",Map.of("error",e.getMessage()));
        }
    }
}
