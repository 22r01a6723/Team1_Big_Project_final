package com.Project1.Search.service;

import com.Project1.Search.dto.SearchRequest;
import com.Project1.Search.model.Message;
import org.springframework.data.domain.Page;

import java.time.Instant;


public interface MessageSearchService {
    Page<Message> searchMessages(SearchRequest request);
    Page<Message> fullTextSearch(String keyword, String tenantId, int page, int size);
    Page<Message> searchByField(String field, String value, String tenantId, boolean exactMatch, int page, int size);
    Page<Message> searchByTimeRange(String tenantId, Instant start, Instant end, int page, int size);
    Page<Message> searchByFlagTimeRange(String tenantId, Instant start, Instant end, int page, int size);

    Page<Message> findByNetwork(String tenantId, String network, int page, int size);

    Page<Message> findByFlaggedStatus(String tenantId, boolean flagged, int page, int size);

    Page<Message> findByTeam(String tenantId, String team, int page, int size);

    Page<Message> findByChannel(String tenantId, String channel, int page, int size);

    Page<Message> findByParticipantId(String tenantId, String participantId, int page, int size);

    Page<Message> findByParticipantRole(String tenantId, String role, int page, int size);

    Page<Message> findByFlagDescription(String tenantId, String flagDescription, int page, int size);

    Page<Message> findByRuleId(String tenantId, String ruleId, int page, int size);
}                       