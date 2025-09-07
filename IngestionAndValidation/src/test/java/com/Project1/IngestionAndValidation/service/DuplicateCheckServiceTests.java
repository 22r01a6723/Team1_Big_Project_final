package com.Project1.IngestionAndValidation.service;


import com.Project1.IngestionAndValidation.exception.CompanyVaultPersistenceException;
import com.Project1.IngestionAndValidation.repository.UniqueIdRepository;
import com.Project1.IngestionAndValidation.services.DuplicateCheckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DuplicateCheckServiceTests {

    @Mock
    private UniqueIdRepository uniqueIdRepository;

    private DuplicateCheckService duplicateCheckService;

    @BeforeEach
    void setUp() {
        duplicateCheckService = new DuplicateCheckService(uniqueIdRepository);
    }

    @Test
    void testIsDuplicate_MessageExists_ReturnsTrue() {
        when(uniqueIdRepository.existsById("msg-1")).thenReturn(true);

        boolean result = duplicateCheckService.isDuplicate("msg-1");

        assertTrue(result);
        verify(uniqueIdRepository).existsById("msg-1");
    }

    @Test
    void testIsDuplicate_MessageNotExists_ReturnsFalse() {
        when(uniqueIdRepository.existsById("msg-1")).thenReturn(false);

        boolean result = duplicateCheckService.isDuplicate("msg-1");

        assertFalse(result);
        verify(uniqueIdRepository).existsById("msg-1");
    }
    @Test
    void testIsDuplicate_NullMessageId_ReturnsFalse() {
        boolean result = duplicateCheckService.isDuplicate(null);

        assertFalse(result);
        verify(uniqueIdRepository).existsById(null);
    }


    @Test
    void testIsDuplicate_CallsRepositoryExactlyOnce() {
        when(uniqueIdRepository.existsById("msg-3")).thenReturn(false);

        duplicateCheckService.isDuplicate("msg-3");

        verify(uniqueIdRepository, times(1)).existsById("msg-3");
    }

    @Test
    void testIsDuplicate_DifferentIds() {
        when(uniqueIdRepository.existsById("id1")).thenReturn(true);
        when(uniqueIdRepository.existsById("id2")).thenReturn(false);

        assertTrue(duplicateCheckService.isDuplicate("id1"));
        assertFalse(duplicateCheckService.isDuplicate("id2"));
    }

    @Test
    void testIsDuplicate_RepositoryCalledWithCorrectArgument() {
        when(uniqueIdRepository.existsById(anyString())).thenReturn(false);

        duplicateCheckService.isDuplicate("custom-msg");

        verify(uniqueIdRepository).existsById("custom-msg");
    }

    @Test
    void testIsDuplicate_UpperCaseAndLowerCaseDifferent() {
        when(uniqueIdRepository.existsById("MSG-100")).thenReturn(true);
        when(uniqueIdRepository.existsById("msg-100")).thenReturn(false);

        assertTrue(duplicateCheckService.isDuplicate("MSG-100"));
        assertFalse(duplicateCheckService.isDuplicate("msg-100"));
    }

    @Test
    void testIsDuplicate_MultipleCalls() {
        when(uniqueIdRepository.existsById("msg-5")).thenReturn(true);

        boolean firstCall = duplicateCheckService.isDuplicate("msg-5");
        boolean secondCall = duplicateCheckService.isDuplicate("msg-5");

        assertTrue(firstCall);
        assertTrue(secondCall);
        verify(uniqueIdRepository, times(2)).existsById("msg-5");
    }

    @Test
    void testIsDuplicate_LongMessageId() {
        String longId = "x".repeat(1000);
        when(uniqueIdRepository.existsById(longId)).thenReturn(false);

        boolean result = duplicateCheckService.isDuplicate(longId);

        assertFalse(result);
        verify(uniqueIdRepository).existsById(longId);
    }

}