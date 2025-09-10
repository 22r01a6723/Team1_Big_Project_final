package com.smarsh.compliance.kafka;

import com.complyvault.shared.client.AuditClient;
import com.smarsh.compliance.models.Message;
import com.smarsh.compliance.service.ComplianceService;
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
        this.auditClient = auditClient;
        this.messageService = messageService;
    }

    @KafkaListener(topics = "compliance-topic", groupId = "compliance-services")
    public void consume(Message message) {
        try {
            auditClient.logEvent(message.getTenantId(), message.getMessageId(), message.getNetwork(),
                    "MESSAGE_RECEIVED_FROM_KAFKA", "compliance-service", Map.of("message", message));

            log.info("Received message from Kafka, {}", message);
            Message processedMessage = complianceService.process(message);
            messageService.publishMessage(processedMessage);

            auditClient.logEvent(message.getTenantId(), message.getMessageId(), message.getNetwork(),
                    "MESSAGE_PUBLISHED_TO_KAFKA", "compliance-service", Map.of("message", processedMessage));
        } catch (Exception e) {
            auditClient.logEvent(message.getTenantId(), message.getMessageId(), message.getNetwork(),
                    "ERROR_PROCESSING_MESSAGE", "compliance-service", Map.of("error", e.getMessage()));
            log.error("Error processing Kafka message", e);
        }
    }
}
