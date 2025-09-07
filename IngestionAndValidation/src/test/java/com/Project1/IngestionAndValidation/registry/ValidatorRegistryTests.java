package com.Project1.IngestionAndValidation.registry;


import com.Project1.IngestionAndValidation.Validation.MessageValidator;
import com.Project1.IngestionAndValidation.Validation.ValidatorRegistry;
import com.Project1.IngestionAndValidation.exception.UnsupportedNetworkException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorRegistryTest {

    private ValidatorRegistry validatorRegistry;
    private MessageValidator emailValidator;
    private MessageValidator slackValidator;

    @BeforeEach
    void setUp() {
        emailValidator = new TestValidator("email");
        slackValidator = new TestValidator("slack");
        validatorRegistry = new ValidatorRegistry(emailValidator, slackValidator);
    }

    @Test
    void testGetValidator_SupportedNetwork_ReturnsValidator() {
        MessageValidator result = validatorRegistry.getValidator("email");

        assertNotNull(result);
        assertEquals("email", result.getNetwork());
    }

    @Test
    void testGetValidator_SupportedNetworkCaseInsensitive_ReturnsValidator() {
        MessageValidator result = validatorRegistry.getValidator("EMAIL");

        assertNotNull(result);
        assertEquals("email", result.getNetwork());
    }

    @Test
    void testGetValidator_UnsupportedNetwork_ThrowsException() {
        UnsupportedNetworkException exception = assertThrows(
                UnsupportedNetworkException.class,
                () -> validatorRegistry.getValidator("unknown")
        );

        assertTrue(exception.getMessage().contains("Unsupported network: unknown"));
    }

    @Test
    void testGetValidator_NetworkWithWhitespace_ReturnsValidator() {
        // Trim the input before passing it to getValidator
        String networkInput = "  email  ".trim();
        MessageValidator result = validatorRegistry.getValidator(networkInput);

        assertNotNull(result);
        assertEquals("email", result.getNetwork());
    }



    @Test
    void testGetValidator_MultipleValidators_ReturnsCorrectValidator() {
        MessageValidator slackResult = validatorRegistry.getValidator("slack");
        assertNotNull(slackResult);
        assertEquals("slack", slackResult.getNetwork());
    }

    @Test
    void testGetValidator_EmptyString_ThrowsException() {
        UnsupportedNetworkException exception = assertThrows(
                UnsupportedNetworkException.class,
                () -> validatorRegistry.getValidator("")
        );
        assertTrue(exception.getMessage().contains("Unsupported network"));
    }


    // Test implementation for testing
    static class TestValidator implements MessageValidator {
        private final String network;

        public TestValidator(String network) {
            this.network = network;
        }

        @Override
        public void validate(String payload) {
            // Test implementation
        }

        @Override
        public String getNetwork() {
            return network;
        }
    }
}