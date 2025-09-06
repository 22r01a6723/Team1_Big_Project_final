package com.Project1.Search.consumer;

import com.Project1.Search.model.Message;
import com.Project1.Search.repository.MessageRepository;
import com.Project1.Search.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MessageConsumer {

    private final MessageRepository messageRepository;
    private final MessageService messageService;


    public MessageConsumer(MessageRepository messageRepository,MessageService messageService) {
        this.messageRepository = messageRepository;
        this.messageService = messageService;
    }

    @KafkaListener(topics = "search-topic", groupId = "search-service")
    public void consume(String messageJson) {
        try {
            System.out.println("Message from Kafka: "+messageJson);
           Message message=messageService.MapToMessage(messageJson);
            System.out.println(message);
            messageRepository.save(message);
        }
        catch(Exception e) {
            log.error(e.getMessage());
        }
    }
}
