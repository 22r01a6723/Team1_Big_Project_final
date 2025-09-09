package com.smarsh.compliance.kafka;




import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.complyvault.shared.client.AuditClient;
import com.smarsh.compliance.models.Message;
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
    private final AuditClient auditClient;

    public MessageConsumer(ComplianceService complianceService, AuditClient auditClient, MessageService messageService) {
        this.complianceService = complianceService;
        this.messageService=messageService;
        this.auditClient = auditClient;
    }

    @KafkaListener(topics = "compliance-topic", groupId = "compliance-services")
    public void consume(Message message) {
        try {
            auditClient.logEvent(message.getTenantId(),message.getMessageId(),message.getNetwork(),
                    "MESSAGE_RECEIVED_FROM_KAFKA", "compliance-service", Map.of("message",message));

//            Message message = objectMapper.readValue(messageJson, Message.class);
            log.info("Received message from Kafka,{}", message.toString());
            Message processedMessage=complianceService.process(message);
            messageService.publishMessage(processedMessage);
            log.info("Processed Message published to review-topic");
            auditClient.logEvent(message.getTenantId(),message.getMessageId(),message.getNetwork(),
                    "MESSAGE_PUBLISHED_TO_KAFKA", "compliance-service", Map.of("message",processedMessage));
//            System.out.println(message);
        }
//        catch (JsonProcessingException e) {
//            System.err.println("Error parsing Kafka message: " + e.getMessage());
//        }
        catch (Exception e) {
            auditClient.logEvent(message.getTenantId(),message.getMessageId(),message.getNetwork(),
                    "ERROR_PROCESSING_MESSAGE", "compliance-service", Map.of("error",e.getMessage()));
        }
    }
}
