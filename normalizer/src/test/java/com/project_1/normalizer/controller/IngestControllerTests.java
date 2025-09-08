package com.project_1.normalizer.controller;

import com.project_1.normalizer.service.MessageService;
import com.project_1.normalizer.service.MongoStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngestControllerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private MongoStorageService mongoStorageService;

    private IngestController ingestController;

    @BeforeEach
    void setUp() {
        ingestController = new IngestController(messageService, mongoStorageService);
    }

    /** 1. Controller initializes */
    @Test
    void testControllerInitialization() {
        assertNotNull(ingestController);
        verifyNoInteractions(messageService, mongoStorageService);
    }


    /** 3. Missing ID */
    @Test
    void testIngestMessage_MissingId() {
        String json = "{\"network\":\"email\"}";

        ResponseEntity<String> response = ingestController.ingestMessage(json);

        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Missing or blank required field: id"));
        verifyNoInteractions(messageService);
    }

    /** 4. Duplicate ID */
    @Test
    void testIngestMessage_DuplicateId() {
        String json = "{\"id\":\"123\",\"network\":\"email\"}";
        when(mongoStorageService.isDuplicate("123")).thenReturn(true);

        ResponseEntity<String> response = ingestController.ingestMessage(json);

        assertEquals(409, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Duplicate ID 123"));
        verifyNoInteractions(messageService);
    }

    /** 5. Invalid JSON */
    @Test
    void testIngestMessage_InvalidJson() {
        String invalidJson = "{id:\"missing-quotes}";

        ResponseEntity<String> response = ingestController.ingestMessage(invalidJson);

        assertEquals(500, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("invalid JSON"));
        verifyNoInteractions(messageService);
    }
}
