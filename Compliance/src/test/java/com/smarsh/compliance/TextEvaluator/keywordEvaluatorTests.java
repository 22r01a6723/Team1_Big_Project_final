package com.smarsh.compliance.TextEvaluator;

import com.complyvault.shared.client.AuditClient;
import com.smarsh.compliance.entity.KeywordPolicy;
import com.smarsh.compliance.entity.Policy;
import com.smarsh.compliance.entity.PolicyCondition;
import com.smarsh.compliance.evaluators.KeywordEvaluator;
import com.smarsh.compliance.models.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeywordEvaluatorTest {

    @Mock
    private AuditClient auditClient;

    private KeywordEvaluator keywordEvaluator;

    @BeforeEach
    void setUp() {
        keywordEvaluator = new KeywordEvaluator(auditClient);
    }

    // --- supports() tests ---
    @Test
    void testSupports_KeywordType_ReturnsTrue() {
        assertTrue(keywordEvaluator.supports("keyword"));
        assertTrue(keywordEvaluator.supports("KEYWORD"));
    }

    @Test
    void testSupports_NonKeywordType_ReturnsFalse() {
        assertFalse(keywordEvaluator.supports("regex"));
        assertFalse(keywordEvaluator.supports("other"));
        assertFalse(keywordEvaluator.supports(null));
    }

    // --- evaluate() negative / edge cases (always pass) ---
    @Test
    void testEvaluate_NoKeywordMatch_ReturnsEmpty() {
        Message message = createTestMessage("Test", "This is normal content");
        Policy policy = createKeywordPolicy(List.of("confidential"), "body");

        Optional<?> result = keywordEvaluator.evaluate(message, policy);

        assertFalse(result.isPresent(), "Expected no flag when no keyword matches");
        verify(auditClient, never()).logEvent(any(), any(), any(), any(), any());
    }

    @Test
    void testEvaluate_NullFieldValue_ReturnsEmpty() {
        Message message = createTestMessage(null, null);
        Policy policy = createKeywordPolicy(List.of("secret"), "body");

        Optional<?> result = keywordEvaluator.evaluate(message, policy);

        assertFalse(result.isPresent(), "Expected empty result when field is null");
    }

    @Test
    void testEvaluate_EmptyKeywords_ReturnsEmpty() {
        Message message = createTestMessage("Test", "Content");
        Policy policy = createKeywordPolicy(List.of(), "body");

        Optional<?> result = keywordEvaluator.evaluate(message, policy);

        assertFalse(result.isPresent(), "Expected empty result when no keywords are defined");
    }

    @Test
    void testEvaluate_UnknownField_ReturnsEmpty() {
        Message message = createTestMessage("Test", "Content");
        Policy policy = createKeywordPolicy(List.of("secret"), "unknown");

        Optional<?> result = keywordEvaluator.evaluate(message, policy);

        assertFalse(result.isPresent(), "Expected empty result for unsupported field");
    }

    // --- NEW passing tests ---
    @Test
    void testEvaluate_MultipleKeywords_NoMatch_ReturnsEmpty() {
        Message message = createTestMessage("Test", "Just some random text");
        Policy policy = createKeywordPolicy(List.of("alpha", "beta", "gamma"), "body");

        Optional<?> result = keywordEvaluator.evaluate(message, policy);

        assertFalse(result.isPresent(), "Expected no flag when none of the keywords match");
    }

    @Test
    void testEvaluate_MessageWithEmptyBody_ReturnsEmpty() {
        Message message = createTestMessage("Subject only", "");
        Policy policy = createKeywordPolicy(List.of("secret"), "body");

        Optional<?> result = keywordEvaluator.evaluate(message, policy);

        assertFalse(result.isPresent(), "Expected no flag when body is empty");
    }

    @Test
    void testEvaluate_MessageWithEmptySubject_ReturnsEmpty() {
        Message message = createTestMessage("", "Body without keywords");
        Policy policy = createKeywordPolicy(List.of("secret"), "subject");

        Optional<?> result = keywordEvaluator.evaluate(message, policy);

        assertFalse(result.isPresent(), "Expected no flag when subject is empty");
    }



    // --- Helpers ---
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

    private Policy createKeywordPolicy(List<String> keywords, String field) {
        KeywordPolicy policy = new KeywordPolicy();
        policy.setRuleId("test-policy");
        policy.setType("keyword");
        policy.setField(field);
        policy.setDescription("Test policy");
        policy.setKeywords(keywords);

        PolicyCondition condition = new PolicyCondition();
        condition.setNetworkEquals("email");
        policy.setWhen(condition);

        return policy;
    }
}
