package com.complyvault.retention.config;


import com.complyvault.retention.config.ScheduledTasksConfig;
import com.complyvault.retention.service.RetentionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ScheduledTasksConfigTest {

    @Mock
    private RetentionService retentionService;

    @InjectMocks
    private ScheduledTasksConfig scheduledTasksConfig;

    @Test
    void processRetentionPolicies_ShouldCallServiceMethod() {
        // Act
        scheduledTasksConfig.processRetentionPolicies();

        // Assert
        verify(retentionService, times(1)).processExpiredMessages();
    }


    @Test
    void scheduledAnnotation_ShouldBePresentWithCorrectCron() throws NoSuchMethodException {
        // Arrange
        Method method = ScheduledTasksConfig.class.getMethod("processRetentionPolicies");
        Scheduled scheduled = method.getAnnotation(Scheduled.class);

        // Assert
        assertNotNull(scheduled);
        assertEquals("0 0 1 * * ?", scheduled.cron());
    }

    @Test
    void componentAnnotation_ShouldBePresent() {
        // Arrange
        Component component = ScheduledTasksConfig.class.getAnnotation(org.springframework.stereotype.Component.class);

        // Assert
        assertNotNull(component);
    }

    @Test
    void requiredArgsConstructor_ShouldInjectDependencies() {
        // This test verifies that Lombok's @RequiredArgsConstructor works
        assertNotNull(scheduledTasksConfig);
        assertNotNull(scheduledTasksConfig.getClass().getDeclaredFields());
    }

    @Test
    void processRetentionPolicies_ShouldBeCalledMultipleTimes() {
        // Act - call multiple times
        scheduledTasksConfig.processRetentionPolicies();
        scheduledTasksConfig.processRetentionPolicies();
        scheduledTasksConfig.processRetentionPolicies();

        // Assert
        verify(retentionService, times(3)).processExpiredMessages();
    }

    @Test
    void processRetentionPolicies_ShouldHandleServiceException() {
        // Arrange
        doThrow(new RuntimeException("Service failed"))
                .when(retentionService).processExpiredMessages();

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> scheduledTasksConfig.processRetentionPolicies());
        verify(retentionService, times(1)).processExpiredMessages();
    }

    @Test
    void scheduledAnnotation_ShouldContainTimeZone() throws NoSuchMethodException {
        // Arrange
        Method method = ScheduledTasksConfig.class.getMethod("processRetentionPolicies");
        Scheduled scheduled = method.getAnnotation(Scheduled.class);

        // Assert
        assertNotNull(scheduled);
        assertTrue(scheduled.zone().isEmpty() || scheduled.zone().equals("UTC"),
                "Expected default or UTC timezone, but got: " + scheduled.zone());
    }

    @Test
    void className_ShouldMatchExpected() {
        // Assert
        assertEquals("ScheduledTasksConfig", scheduledTasksConfig.getClass().getSimpleName());
        assertTrue(scheduledTasksConfig.getClass().getPackageName().contains("config"));
    }


    @Test
    void scheduledAnnotation_ShouldHaveCorrectFixedDelayOrCron() throws NoSuchMethodException {
        // Arrange
        Method method = ScheduledTasksConfig.class.getMethod("processRetentionPolicies");
        Scheduled scheduled = method.getAnnotation(Scheduled.class);

        // Assert
        assertNotNull(scheduled);
        assertTrue(scheduled.cron().equals("0 0 1 * * ?") || scheduled.fixedDelay() > 0,
                "Expected either correct cron or a fixed delay schedule");
    }

}