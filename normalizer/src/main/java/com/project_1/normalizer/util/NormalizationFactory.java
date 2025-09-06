package com.project_1.normalizer.util;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;


@Service
public class NormalizationFactory {

//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    public UnifiedMessage mapToUnified(Map<String, Object> inputJson) {
//        System.out.println(inputJson);
//        JsonNode root = objectMapper.convertValue(inputJson, JsonNode.class);
//
//        UnifiedMessage msg = new UnifiedMessage();
//        msg.setUuid(root.get("uuid").asText());
//        msg.setTenantId(root.get("tenantId").asText());
//        msg.setNetwork(root.get("network").asText());
//
//        String network = msg.getNetwork().toLowerCase();
//
//        if ("email".equals(network)) {
//            mapEmail(root, msg);
//        } else if ("slack".equals(network)) {
//            mapSlack(root, msg);
//        }
//
//        return msg;
//    }
//
//    private void mapEmail(JsonNode root, UnifiedMessage msg) {
//        JsonNode payload = root.get("payload");
//
//        msg.setSender(payload.get("from").asText());
//        msg.setReceiver(objectMapper.convertValue(payload.get("to"), List.class));
//
//        UnifiedMessage.EmailData emailData = new UnifiedMessage.EmailData();
//        emailData.setSubject(payload.get("subject").asText());
//        msg.setEmail(emailData);
//
//        msg.setBody(payload.get("body").asText());
//        msg.setSendTimeStamp(Instant.parse(payload.get("sentAt").asText()));
//    }
//
//    private void mapSlack(JsonNode root, UnifiedMessage msg) {
//        msg.setSender(root.get("user").asText());
//        msg.setReceiver(Collections.singletonList(root.get("channel").asText()));
//
//        UnifiedMessage.SlackData slackData = new UnifiedMessage.SlackData();
//        slackData.setMessageId(root.get("messageId").asText());
//        slackData.setTeam(root.get("team").asText());
//        msg.setSlack(slackData);
//
//        msg.setBody(root.get("text").asText());
//        msg.setSendTimeStamp(Instant.parse(root.get("timestamp").asText()));
//    }
}


