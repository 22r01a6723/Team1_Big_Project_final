package com.Project1.Search.service;

import com.Project1.Search.model.Message;
import com.Project1.Search.repository.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Instant;
import java.util.Map;
import java.util.stream.DoubleStream;

@Service
public class MessageService {

    ObjectMapper objectMapper = new ObjectMapper();

    public MessageService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }
    public Message MapToMessage(String payload) throws Exception
    {
            JsonNode node = objectMapper.readTree(payload);
            Message message = Message.builder()
                    .messageId(node.get("messageId").asText())
                    .tenantId(node.get("tenantId").asText())
                    .network(node.get("network").asText())
                    // convert numeric timestamp to Instant
                    .timestamp(Instant.ofEpochSecond(node.get("timestamp").asLong()))
                    .participants(objectMapper.readerForListOf(Message.Participant.class)
                            .readValue(node.get("participants")))
                    .content(objectMapper.convertValue(node.get("content"), Message.Content.class))
                    .context(objectMapper.convertValue(node.get("context"), Message.Context.class))
                    .createdAt(Instant.ofEpochSecond(node.get("createdAt").asLong()))
                    .flagged(node.get("flagged").asBoolean())
                    .flagInfo(node.get("flagInfo").isNull() ? null :
                            objectMapper.treeToValue(node.get("flagInfo"), Message.FlagInfo.class))
                    .build();
            return message;
    }


}
