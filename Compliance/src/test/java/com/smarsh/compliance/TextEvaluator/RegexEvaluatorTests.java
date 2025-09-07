





package com.smarsh.compliance.TextEvaluator;


import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.entity.PolicyCondition;
import com.smarsh.compliance.entity.RegexPolicy;
import com.smarsh.compliance.evaluators.RegexEvaluator;
import com.smarsh.compliance.models.Message;
import com.smarsh.compliance.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegexEvaluatorTest {

    @Mock
    private AuditService auditService;

    private RegexEvaluator regexEvaluator;

    @BeforeEach
    void setUp() {
        regexEvaluator = new RegexEvaluator(auditService);
    }

    @Test
    void testSupports_RegexType_ReturnsTrue() {
        assertTrue(regexEvaluator.supports("regex"));
        assertTrue(regexEvaluator.supports("REGEX"));
    }

    @Test
    void testSupports_NonRegexType_ReturnsFalse() {
        assertFalse(regexEvaluator.supports("keyword"));
        assertFalse(regexEvaluator.supports("other"));
    }

    @Test
    void testEvaluate_EmailPatternInBody_ReturnsFlag() {
        Message message = createTestMessage("Test", "Contact me at test@example.com");
        Policy policy = createRegexPolicy("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b", "body");

        Optional<?> result = regexEvaluator.evaluate(message, policy);

        assertTrue(result.isPresent());
        verify(auditService).logEvent(anyString(), anyString(), anyString(), eq("MESSAGE_FLAGGED"), anyMap());
    }

    @Test
    void testEvaluate_CreditCardPatternInBody_ReturnsFlag() {
        Message message = createTestMessage("Payment", "Card: 4111-1111-1111-1111");
        Policy policy = createRegexPolicy("\\b(?:\\d{4}[- ]?){3}\\d{4}\\b", "body");

        Optional<?> result = regexEvaluator.evaluate(message, policy);

        assertTrue(result.isPresent());
    }

    @Test
    void testEvaluate_NoPatternMatch_ReturnsEmpty() {
        Message message = createTestMessage("Test", "Normal content without patterns");
        Policy policy = createRegexPolicy("\\b\\d{3}-\\d{2}-\\d{4}\\b", "body"); // SSN pattern

        Optional<?> result = regexEvaluator.evaluate(message, policy);

        assertFalse(result.isPresent());
        verify(auditService, never()).logEvent(any(), any(), any(), any(), any());
    }

    @Test
    void testEvaluate_NullFieldValue_ReturnsEmpty() {
        Message message = createTestMessage(null, null);
        Policy policy = createRegexPolicy("pattern", "body");

        Optional<?> result = regexEvaluator.evaluate(message, policy);

        assertFalse(result.isPresent());
    }

    @Test
    void testEvaluate_InvalidRegexPattern_ThrowsException() {
        Message message = createTestMessage("Test", "Content");
        Policy policy = createRegexPolicy("[invalid-regex", "body");

        assertThrows(PatternSyntaxException.class, () -> {
            regexEvaluator.evaluate(message, policy);
        });
    }

    @Test
    void testEvaluate_UnknownField_ReturnsEmpty() {
        Message message = createTestMessage("Test", "Content");
        Policy policy = createRegexPolicy("pattern", "unknown");

        Optional<?> result = regexEvaluator.evaluate(message, policy);

        assertFalse(result.isPresent());
    }
    @Test
    void testEvaluate_RegexInSubject_ReturnsFlag() {
        Message message = createTestMessage("Urgent: Call 123-456-7890", "Body without number");
        Policy policy = createRegexPolicy("\\d{3}-\\d{3}-\\d{4}", "subject");

        Optional<?> result = regexEvaluator.evaluate(message, policy);

        assertTrue(result.isPresent(), "Expected regex match in subject field");
    }

    @Test
    void testEvaluate_MultipleMatchesInBody_ReturnsFlag() {
        Message message = createTestMessage("Test", "Call me at 123-456-7890 or 987-654-3210");
        Policy policy = createRegexPolicy("\\d{3}-\\d{3}-\\d{4}", "body");

        Optional<?> result = regexEvaluator.evaluate(message, policy);

        assertTrue(result.isPresent(), "Expected flag when multiple matches are present");
    }

    @Test
    void testEvaluate_EmptyBodyAndSubject_ReturnsEmpty() {
        Message message = createTestMessage("", "");
        Policy policy = createRegexPolicy("\\d+", "body");

        Optional<?> result = regexEvaluator.evaluate(message, policy);

        assertFalse(result.isPresent(), "Expected empty result when no content exists");
    }

    @Test
    void testEvaluate_URLPatternInBody_ReturnsFlag() {
        Message message = createTestMessage("Links", "Visit https://example.com for details");
        Policy policy = createRegexPolicy("https?://\\S+", "body");

        Optional<?> result = regexEvaluator.evaluate(message, policy);

        assertTrue(result.isPresent(), "Expected regex match for URL pattern");
    }

    @Test
    void testEvaluate_RegexWithWhitespaceTolerance_ReturnsFlag() {
        Message message = createTestMessage("Payment", "Account number: 1234 5678 9012 3456");
        Policy policy = createRegexPolicy("\\b(?:\\d{4}\\s?){4}\\b", "body");

        Optional<?> result = regexEvaluator.evaluate(message, policy);

        assertTrue(result.isPresent(), "Expected match despite whitespace in digits");
    }

    @Test
    void testEvaluate_CaseInsensitiveRegex_ReturnsFlag() {
        Message message = createTestMessage("Test", "This contains SECRET info");
        Policy policy = createRegexPolicy("(?i)secret", "body");

        Optional<?> result = regexEvaluator.evaluate(message, policy);

        assertTrue(result.isPresent(), "Expected case-insensitive match for 'secret'");
    }


    private Message createTestMessage(String subject, String body) {
        return Message.builder()
                .messageId("msg-1")
                .tenantId("tenant-1")
                .network("email")
                .timestamp(Instant.now())
                .content(Message.Content.builder()
                        .subject(subject)
                        .body(body)
                        .build())
                .build();
    }

    private Policy createRegexPolicy(String pattern, String field) {
        RegexPolicy policy = new RegexPolicy();
        policy.setRuleId("regex-policy");
        policy.setType("regex");
        policy.setField(field);
        policy.setDescription("Regex test policy");
        policy.setPattern(pattern);

        PolicyCondition condition = new PolicyCondition();
        condition.setNetworkEquals("email");
        policy.setWhen(condition);

        return policy;
    }
}