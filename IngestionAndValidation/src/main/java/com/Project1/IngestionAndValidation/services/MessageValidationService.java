package com.Project1.IngestionAndValidation.services;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.Project1.IngestionAndValidation.Validation.MessageValidator;
import com.Project1.IngestionAndValidation.Validation.ValidatorRegistry;
import com.Project1.IngestionAndValidation.repository.UniqueIdRepository;
import com.Project1.IngestionAndValidation.utils.MessageIdGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class MessageValidationService {

    private final ValidatorRegistry validatorRegistry;
    private final AuditService auditService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DuplicateCheckService duplicateCheckService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final MessageIdGenerator messageIdGenerator;
    private final MessageProducerService messageProducerService;
    private final S3StorageService s3StorageService;


    public MessageValidationService(ValidatorRegistry validatorRegistry,
                                    AuditService auditService,
                                    UniqueIdRepository uniqueIdRepository,
                                    DuplicateCheckService  duplicateCheckService,
                                    MessageIdGenerator messageIdGenerator,
                                    MessageProducerService messageProducerService,
                                    S3StorageService s3StorageService) {
        this.validatorRegistry = validatorRegistry;
        this.auditService = auditService;
        this.duplicateCheckService=duplicateCheckService;
        this.messageIdGenerator=messageIdGenerator;
        this.messageProducerService=messageProducerService;
        this.s3StorageService = s3StorageService;

    }

    public String processIncoming(String payload) throws Exception {

        JsonNode root = mapper.readTree(payload);

        String network = root.path("network").asText();
        String tenantIdFromPayload = root.path("tenantId").asText();

        String stableId= messageIdGenerator.generate(root);

        ObjectNode objectNode = (ObjectNode) root;
        objectNode.put("stableMessageId", stableId);

        auditService.logEvent(
                tenantIdFromPayload,
                null,
                network,
                "INGESTED",
                Map.of("rawPayload", payload)
        );


        String tenantId = "";
        String messageId = "";
        try {
            MessageValidator validator = validatorRegistry.getValidator(network);
            validator.validate(payload);

            JsonNode node = objectMapper.readTree(payload);
            tenantId = node.has("tenantId") ? node.get("tenantId").asText() : "UNKNOWN";

            // ✅ Check if duplicate
            if (duplicateCheckService.isDuplicate(stableId)) {
                auditService.logEvent(
                        tenantId,
                        stableId,
                        network,
                        "DUPLICATE",
                        Map.of("status", "duplicate")
                );
                return "Duplicate message detected. ID=" + stableId;
            }
            System.out.println("no duplicate found");


            auditService.logEvent(
                    tenantId,
                    stableId,
                    network,
                    "VALIDATED",
                    Map.of("status", "success")
            );

            // Store raw message to S3
            s3StorageService.storeRawMessage(objectNode.toString());

            // Send to Kafka for normalization
            messageProducerService.produceMessage(objectNode);

            return "Message validated successfully. ID=" + stableId;

        } catch (Exception e) {
            auditService.logEvent(
                    tenantId,
                    stableId,
                    network,
                    "INVALIDATED",
                    Map.of("status", "failed", "reason", e.getMessage())
            );
            throw new RuntimeException("Error validating payload "+e.getMessage()+"MessageId: "+stableId);
        }
    }
}
