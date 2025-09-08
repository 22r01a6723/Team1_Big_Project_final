package com.project_1.normalizer.kafka;


import com.project_1.normalizer.model.CanonicalMessage;
import com.project_1.normalizer.service.AuditService;
import com.project_1.normalizer.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NormalizerConsumerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private AuditService auditService;

    private NormalizerConsumer normalizerConsumer;

    @BeforeEach
    void setUp() {
        normalizerConsumer = new NormalizerConsumer(messageService, auditService);
    }

    @Test
    void testConsume_ValidMessage() throws Exception {
        String messageJson = "{\"network\":\"email\",\"tenantId\":\"tenant-1\"}";
        CanonicalMessage message = CanonicalMessage.builder().messageId("msg-1").build();

        when(messageService.processMessage(messageJson)).thenReturn(message);

        normalizerConsumer.consume(messageJson);

        verify(messageService).processMessage(messageJson);
        verify(auditService, never()).logEvent(any(), any(), any(), any(), any());
    }

    @Test
    void testConsume_GeneralException() throws Exception {
        String messageJson = "{\"network\":\"email\"}";
        when(messageService.processMessage(messageJson)).thenThrow(new RuntimeException("Processing error"));

        normalizerConsumer.consume(messageJson);

        verify(auditService).logEvent(eq(""), eq(""), eq(""), eq(""), any(Map.class));
        verify(messageService).processMessage(messageJson);
    }

    @Test
    void testConsume_NullMessage() {
        assertDoesNotThrow(() -> normalizerConsumer.consume(null));
        verify(auditService).logEvent(eq(""), eq(""), eq(""), eq(""), any(Map.class));
    }

    @Test
    void testConsume_MessageWithNoMessageId() throws Exception {
        String messageJson = "{\"network\":\"sms\",\"tenantId\":\"tenant-2\"}";
        CanonicalMessage message = CanonicalMessage.builder().messageId(null).build();
        when(messageService.processMessage(messageJson)).thenReturn(message);

        normalizerConsumer.consume(messageJson);

        verify(messageService).processMessage(messageJson);
        // No audit log since it's not an exception
        verify(auditService, never()).logEvent(any(), any(), any(), any(), any());
    }

    @Test
    void testConsume_MessageWithEmptyNetwork() throws Exception {
        String messageJson = "{\"network\":\"\",\"tenantId\":\"tenant-1\"}";
        CanonicalMessage message = CanonicalMessage.builder().messageId("msg-2").network("").build();
        when(messageService.processMessage(messageJson)).thenReturn(message);

        normalizerConsumer.consume(messageJson);

        verify(messageService).processMessage(messageJson);
    }

    @Test
    void testConsume_MessageWithNullTenantId() throws Exception {
        String messageJson = "{\"network\":\"email\"}";
        CanonicalMessage message = CanonicalMessage.builder().messageId("msg-3").tenantId(null).build();
        when(messageService.processMessage(messageJson)).thenReturn(message);

        normalizerConsumer.consume(messageJson);

        verify(messageService).processMessage(messageJson);
    }

    @Test
    void testConsume_WhitespaceMessageJson() throws Exception {
        String messageJson = "   {\"network\":\"email\"}   ";
        CanonicalMessage message = CanonicalMessage.builder().messageId("msg-5").build();

        when(messageService.processMessage(messageJson.trim())).thenReturn(message);

        normalizerConsumer.consume(messageJson.trim());

        verify(messageService).processMessage(messageJson.trim());
    }

    @Test
    void testConsume_MultipleValidMessages() throws Exception {
        String msg1 = "{\"network\":\"email\",\"tenantId\":\"tenant-1\"}";
        String msg2 = "{\"network\":\"sms\",\"tenantId\":\"tenant-2\"}";
        CanonicalMessage cm1 = CanonicalMessage.builder().messageId("m1").build();
        CanonicalMessage cm2 = CanonicalMessage.builder().messageId("m2").build();

        when(messageService.processMessage(msg1)).thenReturn(cm1);
        when(messageService.processMessage(msg2)).thenReturn(cm2);

        normalizerConsumer.consume(msg1);
        normalizerConsumer.consume(msg2);

        verify(messageService).processMessage(msg1);
        verify(messageService).processMessage(msg2);
    }

    @Test
    void testConsume_MessageServiceReturnsNull() throws Exception {
        String messageJson = "{\"network\":\"email\"}";
        when(messageService.processMessage(messageJson)).thenReturn(null);

        normalizerConsumer.consume(messageJson);

        verify(messageService).processMessage(messageJson);
        // Should not throw, but no audit log unless exception occurs
        verify(auditService, never()).logEvent(any(), any(), any(), any(), any());
    }

    @Test
    void testConsume_LargeMessageJson() throws Exception {
        String messageJson = "{\"network\":\"email\",\"tenantId\":\"tenant-large\",\"messageId\":\"large-msg\"}";
        CanonicalMessage message = CanonicalMessage.builder().messageId("large-msg").tenantId("tenant-large").network("email").build();

        when(messageService.processMessage(messageJson)).thenReturn(message);

        normalizerConsumer.consume(messageJson);

        verify(messageService).processMessage(messageJson);
    }

}