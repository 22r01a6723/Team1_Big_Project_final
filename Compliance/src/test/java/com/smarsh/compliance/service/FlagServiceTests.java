package com.smarsh.compliance.service;

import com.smarsh.compliance.entity.Flag;
import com.smarsh.compliance.repository.FlagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlagServiceTest {

    @Mock
    private FlagRepository flagRepository;

    private FlagService flagService;

    @BeforeEach
    void setUp() {
        flagService = new FlagService(flagRepository);
    }

    @Test
    void testSaveFlag_CallsRepositorySave() {
        Flag flag = new Flag("rule-1", "msg-1", "Test flag", "email", "tenant-1");

        flagService.saveFlag(flag);

        verify(flagRepository).save(flag);
    }

    @Test
    void testSaveFlag_NullFlag_DoesNotThrowException() {
        assertDoesNotThrow(() -> flagService.saveFlag(null));
    }

    // ---------- Additional Test Cases ----------

    @Test
    void testSaveFlag_WithEmptyRuleId_SavesAnyway() {
        Flag flag = new Flag("", "msg-2", "Empty rule", "chat", "tenant-2");

        flagService.saveFlag(flag);

        verify(flagRepository).save(flag);
    }

    @Test
    void testSaveFlag_WithEmptyMessageId_SavesAnyway() {
        Flag flag = new Flag("rule-2", "", "Empty msg", "email", "tenant-3");

        flagService.saveFlag(flag);

        verify(flagRepository).save(flag);
    }

    @Test
    void testSaveFlag_WithNullFields_SavesAnyway() {
        Flag flag = new Flag(null, null, null, null, null);

        flagService.saveFlag(flag);

        verify(flagRepository).save(flag);
    }

    @Test
    void testSaveFlag_CallsRepositorySaveOnce() {
        Flag flag = new Flag("rule-3", "msg-3", "Description", "sms", "tenant-4");

        flagService.saveFlag(flag);

        verify(flagRepository, times(1)).save(flag);
    }

    @Test
    void testSaveFlag_MultipleFlags_SaveCalledForEach() {
        Flag flag1 = new Flag("rule-4", "msg-4", "First", "email", "tenant-5");
        Flag flag2 = new Flag("rule-5", "msg-5", "Second", "chat", "tenant-5");

        flagService.saveFlag(flag1);
        flagService.saveFlag(flag2);

        verify(flagRepository, times(1)).save(flag1);
        verify(flagRepository, times(1)).save(flag2);
    }



    @Test
    void testSaveFlag_DifferentTenants_SaveWorks() {
        Flag flag1 = new Flag("rule-7", "msg-7", "Tenant 1", "email", "tenant-A");
        Flag flag2 = new Flag("rule-8", "msg-8", "Tenant 2", "sms", "tenant-B");

        flagService.saveFlag(flag1);
        flagService.saveFlag(flag2);

        verify(flagRepository).save(flag1);
        verify(flagRepository).save(flag2);
    }

    @Test
    void testSaveFlag_LongDescription_SavesCorrectly() {
        String longDesc = "A".repeat(500);
        Flag flag = new Flag("rule-9", "msg-9", longDesc, "chat", "tenant-9");

        flagService.saveFlag(flag);

        verify(flagRepository).save(flag);
    }

    @Test
    void testSaveFlag_MultipleCalls_VerifyCallCount() {
        Flag flag = new Flag("rule-10", "msg-10", "Repeated", "email", "tenant-10");

        for (int i = 0; i < 5; i++) {
            flagService.saveFlag(flag);
        }

        verify(flagRepository, times(5)).save(flag);
    }
}
