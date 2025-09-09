package com.Project1.Search;


import com.Project1.Search.dto.SearchRequest;
import com.Project1.Search.model.Message;
import com.Project1.Search.repository.MessageCustomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.query.Query;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MessageCustomRepositoryTest {

    @Mock
    private ElasticsearchOperations elasticsearchOperations;

    @InjectMocks
    private MessageCustomRepository customRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAdvancedSearchWithKeyword() {
        SearchRequest request = new SearchRequest();
        request.setTenantId("tenant1");
        request.setKeyword("test");

        Pageable pageable = PageRequest.of(0, 20);

        SearchHits<Message> mockHits = mock(SearchHits.class);
        when(mockHits.getSearchHits()).thenReturn(List.of());
        when(mockHits.getTotalHits()).thenReturn(0L);

        when(elasticsearchOperations.search((Query) any(), eq(Message.class)))
                .thenReturn(mockHits);

        Page<Message> result = customRepository.advancedSearch(request, pageable);

        assertNotNull(result);
        assertEquals(0, result.getContent().size());
    }

    @Test
    void testAdvancedSearchWithMultipleCriteria() {
        SearchRequest request = new SearchRequest();
        request.setTenantId("tenant1");
        request.setNetwork("slack");
        request.setFlagged(true);
        request.setParticipantIds(List.of("user1", "user2"));

        Pageable pageable = PageRequest.of(0, 20);

        SearchHits<Message> mockHits = mock(SearchHits.class);
        when(mockHits.getSearchHits()).thenReturn(List.of());
        when(mockHits.getTotalHits()).thenReturn(0L);

        when(elasticsearchOperations.search((Query) any(), eq(Message.class)))
                .thenReturn(mockHits);

        Page<Message> result = customRepository.advancedSearch(request, pageable);

        assertNotNull(result);
    }

    @Test
    void testAdvancedSearchWithTimeRange() {
        SearchRequest request = new SearchRequest();
        request.setTenantId("tenant1");
        request.setStartTime(Instant.now().minusSeconds(3600));
        request.setEndTime(Instant.now());

        Pageable pageable = PageRequest.of(0, 20);

        SearchHits<Message> mockHits = mock(SearchHits.class);
        when(mockHits.getSearchHits()).thenReturn(List.of());
        when(mockHits.getTotalHits()).thenReturn(0L);

        when(elasticsearchOperations.search((Query) any(), eq(Message.class)))
                .thenReturn(mockHits);

        Page<Message> result = customRepository.advancedSearch(request, pageable);

        assertNotNull(result);
    }

    @Test
    void testAdvancedSearchWithSubjectExactMatch() {
        SearchRequest request = new SearchRequest();
        request.setTenantId("tenant1");
        request.setSubject("Important");
        request.setExactMatch(true);

        Pageable pageable = PageRequest.of(0, 20);

        SearchHits<Message> mockHits = mock(SearchHits.class);
        when(mockHits.getSearchHits()).thenReturn(List.of());
        when(mockHits.getTotalHits()).thenReturn(0L);

        when(elasticsearchOperations.search((Query) any(), eq(Message.class)))
                .thenReturn(mockHits);

        Page<Message> result = customRepository.advancedSearch(request, pageable);

        assertNotNull(result);
    }

    @Test
    void testAdvancedSearchWithFlagDescription() {
        SearchRequest request = new SearchRequest();
        request.setTenantId("tenant1");
        request.setFlagDescription("suspicious");

        Pageable pageable = PageRequest.of(0, 20);

        SearchHits<Message> mockHits = mock(SearchHits.class);
        when(mockHits.getSearchHits()).thenReturn(List.of());
        when(mockHits.getTotalHits()).thenReturn(0L);

        when(elasticsearchOperations.search((Query) any(), eq(Message.class)))
                .thenReturn(mockHits);

        Page<Message> result = customRepository.advancedSearch(request, pageable);

        assertNotNull(result);
    }
}
