package com.Project1.IngestionAndValidation.services;
import com.Project1.IngestionAndValidation.Models.UniqueId;
import com.Project1.IngestionAndValidation.Validation.MessageValidator;
import com.Project1.IngestionAndValidation.Validation.ValidatorRegistry;
import com.Project1.IngestionAndValidation.exception.*;
import com.Project1.IngestionAndValidation.repository.UniqueIdRepository;
import com.Project1.IngestionAndValidation.utils.MessageIdGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.catalina.Session;
import org.apache.catalina.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MessageValidationService {

    private static final Logger logger = LoggerFactory.getLogger(MessageValidationService.class);

    private final ValidatorRegistry validatorRegistry;
    private final AuditService auditService;
    private final DuplicateCheckService duplicateCheckService;
    private final MessageIdGenerator messageIdGenerator;
    private final MessageProducerService messageProducerService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final UniqueIdRepository uniqueIdRepository;

    public MessageValidationService(
            ValidatorRegistry validatorRegistry,
            AuditService auditService,
            UniqueIdRepository uniqueIdRepository,
            DuplicateCheckService duplicateCheckService,
            MessageIdGenerator messageIdGenerator,
            MessageProducerService messageProducerService) {
        this.validatorRegistry = validatorRegistry;
        this.auditService = auditService;

        this.uniqueIdRepository = uniqueIdRepository;

        this.duplicateCheckService = duplicateCheckService;
        this.messageIdGenerator = messageIdGenerator;
        this.messageProducerService = messageProducerService;
    }


    public String processIncoming(String payload, String network) {

    public String processIncoming(String payload) {

        JsonNode root;
        try {
            root = mapper.readTree(payload);
        } catch (JsonProcessingException e) {
            throw new InvalidMessageException("Malformed JSON", e);
        }

        // Use the network parameter directly, do not redeclare
        String tenantIdFromPayload = root.path("tenantId").asText();

        String stableId = messageIdGenerator.generate(root);


        String stableId = messageIdGenerator.generate(root);


        ObjectNode objectNode = (ObjectNode) root;
        objectNode.put("stableMessageId", stableId);

        try {
            auditService.logEvent(
                    tenantIdFromPayload,
                    null,
                    network,
                    "INGESTED",
                    Map.of("rawPayload", payload)
            );
        } catch (Exception e) {
            throw new AuditLoggingException("Failed to log INGESTED event", e);
        }

        MessageValidator validator;
        try {
            validator = validatorRegistry.getValidator(network);
            validator.validate(payload);
        } catch (UnsupportedNetworkException | InvalidMessageException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Validation failed for network: " + network, e);
        }


        // Check for duplicates

        // ✅ Handle duplicates gracefully

        if (duplicateCheckService.isDuplicate(stableId)) {
            try {
                auditService.logEvent(
                        tenantIdFromPayload,
                        stableId,
                        network,
                        "DUPLICATE",
                        Map.of("status", "duplicate")
                );
            } catch (Exception e) {
                throw new AuditLoggingException("Failed to log DUPLICATE event", e);
            }

            throw new DuplicateMessageException("Duplicate message detected. ID=" + stableId);


            // 🔹 Instead of throwing exception, log a simple warning & return
            logger.warn("Duplicate message detected. ID={}", stableId);
            return "Duplicate message detected. ID=" + stableId;

        }

        try {
            auditService.logEvent(
                    tenantIdFromPayload,
                    stableId,
                    network,
                    "VALIDATED",
                    Map.of("status", "success")
            );
        } catch (Exception e) {
            throw new AuditLoggingException("Failed to log VALIDATED event", e);
        }

        // Produce message
        try {
            messageProducerService.produceMessage(objectNode);

        } catch (Exception e) {
            throw new MessagePublishingException("Failed to publish message to Kafka", e);
        }

        // Persist stable message ID to prevent future duplicates
        try {
            uniqueIdRepository.save(new UniqueId(stableId));
        } catch (Exception e) {
            throw new CompanyVaultPersistenceException("Failed to persist stable message ID", e);

        } catch (Exception e) {
            throw new MessagePublishingException("Failed to publish message to Kafka", e);

        }

        return "Message validated successfully. ID=" + stableId;
    }
}
