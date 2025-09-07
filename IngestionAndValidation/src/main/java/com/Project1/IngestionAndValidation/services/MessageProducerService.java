package com.Project1.IngestionAndValidation.services;

import com.Project1.IngestionAndValidation.kafka.MessageProducer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MessageProducerService {

    private static final Logger log = LoggerFactory.getLogger(MessageProducerService.class);

    private final ProducerContext producerContext;

    public MessageProducerService(MessageProducer kafkaProducer) {
        ProducerAdapter kafkaAdapter = new KafkaProducerAdapter(kafkaProducer);
        this.producerContext = new ProducerContext(kafkaAdapter);
    }

    public void produceMessage(ObjectNode message) {
        producerContext.send(message);
    }

    // ================== ADAPTER ==================
    interface ProducerAdapter {
        void doSend(ObjectNode message);
    }

    static class KafkaProducerAdapter implements ProducerAdapter {
        private final MessageProducer producer;

        public KafkaProducerAdapter(MessageProducer producer) {
            this.producer = producer;
        }

        @Override
        public void doSend(ObjectNode message) {
            log.info("Preparing to send message to Kafka: {}", message);
            producer.sendMessage(message);  // original send logic
            log.info("✅ Message sent to Kafka: {}", message);
        }
    }

    // ================== STRATEGY / CONTEXT ==================
    static class ProducerContext {
        private ProducerAdapter adapter;

        public ProducerContext(ProducerAdapter adapter) {
            this.adapter = adapter;
        }

        public void setAdapter(ProducerAdapter adapter) {
            this.adapter = adapter;
        }

        public void send(ObjectNode message) {
            adapter.doSend(message);
        }
    }
}
