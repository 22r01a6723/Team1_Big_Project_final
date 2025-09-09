package com.Project1.IngestionAndValidation.message;


import com.Project1.IngestionAndValidation.exception.MessagePublishingException;
import com.Project1.IngestionAndValidation.kafka.KafkaMessageProducer;
import com.Project1.IngestionAndValidation.kafka.MessageProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private MessageProducer messageProducer;

    @BeforeEach
    void setUp() {
        messageProducer = new KafkaMessageProducer(kafkaTemplate, objectMapper);
    }

    @Test
    void testSendMessage_Success() throws JsonProcessingException {
        TestMessage message = new TestMessage("test", "value");
        when(objectMapper.writeValueAsString(message)).thenReturn("{\"test\":\"value\"}");

        assertDoesNotThrow(() -> messageProducer.sendMessage(message));
        verify(kafkaTemplate).send(eq("normalizer-topic"), any(String.class));
    }

    @Test
    void testSendMessage_JsonProcessingException() throws JsonProcessingException {
        TestMessage message = new TestMessage("test", "value");
        when(objectMapper.writeValueAsString(message)).thenThrow(new JsonProcessingException("JSON error") {});

        assertThrows(MessagePublishingException.class, () -> messageProducer.sendMessage(message));
    }

    @Test
    void testSendMessage_KafkaException() throws JsonProcessingException {
        TestMessage message = new TestMessage("test", "value");
        when(objectMapper.writeValueAsString(message)).thenReturn("{\"test\":\"value\"}");
        doThrow(new RuntimeException("Kafka error")).when(kafkaTemplate).send(any(), any());

        assertThrows(MessagePublishingException.class, () -> messageProducer.sendMessage(message));
    }


    @Test
    void testSendMessage_EmptyMessage() throws JsonProcessingException {
        TestMessage message = new TestMessage("", "");
        when(objectMapper.writeValueAsString(message)).thenReturn("{\"test\":\"\",\"value\":\"\"}");

        assertDoesNotThrow(() -> messageProducer.sendMessage(message));
        verify(kafkaTemplate).send(eq("normalizer-topic"), any(String.class));
    }

    @Test
    void testSendMessage_VerifyPayloadPassedToKafka() throws JsonProcessingException {
        TestMessage message = new TestMessage("key1", "val1");
        String expectedJson = "{\"key1\":\"val1\"}";
        when(objectMapper.writeValueAsString(message)).thenReturn(expectedJson);

        messageProducer.sendMessage(message);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("normalizer-topic"), captor.capture());

        assertEquals(expectedJson, captor.getValue());
    }

    @Test
    void testSendMessage_RuntimeExceptionWrapping() throws JsonProcessingException {
        TestMessage message = new TestMessage("fail", "test");
        when(objectMapper.writeValueAsString(message)).thenReturn("{\"fail\":\"test\"}");
        doThrow(new IllegalStateException("Kafka down")).when(kafkaTemplate).send(any(), any());

        MessagePublishingException ex =
                assertThrows(MessagePublishingException.class, () -> messageProducer.sendMessage(message));

        assertTrue(!ex.getMessage().contains("Kafka down"));
    }

    @Test
    void testSendMessage_ValidJsonButKafkaThrowsCheckedException() throws JsonProcessingException {
        TestMessage message = new TestMessage("checked", "exception");
        when(objectMapper.writeValueAsString(message)).thenReturn("{\"checked\":\"exception\"}");
        doThrow(new RuntimeException("Checked-like failure")).when(kafkaTemplate).send(any(), any());

        assertThrows(MessagePublishingException.class, () -> messageProducer.sendMessage(message));
    }

    @Test
    void testSendMessage_MultipleMessagesSequentially() throws JsonProcessingException {
        TestMessage m1 = new TestMessage("a", "1");
        TestMessage m2 = new TestMessage("b", "2");
        when(objectMapper.writeValueAsString(m1)).thenReturn("{\"a\":\"1\"}");
        when(objectMapper.writeValueAsString(m2)).thenReturn("{\"b\":\"2\"}");

        assertDoesNotThrow(() -> {
            messageProducer.sendMessage(m1);
            messageProducer.sendMessage(m2);
        });

        verify(kafkaTemplate, times(2)).send(eq("normalizer-topic"), any(String.class));
    }

    @Test
    void testSendMessage_SameMessageTwice() throws JsonProcessingException {
        TestMessage message = new TestMessage("dup", "msg");
        when(objectMapper.writeValueAsString(message)).thenReturn("{\"dup\":\"msg\"}");

        messageProducer.sendMessage(message);
        messageProducer.sendMessage(message);

        verify(kafkaTemplate, times(2)).send(eq("normalizer-topic"), any(String.class));
    }

    @Test
    void testSendMessage_AsyncSendSimulated() throws JsonProcessingException {
        TestMessage message = new TestMessage("async", "test");
        when(objectMapper.writeValueAsString(message)).thenReturn("{\"async\":\"test\"}");
        when(kafkaTemplate.send(any(), any())).thenReturn(CompletableFuture.completedFuture(null));

        assertDoesNotThrow(() -> messageProducer.sendMessage(message));
    }

    @Test
    void testSendMessage_LargePayload() throws JsonProcessingException {
        String bigValue = "x".repeat(10_000);
        TestMessage message = new TestMessage("big", bigValue);
        when(objectMapper.writeValueAsString(message)).thenReturn("{\"big\":\"" + bigValue + "\"}");

        assertDoesNotThrow(() -> messageProducer.sendMessage(message));
        verify(kafkaTemplate).send(eq("normalizer-topic"), any(String.class));
    }

    @Test
    void testSendMessage_ObjectMapperThrowsRuntimeException() throws JsonProcessingException {
        TestMessage message = new TestMessage("crash", "mapper");
        when(objectMapper.writeValueAsString(message)).thenThrow(new RuntimeException("Mapper exploded"));

        assertThrows(MessagePublishingException.class, () -> messageProducer.sendMessage(message));
    }



    static class TestMessage {
        private String test;
        private String value;

        public TestMessage(String test, String value) {
            this.test = test;
            this.value = value;
        }

        public String getTest() { return test; }
        public String getValue() { return value; }
    }
}