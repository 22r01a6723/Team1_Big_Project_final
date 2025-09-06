package com.Project1.Search.service;

import com.Project1.Search.dto.SearchRequest;
import com.Project1.Search.model.Message;
import com.Project1.Search.repository.MessageCustomRepository;
import com.Project1.Search.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageSearchServiceImpl implements MessageSearchService {

    private final MessageRepository messageRepository;
    private final MessageCustomRepository messageCustomRepository;

    @Override
    public Page<Message> searchMessages(SearchRequest request) throws IllegalArgumentException {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(),
                Sort.by(Sort.Direction.DESC, "timestamp"));

        // Validate tenantId
        if (request.getTenantId() == null || request.getTenantId().isEmpty()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }

        // Keyword search (highest priority) - uses simple repository
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            return messageRepository.fullTextSearch(
                    request.getKeyword(),
                    request.getTenantId(),
                    pageable
            );
        }
        System.out.println("Searching in custom "+request);
        // Use custom repository for complex multi-criteria searches
        return messageCustomRepository.advancedSearch(request, pageable);
    }

    // Add this method to SearchService


    @Override
    public Page<Message> fullTextSearch(String keyword, String tenantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return messageRepository.fullTextSearch(keyword, tenantId, pageable);
    }

    @Override
    public Page<Message> searchByField(String field, String value, String tenantId, boolean exactMatch, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));

        if (exactMatch) {
            return messageRepository.exactMatchSearch(field, value, tenantId, pageable);
        } else {
            return messageRepository.partialMatchSearch(field, value, tenantId, pageable);
        }
    }

    @Override
    public Page<Message> searchByTimeRange(String tenantId, Instant start, Instant end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return messageRepository.findByTenantIdAndTimestampBetween(
                tenantId,
                Optional.ofNullable(start).orElse(Instant.MIN),
                Optional.ofNullable(end).orElse(Instant.now()),
                pageable
        );
    }

    @Override
    public Page<Message> searchByFlagTimeRange(String tenantId, Instant start, Instant end, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "flagInfo.timestamp"));
        return messageRepository.findByTenantIdAndFlagInfoTimestampBetween(
                tenantId,
                Optional.ofNullable(start).orElse(Instant.MIN),
                Optional.ofNullable(end).orElse(Instant.now()),
                pageable
        );
    }

    // Additional specific search methods that use the simple repository
    public Page<Message> findByNetwork(String tenantId, String network, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return messageRepository.findByTenantIdAndNetwork(tenantId, network, pageable);
    }

    public Page<Message> findByFlaggedStatus(String tenantId, boolean flagged, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return messageRepository.findByTenantIdAndFlagged(tenantId, flagged, pageable);
    }

//    public Page<Message> findByTeam(String tenantId, String team, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
//        return messageRepository.findByTeam(team, tenantId, pageable);
//    }

    public Page<Message> findByTeam(String tenantId, String team, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return messageRepository.findByTeam(team,tenantId, pageable);
    }

    public Page<Message> findByChannel(String tenantId, String channel, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return messageRepository.findByChannel(channel, tenantId, pageable);
    }

    public Page<Message> findByParticipantId(String tenantId, String participantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return messageRepository.findByParticipantId(participantId, tenantId, pageable);
    }

    public Page<Message> findByParticipantRole(String tenantId, String role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return messageRepository.findByParticipantRole(role, tenantId, pageable);
    }

    public Page<Message> findByFlagDescription(String tenantId, String flagDescription, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return messageRepository.findByFlagDescription(flagDescription, tenantId, pageable);
    }

    public Page<Message> findByRuleId(String tenantId, String ruleId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        return messageRepository.findByRuleId(ruleId, tenantId, pageable);
    }
}