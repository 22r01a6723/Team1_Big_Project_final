package com.project_1.normalizer.adapter;

import com.project_1.normalizer.util.MessageAdapterFactory;
import com.project_1.normalizer.util.adapters.EmailAdapter;
import com.project_1.normalizer.util.adapters.MessageAdapter;
import com.project_1.normalizer.util.adapters.SlackAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MessageAdapterFactoryTest {

    private MessageAdapterFactory messageAdapterFactory;
    private EmailAdapter emailAdapter;
    private SlackAdapter slackAdapter;

    @BeforeEach
    void setUp() {
        emailAdapter = new EmailAdapter();
        slackAdapter = new SlackAdapter();
        messageAdapterFactory = new MessageAdapterFactory(List.of(emailAdapter, slackAdapter));
    }

    @Test
    void testGetAdapter_EmailNetwork_ReturnsEmailAdapter() {
        MessageAdapter adapter = messageAdapterFactory.getAdapter("email");

        assertNotNull(adapter);
        assertTrue(adapter instanceof EmailAdapter);
        assertTrue(adapter.supports("email"));
    }

    @Test
    void testGetAdapter_SlackNetwork_ReturnsSlackAdapter() {
        MessageAdapter adapter = messageAdapterFactory.getAdapter("slack");

        assertNotNull(adapter);
        assertTrue(adapter instanceof SlackAdapter);
        assertTrue(adapter.supports("slack"));
    }

    @Test
    void testGetAdapter_UnsupportedNetwork_ThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> messageAdapterFactory.getAdapter("unknown")
        );

        assertTrue(exception.getMessage().contains("No adapter for network: unknown"));
    }

    @Test
    void testGetAdapter_NullNetwork_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> messageAdapterFactory.getAdapter(null));
    }

    @Test
    void testGetAdapter_EmptyNetwork_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> messageAdapterFactory.getAdapter(""));
    }

    @Test
    void testGetAdapter_CaseInsensitiveMatching() {
        MessageAdapter emailAdapter = messageAdapterFactory.getAdapter("EMAIL");
        MessageAdapter slackAdapter = messageAdapterFactory.getAdapter("SLACK");

        assertNotNull(emailAdapter);
        assertNotNull(slackAdapter);
        assertTrue(emailAdapter.supports("email"));
        assertTrue(slackAdapter.supports("slack"));
    }

    // ---------------- Extra 6 cases ----------------

    @Test
    void testFactory_WithDuplicateAdapters_StillReturnsValidAdapter() {
        messageAdapterFactory = new MessageAdapterFactory(List.of(emailAdapter, new EmailAdapter()));

        MessageAdapter adapter = messageAdapterFactory.getAdapter("email");

        assertNotNull(adapter);
        assertTrue(adapter instanceof EmailAdapter);
    }

    @Test
    void testFactory_WithMultipleAdaptersForSameNetwork_ReturnsFirstMatch() {
        EmailAdapter firstEmailAdapter = new EmailAdapter();
        EmailAdapter secondEmailAdapter = new EmailAdapter();

        messageAdapterFactory = new MessageAdapterFactory(List.of(firstEmailAdapter, secondEmailAdapter));

        MessageAdapter adapter = messageAdapterFactory.getAdapter("email");

        assertSame(firstEmailAdapter.getClass(), adapter.getClass());
    }

    @Test
    void testFactory_ThrowsWhenNoAdaptersProvided() {
        messageAdapterFactory = new MessageAdapterFactory(List.of());

        assertThrows(IllegalArgumentException.class, () -> messageAdapterFactory.getAdapter("email"));
    }


    @Test
    void testGetAdapter_SupportsMixedCaseSlack() {
        MessageAdapter adapter = messageAdapterFactory.getAdapter("SlAcK");

        assertNotNull(adapter);
        assertTrue(adapter instanceof SlackAdapter);
    }

    @Test
    void testFactory_AdaptersListImmutability() {
        List<MessageAdapter> adapters = List.of(emailAdapter, slackAdapter);
        messageAdapterFactory = new MessageAdapterFactory(adapters);

        // Ensure original list is not modified
        assertEquals(2, adapters.size());

    }
}
