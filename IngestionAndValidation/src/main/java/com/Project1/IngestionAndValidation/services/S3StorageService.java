package com.Project1.IngestionAndValidation.services;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
public class S3StorageService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final S3Client s3Client;

    @Value("${app.s3.bucket}")
    private String bucket;

    @Value("${app.s3.prefix:raw/}")
    private String keyPrefix;

    @Value("${app.s3.partitionByDate:true}")
    private boolean partitionByDate;

    public S3StorageService(
            @Value("${app.s3.region:us-east-1}") String region,
            @Value("${app.s3.accessKey:}") String accessKey,
            @Value("${app.s3.secretKey:}") String secretKey
    ) {
        Region awsRegion = Region.of(region);
        if (accessKey != null && !accessKey.isBlank() && secretKey != null && !secretKey.isBlank()) {
            log.info("🔑 Using STATIC credentials for S3 (accessKey provided)");
            this.s3Client = S3Client.builder()
                    .region(awsRegion)
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKey, secretKey)
                    ))
                    .build();
        } else {
            log.info("🔑 Using DEFAULT credential provider chain for S3 (no static keys)");
            this.s3Client = S3Client.builder()
                    .region(awsRegion)
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }
    }

    public void storeRawMessage(String rawJson) {
        try {
            // Parse JSON to extract tenant info
            JsonNode root = objectMapper.readTree(rawJson);
            String tenantId = root.get("tenantId").asText("unknown-tenant");

            // Build S3 key with tenant/date partitioning
            StringBuilder keyBuilder = new StringBuilder();
            keyBuilder.append(keyPrefix);
            keyBuilder.append(tenantId).append('/');

            if (partitionByDate) {
                LocalDate now = LocalDate.now(ZoneOffset.UTC);
                keyBuilder
                        .append(now.getYear()).append('/')
                        .append(String.format("%02d", now.getMonthValue())).append('/')
                        .append(String.format("%02d", now.getDayOfMonth())).append('/');
            }

            // Instead of messageId, use a unique timestamp-based name
            String fileName = "msg-" + System.currentTimeMillis() + ".json";
            keyBuilder.append(fileName);

            String key = keyBuilder.toString();

            // Upload raw JSON to S3
            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("application/json")
                    .build();

            s3Client.putObject(putReq, RequestBody.fromBytes(rawJson.getBytes(StandardCharsets.UTF_8)));

            log.info("✅ Uploaded raw message to S3 bucket={} key={}", bucket, key);

            // Log audit details
            Map<String, Object> details = new HashMap<>();
            details.put("bucket", bucket);
            details.put("key", key);
            details.put("tenantId", tenantId);

            log.info("📋 S3 Storage Audit: {}", details);

        } catch (Exception e) {
            log.error("❌ S3 upload failed: {}", e.getMessage());
            throw new RuntimeException("Failed to store message in S3", e);
        }
    }

}
