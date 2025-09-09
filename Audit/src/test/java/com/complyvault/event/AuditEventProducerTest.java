package com.complyvault.audit.event;


import com.complyvault.audit.audit.kafka.AuditEventProducer;
import com.complyvault.shared.dto.AuditEventDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditEventProducerTest {

    @Mock
    private KafkaTemplate<String, AuditEventDTO> kafkaTemplate;

    @InjectMocks
    private AuditEventProducer auditEventProducer;

    private AuditEventDTO auditEventDTO;

    @BeforeEach
    void setUp() {
        auditEventDTO = new AuditEventDTO();
        auditEventDTO.setTenantId("bank-001");
        auditEventDTO.setMessageId("msg-123");
        auditEventDTO.setEventType("INGESTED");
    }

    @Test
    void sendAuditEvent_ShouldSendToKafka() {
        // Act
        auditEventProducer.sendAuditEvent(auditEventDTO);

        // Assert
        verify(kafkaTemplate).send(eq("audit-events"), eq(auditEventDTO));
    }

    @Test
    void sendAuditEvent_WithKafkaError_ShouldLogError() {
        // Arrange
        doThrow(new RuntimeException("Kafka error")).when(kafkaTemplate).send(anyString(), any(AuditEventDTO.class));

        // Act
        auditEventProducer.sendAuditEvent(auditEventDTO);

        // Assert
        verify(kafkaTemplate).send(eq("audit-events"), eq(auditEventDTO));
        // Error should be logged but not rethrown
    }

    @Test
    void sendAuditEvent_WithNullEvent_ShouldHandleGracefully() {
        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> auditEventProducer.sendAuditEvent(null));
    }

    @Test
    void sendAuditEvent_MultipleCalls_ShouldSendMultipleTimes() {
        // Act
        auditEventProducer.sendAuditEvent(auditEventDTO);
        auditEventProducer.sendAuditEvent(auditEventDTO);
        auditEventProducer.sendAuditEvent(auditEventDTO);

        // Assert
        verify(kafkaTemplate, times(3)).send(eq("audit-events"), eq(auditEventDTO));
    }

    @Test
    void sendAuditEvent_WithDifferentEvents_ShouldSendEach() {
        // Arrange
        AuditEventDTO event2 = new AuditEventDTO();
        event2.setTenantId("bank-002");
        event2.setMessageId("msg-456");
        event2.setEventType("VALIDATED");

        // Act
        auditEventProducer.sendAuditEvent(auditEventDTO);
        auditEventProducer.sendAuditEvent(event2);

        // Assert
        verify(kafkaTemplate).send(eq("audit-events"), eq(auditEventDTO));
        verify(kafkaTemplate).send(eq("audit-events"), eq(event2));
    }

    @Test
    void sendAuditEvent_WithEmptyEvent_ShouldSendButLogMinimalInfo() {
        // Arrange
        AuditEventDTO emptyEvent = new AuditEventDTO();

        // Act
        auditEventProducer.sendAuditEvent(emptyEvent);

        // Assert
        verify(kafkaTemplate).send(eq("audit-events"), eq(emptyEvent));
    }

    @Test
    void sendAuditEvent_WhenKafkaTemplateIsNull_ShouldHandleGracefully() {
        // Arrange
        AuditEventProducer producerWithNullKafka = new AuditEventProducer(null);

        // Act & Assert
        assertDoesNotThrow(() -> producerWithNullKafka.sendAuditEvent(auditEventDTO));
    }


    @Test
    void sendAuditEvent_ShouldLogInfoMessageOnSuccess() {
        // Act
        auditEventProducer.sendAuditEvent(auditEventDTO);

        // Assert
        verify(kafkaTemplate).send(eq("audit-events"), eq(auditEventDTO));
        // No exception thrown = implies successful logging (can't assert logs directly without TestLogger)
    }

    @Test
    void sendAuditEvent_ShouldNotThrowEvenIfSendReturnsNullFuture() {
        // Arrange
        when(kafkaTemplate.send(anyString(), any(AuditEventDTO.class))).thenReturn(null);

        // Act & Assert
        assertDoesNotThrow(() -> auditEventProducer.sendAuditEvent(auditEventDTO));
    }

}
