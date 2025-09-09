package com.Project1.Search.config;


import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class KafkaConsumerConfigTest {

    private final KafkaConsumerConfig config = new KafkaConsumerConfig();

    @Test
    void testConsumerFactoryProperties() {
        ConsumerFactory<String, String> factory = config.consumerFactory();
        Map<String, Object> props = factory.getConfigurationProperties();

        assertEquals("localhost:9092", props.get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));
        assertEquals("search-service", props.get(ConsumerConfig.GROUP_ID_CONFIG));
        assertNotNull(props.get(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
        assertNotNull(props.get(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
    }

    @Test
    void testConsumerFactoryNotNull() {
        ConsumerFactory<String, String> factory = config.consumerFactory();
        assertNotNull(factory);
    }

    @Test
    void testKafkaListenerContainerFactoryNotNull() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                config.kafkaListenerContainerFactory();
        assertNotNull(factory);
    }

    @Test
    void testKafkaListenerContainerFactoryHasConsumerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                config.kafkaListenerContainerFactory();
        assertNotNull(factory.getConsumerFactory());
    }

    @Test
    void testConsumerFactoryConfigurationConsistency() {
        ConsumerFactory<String, String> factory1 = config.consumerFactory();
        ConsumerFactory<String, String> factory2 = config.consumerFactory();

        // Should return different instances but same configuration
        assertNotSame(factory1, factory2);
        assertEquals(factory1.getConfigurationProperties(), factory2.getConfigurationProperties());
    }

    @Test
    void testBootstrapServersConfigIsNotEmpty() {
        ConsumerFactory<String, String> factory = config.consumerFactory();
        String bootstrapServers = (String) factory.getConfigurationProperties()
                .get(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG);
        assertNotNull(bootstrapServers);
        assertFalse(bootstrapServers.isEmpty());
    }

    @Test
    void testGroupIdConfigIsNotEmpty() {
        ConsumerFactory<String, String> factory = config.consumerFactory();
        String groupId = (String) factory.getConfigurationProperties()
                .get(ConsumerConfig.GROUP_ID_CONFIG);
        assertNotNull(groupId);
        assertFalse(groupId.isEmpty());
    }
}
