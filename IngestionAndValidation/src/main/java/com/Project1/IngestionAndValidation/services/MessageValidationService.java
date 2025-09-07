///*package com.Project1.IngestionAndValidation.services;
//
//import com.Project1.IngestionAndValidation.Validation.MessageValidator;
//import com.Project1.IngestionAndValidation.Validation.ValidatorRegistry;
//import com.Project1.IngestionAndValidation.services.AuditService;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//
//@Service
//public class MessageValidationService {
//
//    private final ValidatorRegistry validatorRegistry;
//    private final AuditService auditService;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public MessageValidationService(ValidatorRegistry validatorRegistry,
//                                    AuditService auditService) {
//        this.validatorRegistry = validatorRegistry;
//        this.auditService = auditService;
//    }
//
//    public String processIncoming(String payload, String network) {
//        String tenantId = "";
//        String messageId="";
//        try {
//            // Get correct validator for the network
//            MessageValidator validator = validatorRegistry.getValidator(network);
//
//            // Run schema validation
//            validator.validate(payload);
//
//            // Parse payload into JsonNode to fetch tenantId/messageId fields
//            JsonNode node = objectMapper.readTree(payload);
//
//             tenantId = node.has("tenantId") ? node.get("tenantId").asText() : "UNKNOWN";
//             messageId = node.has("messageId") ? node.get("messageId").asText() : null;
//
//                // AUDIT: VALIDATED (success)
//                auditService.logEvent(
//                        tenantId,
//                        messageId,
//                        network,
//                        "VALIDATED",
//                        Map.of("status", "success")
//                );
//
//                System.out.println("✅ Valid JSON for network: " + network);
//                return "Message validated successfully. ID=" + messageId;
//
//        } catch (Exception e) {
//            // Log unexpected validation error
//            auditService.logEvent(
//                    tenantId,
//                    messageId,
//                    network,
//                    "INVALIDATED",
//                    Map.of("status", "failed", "reason", "requiredFieldsMissing")
//            );
//
//            throw new RuntimeException("Error validating payload", e);
//        }
//    }
//}*/
//
//package com.Project1.IngestionAndValidation.services;
//
//import com.Project1.IngestionAndValidation.Models.UniqueId;
//import com.Project1.IngestionAndValidation.Validation.MessageValidator;
//import com.Project1.IngestionAndValidation.Validation.ValidatorRegistry;
//import com.Project1.IngestionAndValidation.repository.UniqueIdRepository;
//import com.Project1.IngestionAndValidation.services.AuditService;
//import com.Project1.IngestionAndValidation.services.DuplicateCheckService;
//import com.Project1.IngestionAndValidation.utils.MessageIdGenerator;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import org.springframework.stereotype.Service;
//import com.Project1.IngestionAndValidation.exception.*;
//
//import java.util.Map;
//
//@Service
//public class MessageValidationService {
//
//    private final ValidatorRegistry validatorRegistry;
//    private final AuditService auditService;
//    private final ObjectMapper objectMapper = new ObjectMapper();
//    private final DuplicateCheckService duplicateCheckService;
//    private final ObjectMapper mapper = new ObjectMapper();
//    private final MessageIdGenerator messageIdGenerator;
//    private final MessageProducerService messageProducerService;
//
//
//    public MessageValidationService(ValidatorRegistry validatorRegistry,
//                                    AuditService auditService,
//                                    UniqueIdRepository uniqueIdRepository,
//                                    DuplicateCheckService  duplicateCheckService,
//                                    MessageIdGenerator messageIdGenerator,
//                                    MessageProducerService messageProducerService) {
//        this.validatorRegistry = validatorRegistry;
//        this.auditService = auditService;
//        this.duplicateCheckService=duplicateCheckService;
//        this.messageIdGenerator=messageIdGenerator;
//        this.messageProducerService=messageProducerService;
//
//    }
//
//    public String processIncoming(String payload) throws Exception {
//
//        JsonNode root = mapper.readTree(payload);
//
//        String network = root.path("network").asText();
//        String tenantIdFromPayload = root.path("tenantId").asText();
//
//        String stableId = messageIdGenerator.generate(root);
//
//        ObjectNode objectNode = (ObjectNode) root;
//        objectNode.put("stableMessageId", stableId);
//
//        auditService.logEvent(
//                tenantIdFromPayload,
//                null,
//                network,
//                "INGESTED",
//                Map.of("rawPayload", payload)
//        );
//
//
//        String tenantId = "";
//        String messageId = "";
//        try {
//            MessageValidator validator = validatorRegistry.getValidator(network);
//            validator.validate(payload);
//
//            JsonNode node = objectMapper.readTree(payload);
//            tenantId = node.has("tenantId") ? node.get("tenantId").asText() : "UNKNOWN";
//
//            // ✅ Check if duplicate
//            if (duplicateCheckService.isDuplicate(stableId)) {
//                auditService.logEvent(
//                        tenantId,
//                        stableId,
//                        network,
//                        "DUPLICATE",
//                        Map.of("status", "duplicate")
//                );
//                return "Duplicate message detected. ID=" + stableId;
//            }
//            System.out.println("no duplicate found");
//
//
//            auditService.logEvent(
//                    tenantId,
//                    stableId,
//                    network,
//                    "VALIDATED",
//                    Map.of("status", "success")
//            );
//
//            messageProducerService.produceMessage(objectNode);
//
//            return "Message validated successfully. ID=" + stableId;
//
//        }
////        catch (Exception e) {
////            auditService.logEvent(
////                    tenantId,
////                    stableId,
////                    network,
////                    "INVALIDATED",
////                    Map.of("status", "failed", "reason", e.getMessage())
////            );
////            throw new RuntimeException("Error validating payload "+e.getMessage()+"MessageId: "+stableId);
////        }
//        catch (JsonProcessingException e) {
//            throw new InvalidMessageException("Malformed JSON", e);
//        } catch (ValidationException e) {
//            throw new InvalidMessageException("Schema validation failed: " + e.getMessage(), e);
//        } catch (CompanyVaultPersistenceException e) {
//            throw e; // already wrapped from repo calls
//        } catch (Exception e) {
//            throw new CompanyVaultException("Unexpected validation error for messageId=" + stableId, e);
//        }
//    }
//}
package com.Project1.IngestionAndValidation.services;

import com.Project1.IngestionAndValidation.Validation.MessageValidator;
import com.Project1.IngestionAndValidation.Validation.ValidatorRegistry;
import com.Project1.IngestionAndValidation.exception.*;
import com.Project1.IngestionAndValidation.repository.UniqueIdRepository;
import com.Project1.IngestionAndValidation.utils.MessageIdGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MessageValidationService {

    private final ValidatorRegistry validatorRegistry;
    private final AuditService auditService;
    private final DuplicateCheckService duplicateCheckService;
    private final MessageIdGenerator messageIdGenerator;
    private final MessageProducerService messageProducerService;
    private final ObjectMapper mapper = new ObjectMapper();

    public MessageValidationService(
            ValidatorRegistry validatorRegistry,
            AuditService auditService,
            UniqueIdRepository uniqueIdRepository,
            DuplicateCheckService duplicateCheckService,
            MessageIdGenerator messageIdGenerator,
            MessageProducerService messageProducerService) {
        this.validatorRegistry = validatorRegistry;
        this.auditService = auditService;
        this.duplicateCheckService = duplicateCheckService;
        this.messageIdGenerator = messageIdGenerator;
        this.messageProducerService = messageProducerService;
    }

    public String processIncoming(String payload) {
        JsonNode root;
        try {
            root = mapper.readTree(payload);
        } catch (JsonProcessingException e) {
            throw new InvalidMessageException("Malformed JSON", e);
        }

        String network = root.path("network").asText();
        String tenantIdFromPayload = root.path("tenantId").asText();

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

        return "Message validated successfully. ID=" + stableId;
    }
}

