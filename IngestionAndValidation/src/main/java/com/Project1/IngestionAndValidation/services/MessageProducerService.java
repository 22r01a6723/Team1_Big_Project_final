package com.Project1.IngestionAndValidation.services;

import com.Project1.IngestionAndValidation.kafka.MessageProducer;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageProducerService {

    private MessageProducer messageProducer;
    public MessageProducerService(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }


    public void produceMessage(ObjectNode message) {
        messageProducer.sendMessage(message);
    }

}
