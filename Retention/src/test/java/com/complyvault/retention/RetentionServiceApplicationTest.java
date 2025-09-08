package com.complyvault.retention;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RetentionServiceApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring application context loads successfully
    }

    @Test
    void mainMethod_ShouldStartApplication() {
        // Test that the main method can be called without exceptions
        assertDoesNotThrow(() -> RetentionServiceApplication.main(new String[]{}));
    }

    @Test
    void springBootApplicationAnnotation_ShouldBePresent() {
        // Verify that the @SpringBootApplication annotation is properly configured
        assertNotNull(RetentionServiceApplication.class.getAnnotation(org.springframework.boot.autoconfigure.SpringBootApplication.class));
    }

    @Test
    void enableSchedulingAnnotation_ShouldBePresent() {
        // Verify that the @EnableScheduling annotation is present
        assertNotNull(RetentionServiceApplication.class.getAnnotation(EnableScheduling.class));
    }

    @Test
    void application_ShouldHaveMainMethod() {
        // Verify that the main method exists
        assertDoesNotThrow(() -> {
            RetentionServiceApplication.class.getMethod("main", String[].class);
        });
    }

    @Test
    void applicationProperties_ShouldBeLoaded() {
        // Test that application properties are loaded correctly
        // This is implicitly tested by the @SpringBootTest annotation
    }
}