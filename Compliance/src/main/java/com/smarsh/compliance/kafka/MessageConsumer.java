package com.smarsh.compliance.kafka;




import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarsh.compliance.models.Message;
import com.smarsh.compliance.service.AuditService;
import com.smarsh.compliance.service.ComplianceService;
import com.smarsh.compliance.service.KafkaProducerService;
import com.smarsh.compliance.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MessageConsumer {


    private final ComplianceService complianceService;
    private final MessageService messageService;
    private final AuditService auditService;

    public MessageConsumer(ComplianceService complianceService, AuditService auditService, MessageService messageService) {
        this.complianceService = complianceService;
        this.messageService=messageService;
        this.auditService = auditService;
    }

    @KafkaListener(topics = "compliance-topic", groupId = "compliance-services")
    public void consume(Message message) {
        try {
            auditService.logEvent(message.getTenantId(),message.getMessageId(),message.getNetwork(),
                    "MESSAGE_RECEIVED_FROM_KAFKA", Map.of("message",message));

//            Message message = objectMapper.readValue(messageJson, Message.class);
            log.info("Received message from Kafka,{}", message.toString());
            Message processedMessage=complianceService.process(message);
            messageService.publishMessage(processedMessage);
            log.info("Processed Message published to review-topic");
            auditService.logEvent(message.getTenantId(),message.getMessageId(),message.getNetwork(),
                    "MESSAGE_PUBLISHED_TO_KAFKA", Map.of("message",processedMessage));
//            System.out.println(message);
        }
//        catch (JsonProcessingException e) {
//            System.err.println("Error parsing Kafka message: " + e.getMessage());
//        }
        catch (Exception e) {
            auditService.logEvent(message.getTenantId(),message.getMessageId(),message.getNetwork(),
                    "Processed Message published to kafka", Map.of("error",e.getMessage()));
        }
    }
}
