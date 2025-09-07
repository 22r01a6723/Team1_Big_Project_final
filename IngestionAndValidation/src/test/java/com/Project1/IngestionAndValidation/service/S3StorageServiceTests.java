package com.Project1.IngestionAndValidation.service;

import com.Project1.IngestionAndValidation.services.S3StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3StorageServiceTest {

    @Mock
    private S3Client s3Client;

    private S3StorageService s3StorageService;

    @BeforeEach
    void setUp() {
        s3StorageService = new S3StorageService("us-east-1", "test-key", "test-secret") {
            @Override
            protected S3Client createS3Client(String region, String accessKey, String secretKey) {
                return s3Client;
            }
        };
    }



    @Test
    void testStoreRawMessage_InvalidJson_ThrowsException() {
        String invalidJson = "invalid json";
        assertThrows(RuntimeException.class, () -> s3StorageService.storeRawMessage(invalidJson));
    }

    @Test
    void testStoreRawMessage_EmptyJson_ThrowsException() {
        assertThrows(RuntimeException.class, () -> s3StorageService.storeRawMessage(""));
    }

    // New tests
    @Test
    void testStoreRawMessage_NullJson_ThrowsException() {
        assertThrows(RuntimeException.class, () -> s3StorageService.storeRawMessage(null));
    }



}
