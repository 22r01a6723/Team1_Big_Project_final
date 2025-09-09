package com.Project1.Search.controller;


import com.Project1.Search.dto.SearchRequest;
import com.Project1.Search.model.Message;
import com.Project1.Search.service.MessageSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SearchControllerTest {

    @Mock
    private MessageSearchService searchService;

    @InjectMocks
    private SearchController searchController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchWithValidRequest() {
        SearchRequest request = new SearchRequest();
        request.setTenantId("tenant1");
        request.setKeyword("test");

        Page<Message> mockPage = new PageImpl<>(List.of(
                Message.builder().messageId("msg1").build(),
                Message.builder().messageId("msg2").build()
        ));

        when(searchService.searchMessages(request)).thenReturn(mockPage);

        ResponseEntity<Page<Message>> response = searchController.search(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getContent().size());
    }

    @Test
    void testFullTextSearch() {
        Page<Message> mockPage = new PageImpl<>(List.of(
                Message.builder().messageId("msg1").build()
        ));

        when(searchService.fullTextSearch("test", "tenant1", 0, 20))
                .thenReturn(mockPage);

        ResponseEntity<Page<Message>> response = searchController.fullTextSearch(
                "tenant1", "test", 0, 20);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    void testSearchByTimeRange() {
        Instant start = Instant.now().minusSeconds(3600);
        Instant end = Instant.now();

        Page<Message> mockPage = new PageImpl<>(List.of(
                Message.builder().messageId("msg1").build()
        ));

        when(searchService.searchByTimeRange("tenant1", start, end, 0, 20))
                .thenReturn(mockPage);

        ResponseEntity<Page<Message>> response = searchController.searchByTimeRange(
                "tenant1", start, end, 0, 20);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testFindByNetwork() {
        Page<Message> mockPage = new PageImpl<>(List.of(
                Message.builder().messageId("msg1").network("slack").build()
        ));

        when(searchService.findByNetwork("tenant1", "slack", 0, 20))
                .thenReturn(mockPage);

        ResponseEntity<Page<Message>> response = searchController.findByNetwork(
                "tenant1", "slack", 0, 20);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("slack", response.getBody().getContent().get(0).getNetwork());
    }

    @Test
    void testSearchWithException() {
        SearchRequest request = new SearchRequest();
        request.setTenantId("tenant1");

        when(searchService.searchMessages(request)).thenThrow(new RuntimeException("Error"));

        ResponseEntity<Page<Message>> response = searchController.search(request);

        assertEquals(400, response.getStatusCodeValue());
    }
    @Test
    void testFullTextSearchReturnsEmptyPage() {
        when(searchService.fullTextSearch("empty", "tenant1", 0, 20))
                .thenReturn(Page.empty());

        ResponseEntity<Page<Message>> response = searchController.fullTextSearch(
                "tenant1", "empty", 0, 20);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testSearchByTimeRangeWithNoResults() {
        Instant start = Instant.now().minusSeconds(7200);
        Instant end = Instant.now().minusSeconds(3600);

        when(searchService.searchByTimeRange("tenant1", start, end, 0, 20))
                .thenReturn(Page.empty());

        ResponseEntity<Page<Message>> response = searchController.searchByTimeRange(
                "tenant1", start, end, 0, 20);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void testFindByNetworkWithMultipleResults() {
        Page<Message> mockPage = new PageImpl<>(List.of(
                Message.builder().messageId("msg1").network("slack").build(),
                Message.builder().messageId("msg2").network("slack").build()
        ));

        when(searchService.findByNetwork("tenant1", "slack", 0, 20))
                .thenReturn(mockPage);

        ResponseEntity<Page<Message>> response = searchController.findByNetwork(
                "tenant1", "slack", 0, 20);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().getContent().size());
    }


}
