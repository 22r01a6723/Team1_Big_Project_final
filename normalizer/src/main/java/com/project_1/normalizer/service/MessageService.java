//
//package com.project_1.normalizer.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project_1.normalizer.model.UniqueId;
//import com.project_1.normalizer.repository.UniqueIdRepository;
//import com.project_1.normalizer.service.AuditService;
//import com.project_1.normalizer.model.CanonicalMessage;
//import com.project_1.normalizer.util.MessageAdapterFactory;
//import com.project_1.normalizer.util.adapters.MessageAdapter;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//
//@Slf4j
//@Service
//public class MessageService {
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    private final MessageAdapterFactory messageAdapterFactory;
//    private final List<StorageService> storageServices;
//    private final ProducerService producerService;
//    private final AuditService auditService; // 🔹 add
//    private final UniqueIdRepository uniqueIdRepository;
//
//    public MessageService(MessageAdapterFactory messageAdapterFactory,
//                          List<StorageService> storageServices,
//                          ProducerService producerService,
//                          AuditService auditService,
//                          UniqueIdRepository uniqueIdRepository) { // 🔹 add
//        this.messageAdapterFactory = messageAdapterFactory;
//        this.storageServices = storageServices;
//        this.producerService = producerService;
//        this.auditService = auditService; // 🔹 add
//        this.uniqueIdRepository = uniqueIdRepository;
//    }
//
//    public CanonicalMessage processMessage(String json) throws IOException {
//        JsonNode root = objectMapper.readTree(json);
//        String network = root.get("network").asText();
//
//        MessageAdapter adapter = messageAdapterFactory.getAdapter(network);
//        CanonicalMessage message = adapter.map(root);
//
//        // 🔹 AUDIT #1: CANONICALIZED
//        auditService.logEvent(
//                message.getTenantId(),
//                message.getMessageId(),
//                message.getNetwork(),
//                "CANONICALIZED",
//                Map.of(
//                        // Keep screenshot’s style: store raw payload (string) under details.rawPayload
//                        "rawPayload", json
//                )
//        );
//
//        // fan out to storage targets (Mongo + Disk)
//        storageServices.forEach(storage -> storage.store(message, json));
//
//        // push to compliance pipeline (Kafka)
//        producerService.sendMessage(message);
//
//        uniqueIdRepository.save(new UniqueId(message.getMessageId()));
//
//        return message;
//    }
//}
//
//
//
//
package com.project_1.normalizer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_1.normalizer.exception.*;
import com.project_1.normalizer.model.CanonicalMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CanonicalMessage normalizeMessage(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);

            // Validate message format
            if (rootNode == null || !rootNode.has("messageType")) {
                throw new InvalidMessageFormatException("Missing required field: messageType");
            }

            String messageType = rootNode.get("messageType").asText();
            if (!isSupportedType(messageType)) {
                throw new UnsupportedMessageTypeException("Unsupported message type: " + messageType);
            }

            // Perform normalization
            try {
                return mapToCanonical(rootNode);
            } catch (Exception ex) {
                throw new MappingException("Failed to map message fields", ex);
            }

        } catch (InvalidMessageFormatException | UnsupportedMessageTypeException |
                 MappingException e) {
            log.error("Normalization failed: {}", e.getMessage());
            throw e; // rethrow custom exception
        } catch (Exception e) {
            log.error("Unexpected normalization error", e);
            throw new NormalizationException("Unexpected error while normalizing message", e);
        }
    }

    private boolean isSupportedType(String messageType) {
        return "EMAIL".equalsIgnoreCase(messageType) || "SLACK".equalsIgnoreCase(messageType);
    }

    private CanonicalMessage mapToCanonical(JsonNode rootNode) {
        CanonicalMessage message = new CanonicalMessage();
        message.setId(rootNode.path("id").asText());
        message.setType(rootNode.path("messageType").asText());
        message.setContent(rootNode.path("content").asText());
        return message;
    }
}
