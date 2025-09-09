package com.Project1.IngestionAndValidation.services;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.Project1.IngestionAndValidation.Models.UniqueId;
import com.Project1.IngestionAndValidation.Validation.MessageValidator;
import com.Project1.IngestionAndValidation.Validation.ValidatorRegistry;
import com.Project1.IngestionAndValidation.exception.AuditLoggingException;
import com.Project1.IngestionAndValidation.exception.CompanyVaultPersistenceException;
import com.Project1.IngestionAndValidation.exception.DuplicateMessageException;
import com.Project1.IngestionAndValidation.exception.InvalidMessageException;
import com.Project1.IngestionAndValidation.exception.MessagePublishingException;
import com.Project1.IngestionAndValidation.exception.UnsupportedNetworkException;
import com.Project1.IngestionAndValidation.exception.ValidationException;
import com.Project1.IngestionAndValidation.repository.UniqueIdRepository;
import com.Project1.IngestionAndValidation.utils.MessageIdGenerator;
import com.complyvault.shared.client.AuditClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class MessageValidationService {

    private final ValidatorRegistry validatorRegistry;
    private final AuditClient auditClient;
    private final DuplicateCheckService duplicateCheckService;
    private final MessageIdGenerator messageIdGenerator;
    private final MessageProducerService messageProducerService;
    private final S3StorageService s3StorageService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final UniqueIdRepository uniqueIdRepository;

    public MessageValidationService(
            ValidatorRegistry validatorRegistry,
            AuditClient auditClient,
            UniqueIdRepository uniqueIdRepository,
            DuplicateCheckService duplicateCheckService,
            MessageIdGenerator messageIdGenerator,
            MessageProducerService messageProducerService,
            S3StorageService s3StorageService) {
        this.validatorRegistry = validatorRegistry;
        this.auditClient = auditClient;
        this.uniqueIdRepository = uniqueIdRepository;
        this.duplicateCheckService = duplicateCheckService;
        this.messageIdGenerator = messageIdGenerator;
        this.messageProducerService = messageProducerService;
        this.s3StorageService = s3StorageService;
    }

    public String processIncoming(String payload, String network) {
        JsonNode root;
        try {
            root = mapper.readTree(payload);
        } catch (JsonProcessingException e) {
            throw new InvalidMessageException("Malformed JSON", e);
        }

        // Use the network parameter directly, do not redeclare
        String tenantIdFromPayload = root.path("tenantId").asText();
        String stableId = messageIdGenerator.generate(root);
        ObjectNode objectNode = (ObjectNode) root;
        objectNode.put("stableMessageId", stableId);

        // Store raw message in S3
        String s3Key = null;
        try {
            s3Key = s3StorageService.storeRawMessage(payload);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store raw message in S3", e);
        }

        try {
            auditClient.logEvent(
                    tenantIdFromPayload,
                    null,
                    network,
                    "INGESTED",
                    "ingestion-validation-service",
                    Map.of("rawPayload", payload, "s3Key", s3Key)
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
        if (duplicateCheckService.isDuplicate(stableId)) {
            try {
                auditClient.logEvent(
                        tenantIdFromPayload,
                        stableId,
                        network,
                        "DUPLICATE",
                        "ingestion-validation-service",
                        Map.of("status", "duplicate")
                );
            } catch (Exception e) {
                throw new AuditLoggingException("Failed to log DUPLICATE event", e);
            }
            throw new DuplicateMessageException("Duplicate message detected. ID=" + stableId);
        }

        try {
            auditClient.logEvent(
                    tenantIdFromPayload,
                    stableId,
                    network,
                    "VALIDATED",
                    "ingestion-validation-service",
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
        }

        return "Message validated successfully. ID=" + stableId;
    }
}