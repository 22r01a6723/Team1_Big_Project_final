package com.Project1.Search.service;


import com.Project1.Search.dto.SearchRequest;
import com.Project1.Search.model.Message;
import com.Project1.Search.repository.MessageCustomRepository;
import com.Project1.Search.repository.MessageRepository;
import com.Project1.Search.util.PageableFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageSearchServiceImpl implements MessageSearchService {

    private final MessageRepository messageRepository;
    private final MessageCustomRepository messageCustomRepository;

    @Override
    public Page<Message> searchMessages(SearchRequest request) {
        validateRequest(request);

        Pageable pageable = PageableFactory.create(request.getPage(), request.getSize(), "timestamp");

        // Strategy: Keyword search takes priority
        if (hasText(request.getKeyword())) {
            return messageRepository.fullTextSearch(request.getKeyword(), request.getTenantId(), pageable);
        }

        // Fallback: advanced multi-filter search
        return messageCustomRepository.advancedSearch(request, pageable);
    }

    @Override
    public Page<Message> fullTextSearch(String keyword, String tenantId, int page, int size) {
        Pageable pageable = PageableFactory.create(page, size, "timestamp");
        return messageRepository.fullTextSearch(keyword, tenantId, pageable);
    }

    @Override
    public Page<Message> searchByField(String field, String value, String tenantId, boolean exactMatch, int page, int size) {
        Pageable pageable = PageableFactory.create(page, size, "timestamp");
        return exactMatch
                ? messageRepository.exactMatchSearch(field, value, tenantId, pageable)
                : messageRepository.partialMatchSearch(field, value, tenantId, pageable);
    }

    @Override
    public Page<Message> searchByTimeRange(String tenantId, Instant start, Instant end, int page, int size) {
        Pageable pageable = PageableFactory.create(page, size, "timestamp");
        return messageRepository.findByTenantIdAndTimestampBetween(
                tenantId,
                Optional.ofNullable(start).orElse(Instant.MIN),
                Optional.ofNullable(end).orElse(Instant.now()),
                pageable
        );
    }

    @Override
    public Page<Message> searchByFlagTimeRange(String tenantId, Instant start, Instant end, int page, int size) {
        Pageable pageable = PageableFactory.create(page, size, "flagInfo.timestamp");
        return messageRepository.findByTenantIdAndFlagInfoTimestampBetween(
                tenantId,
                Optional.ofNullable(start).orElse(Instant.MIN),
                Optional.ofNullable(end).orElse(Instant.now()),
                pageable
        );
    }

    public Page<Message> findByNetwork(String tenantId, String network, int page, int size) {
        return messageRepository.findByTenantIdAndNetwork(tenantId, network, PageableFactory.create(page, size, "timestamp"));
    }

    public Page<Message> findByFlaggedStatus(String tenantId, boolean flagged, int page, int size) {
        return messageRepository.findByTenantIdAndFlagged(tenantId, flagged, PageableFactory.create(page, size, "timestamp"));
    }

    public Page<Message> findByTeam(String tenantId, String team, int page, int size) {
        return messageRepository.findByTeam(team, tenantId, PageableFactory.create(page, size, "timestamp"));
    }

    public Page<Message> findByChannel(String tenantId, String channel, int page, int size) {
        return messageRepository.findByChannel(channel, tenantId, PageableFactory.create(page, size, "timestamp"));
    }

    public Page<Message> findByParticipantId(String tenantId, String participantId, int page, int size) {
        return messageRepository.findByParticipantId(participantId, tenantId, PageableFactory.create(page, size, "timestamp"));
    }

    public Page<Message> findByParticipantRole(String tenantId, String role, int page, int size) {
        return messageRepository.findByParticipantRole(role, tenantId, PageableFactory.create(page, size, "timestamp"));
    }

    public Page<Message> findByFlagDescription(String tenantId, String flagDescription, int page, int size) {
        return messageRepository.findByFlagDescription(flagDescription, tenantId, PageableFactory.create(page, size, "timestamp"));
    }

    public Page<Message> findByRuleId(String tenantId, String ruleId, int page, int size) {
        return messageRepository.findByRuleId(ruleId, tenantId, PageableFactory.create(page, size, "timestamp"));
    }

    // === Private helpers ===

    private void validateRequest(SearchRequest request) {
        if (!hasText(request.getTenantId())) {
            throw new IllegalArgumentException("Tenant ID is required");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
