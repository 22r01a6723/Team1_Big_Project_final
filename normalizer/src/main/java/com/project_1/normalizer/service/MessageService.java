/*package com.project_1.normalizer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.project_1.normalizer.model.CanonicalMessage;
import com.project_1.normalizer.model.UniqueId;
import com.project_1.normalizer.repository.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_1.normalizer.repository.UniqueIdRepository;
import com.project_1.normalizer.util.MessageAdapterFactory;
import com.project_1.normalizer.util.adapters.MessageAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MessageService {

    private final ObjectMapper objectMapper=new ObjectMapper();
    private final MessageAdapterFactory messageAdapterFactory;
    private List<StorageService> storageServices;
    private ProducerService producerService;
    private UniqueIdRepository uniqueIdRepository;

    public MessageService(MessageAdapterFactory messageAdapterFactory,List<StorageService> storageServices,
                          ProducerService producerService,UniqueIdRepository uniqueIdRepository) {
        this.messageAdapterFactory = messageAdapterFactory;
        this.storageServices = storageServices;
        this.producerService = producerService;
        this.uniqueIdRepository = uniqueIdRepository;
    }



    public CanonicalMessage processMessage(String json) throws IOException {

        JsonNode root = objectMapper.readTree(json);
        System.out.println(root);
        String network=root.get("network").asText();

        MessageAdapter adapter = messageAdapterFactory.getAdapter(network);

        CanonicalMessage message= adapter.map(root);

        storageServices.forEach(storage->storage.store(message,json));

        uniqueIdRepository.save(new UniqueId(message.getMessageId()));

        producerService.sendMessage(message);



        return message;
    }

}*/




package com.project_1.normalizer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_1.normalizer.model.UniqueId;
import com.project_1.normalizer.repository.UniqueIdRepository;
import com.project_1.normalizer.service.AuditService;
import com.project_1.normalizer.model.CanonicalMessage;
import com.project_1.normalizer.util.MessageAdapterFactory;
import com.project_1.normalizer.util.adapters.MessageAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MessageService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MessageAdapterFactory messageAdapterFactory;
    private final List<StorageService> storageServices;
    private final ProducerService producerService;
    private final AuditService auditService; // 🔹 add
    private final UniqueIdRepository uniqueIdRepository;

    public MessageService(MessageAdapterFactory messageAdapterFactory,
                          List<StorageService> storageServices,
                          ProducerService producerService,
                          AuditService auditService,
                          UniqueIdRepository uniqueIdRepository) { // 🔹 add
        this.messageAdapterFactory = messageAdapterFactory;
        this.storageServices = storageServices;
        this.producerService = producerService;
        this.auditService = auditService; // 🔹 add
        this.uniqueIdRepository = uniqueIdRepository;
    }

    public CanonicalMessage processMessage(String json) throws IOException {
        JsonNode root = objectMapper.readTree(json);
        String network = root.get("network").asText();

        MessageAdapter adapter = messageAdapterFactory.getAdapter(network);
        CanonicalMessage message = adapter.map(root);

        // 🔹 AUDIT #1: CANONICALIZED
        auditService.logEvent(
                message.getTenantId(),
                message.getMessageId(),
                message.getNetwork(),
                "CANONICALIZED",
                Map.of(
                        // Keep screenshot’s style: store raw payload (string) under details.rawPayload
                        "rawPayload", json
                )
        );

        // fan out to storage targets (Mongo + Disk)
        storageServices.forEach(storage -> storage.store(message, json));

        // push to compliance pipeline (Kafka)
        producerService.sendMessage(message);

        uniqueIdRepository.save(new UniqueId(message.getMessageId()));

        return message;
    }
}




//
//
//// ✅ Duplicate check
//
//
//        // ✅ Save in Mongo
//        message.setUuid(message.getUuid()); // ensure MongoDB _id = uuid
//        repository.save(message);
//        log.info("Saved to DB with uuid={}", message.getUuid());
//
//// ✅ Save to disk as JSON (pretty-printed & marked read-only)
////        Path filePath = Path.of(fileDirectory, message.getUuid() + ".json");
////        File file = filePath.toFile();
////
////        // Write the message to disk
////        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, message);
////
////        // Mark the file as read-only
////        if (file.setReadOnly()) {
////            log.info("✅ File written and marked read-only with uuid={}", message.getUuid());
////        } else {
////            log.warn("⚠️ File written but could not be marked read-only: {}", file.getAbsolutePath());
////        }
////
////        return "✅ Message processed and stored successfully.";
//
//
//// ✅ Save to disk as JSON (create-only, immutable style)
//Path filePath = Path.of(fileDirectory, message.getUuid() + ".json");
//File file = filePath.toFile();
//
//// 1. Prevent overwriting if file already exists
//        if (file.exists()) {
//        log.error("❌ File already exists: {}", file.getAbsolutePath());
//        throw new IOException("File with UUID already exists. Cannot overwrite.");
//        }
//
//// 2. Write new file (pretty-printed JSON)
//                objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, inputJson);
//
//// 3. Mark file as read-only (prevents in-place edits via editors)
//        if (file.setReadOnly()) {
//        log.info("✅ File written and locked (read-only) with uuid={}", message.getUuid());
//        } else {
//        log.warn("⚠️ File written but could not be marked read-only: {}", file.getAbsolutePath());
//        }
//
//        return "✅ Message processed and stored successfully.";

//package com.project_1.normalizer.service;
//
//
//import com.project_1.normalizer.model.UnifiedMessage;
//import com.project_1.normalizer.repository.MessageRepository;
//import com.project_1.normalizer.util.NormalizationFactory;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Path;
//
//@Service
//public class MessageService {
//
//    private final MessageRepository repository;
//    private final ObjectMapper objectMapper;
//
//    private final String fileDirectory = "C:\\Users\\challa.rajesh\\OneDrive - Smarsh, Inc\\Desktop\\dataOnDisk";
//
//    public MessageService(MessageRepository repository, ObjectMapper objectMapper) {
//        this.repository = repository;
//        this.objectMapper = objectMapper;
//    }
//
//    public String processMessage(UnifiedMessage message) throws IOException {
//        if (repository.existsById(message.getUuid())) {
//            return "❌ Error: Duplicate message. Dropped.";
//        }
//
//        // Save in Mongo
//        repository.save(message);
//
//        // Save to disk as JSON
//        Path filePath = Path.of(fileDirectory, message.getUuid() + ".json");
//        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath.toString()), message);
//
//        return "✅ Message processed and stored successfully.";
//    }
//}
