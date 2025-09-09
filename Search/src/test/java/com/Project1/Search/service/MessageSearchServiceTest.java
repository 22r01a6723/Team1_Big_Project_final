package com.Project1.Search.service;


import com.Project1.Search.dto.SearchRequest;
import com.Project1.Search.model.Message;
import com.Project1.Search.repository.MessageCustomRepository;
import com.Project1.Search.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MessageSearchServiceImplTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageCustomRepository messageCustomRepository;

    @InjectMocks
    private MessageSearchServiceImpl searchService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchMessagesWithEmptyResult() {
        SearchRequest request = new SearchRequest();
        request.setTenantId("tenant1");
        request.setKeyword("doesnotexist");

        when(messageRepository.fullTextSearch(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(Page.empty());

        Page<Message> result = searchService.searchMessages(request);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchByTimeRangeEmptyResult() {
        Instant start = Instant.now().minusSeconds(7200);
        Instant end = Instant.now().minusSeconds(3600);

        when(messageRepository.findByTenantIdAndTimestampBetween(
                eq("tenant1"), any(Instant.class), any(Instant.class), any(Pageable.class)))
                .thenReturn(Page.empty());

        Page<Message> result = searchService.searchByTimeRange("tenant1", start, end, 0, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSearchMessagesDelegatesToAdvancedSearchWhenNoKeyword() {
        SearchRequest request = new SearchRequest();
        request.setTenantId("tenant1");
        request.setNetwork("email");

        Page<Message> mockPage = new PageImpl<>(List.of(
                Message.builder().messageId("msg1").network("email").build()
        ));

        when(messageCustomRepository.advancedSearch(eq(request), any(Pageable.class)))
                .thenReturn(mockPage);

        Page<Message> result = searchService.searchMessages(request);

        assertEquals(1, result.getContent().size());
        assertEquals("email", result.getContent().get(0).getNetwork());
    }

    @Test
    void testSearchByTimeRangeWithMultipleMessages() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();

        List<Message> messages = List.of(
                Message.builder().messageId("msg1").timestamp(Instant.now()).build(),
                Message.builder().messageId("msg2").timestamp(Instant.now()).build()
        );

        when(messageRepository.findByTenantIdAndTimestampBetween(
                eq("tenant1"), any(Instant.class), any(Instant.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(messages));

        Page<Message> result = searchService.searchByTimeRange("tenant1", start, end, 0, 10);

        assertEquals(2, result.getContent().size());
        assertEquals("msg2", result.getContent().get(1).getMessageId());
    }


    @Test
    void testSearchMessagesWithKeyword() {
        SearchRequest request = new SearchRequest();
        request.setTenantId("tenant1");
        request.setKeyword("test");

        Page<Message> mockPage = new PageImpl<>(List.of(
                Message.builder().messageId("msg1").build()
        ));

        when(messageRepository.fullTextSearch(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(mockPage);

        Page<Message> result = searchService.searchMessages(request);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testSearchMessagesWithComplexCriteria() {
        SearchRequest request = new SearchRequest();
        request.setTenantId("tenant1");
        request.setNetwork("slack");
        request.setFlagged(true);

        Page<Message> mockPage = new PageImpl<>(List.of(
                Message.builder().messageId("msg1").network("slack").flagged(true).build()
        ));

        when(messageCustomRepository.advancedSearch(any(SearchRequest.class), any(Pageable.class)))
                .thenReturn(mockPage);

        Page<Message> result = searchService.searchMessages(request);

        assertNotNull(result);
        assertEquals("slack", result.getContent().get(0).getNetwork());
        assertTrue(result.getContent().get(0).isFlagged());
    }

    @Test
    void testSearchMessagesWithoutTenantId() {
        SearchRequest request = new SearchRequest();
        request.setKeyword("test");

        assertThrows(IllegalArgumentException.class, () -> {
            searchService.searchMessages(request);
        });
    }


    @Test
    void testSearchByTimeRange() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();

        Page<Message> mockPage = new PageImpl<>(List.of(
                Message.builder().messageId("msg1").timestamp(Instant.now()).build()
        ));

        when(messageRepository.findByTenantIdAndTimestampBetween(
                eq("tenant1"), any(Instant.class), any(Instant.class), any(Pageable.class)))
                .thenReturn(mockPage);

        Page<Message> result = searchService.searchByTimeRange("tenant1", start, end, 0, 20);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }
}
