package com.project_1.normalizer.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.complyvault.shared.client.AuditClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_1.normalizer.exception.NormalizerException;
import com.project_1.normalizer.exception.NormalizerInvalidMessageFormatException;
import com.project_1.normalizer.exception.NormalizerMappingException;
import com.project_1.normalizer.exception.NormalizerProducerException;
import com.project_1.normalizer.exception.NormalizerStorageException;
import com.project_1.normalizer.model.CanonicalMessage;
import com.project_1.normalizer.model.UniqueId;
import com.project_1.normalizer.repository.UniqueIdRepository;
import com.project_1.normalizer.util.MessageAdapterFactory;
import com.project_1.normalizer.util.adapters.MessageAdapter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MessageService implements IMessageService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MessageAdapterFactory messageAdapterFactory;
    private final List<StorageService> storageServices;
    private final ProducerService producerService;
    private final AuditClient auditClient;
    private final UniqueIdRepository uniqueIdRepository;

    public MessageService(MessageAdapterFactory messageAdapterFactory,
                          List<StorageService> storageServices,
                          ProducerService producerService,
                          AuditClient auditClient,
                          UniqueIdRepository uniqueIdRepository) {
        this.messageAdapterFactory = messageAdapterFactory;
        this.storageServices = storageServices;
        this.producerService = producerService;
        this.auditClient = auditClient;
        this.uniqueIdRepository = uniqueIdRepository;
    }

    @Override
    public CanonicalMessage processMessage(String json) throws IOException {
        try {
            JsonNode root = objectMapper.readTree(json);

            // ✅ Validation
            if (root == null || !root.has("network")) {
                throw new NormalizerInvalidMessageFormatException("Missing required field: network");
            }

            String network = root.get("network").asText();
            if (network == null || network.trim().isEmpty()) {
                throw new NormalizerInvalidMessageFormatException("Blank value for required field: network");
            }

            // ✅ Adapter mapping
            MessageAdapter adapter = messageAdapterFactory.getAdapter(network);
            CanonicalMessage message;
            try {
                log.debug("Using adapter for network: {}", network);
                message = adapter.map(root);
                log.debug("Successfully mapped message to canonical format: {}", message.getMessageId());
            } catch (Exception ex) {
                log.error("Failed to map message fields for network: {}, message: {}", network, json, ex);
                throw new NormalizerMappingException("Failed to map message fields for network: " + network, ex);
            }

            // ✅ Audit event: CANONICALIZED
            auditClient.logEvent(
                    message.getTenantId(),
                    message.getMessageId(),
                    message.getNetwork(),
                    "CANONICALIZED",
                    "normalizer-service",
                    Map.of("rawPayload", json)
            );

            // ✅ Fan out to storage targets (Mongo + Disk)
            for (StorageService storage : storageServices) {
                try {
                    storage.store(message, json);
                } catch (Exception ex) {
                    throw new NormalizerStorageException("Failed to store message in " + storage.getClass().getSimpleName(), ex);
                }
            }

            // ✅ Push to compliance pipeline (Kafka)
            try {
                producerService.sendMessage(message);
            } catch (Exception ex) {
                throw new NormalizerProducerException("Failed to send message to Kafka", ex);
            }

            // ✅ Save unique ID
            uniqueIdRepository.save(new UniqueId(message.getMessageId()));

            return message;
        } catch (NormalizerException e) {
            log.error("Message processing failed: {}", e.getMessage());
            throw e; // Rethrow custom exception for GlobalExceptionHandler
        } catch (Exception e) {
            log.error("Unexpected error during message processing", e);
            throw new NormalizerException("Unexpected error while processing message", e);
        }
    }
}
