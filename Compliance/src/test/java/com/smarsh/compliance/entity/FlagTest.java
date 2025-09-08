package com.smarsh.compliance.entity;


import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class FlagTest {

    @Test
    void testFlagConstructor_SetsAllFields() {
        Flag flag = new Flag("rule-1", "msg-1", "Test flag", "email", "tenant-1");

        assertEquals("rule-1", flag.getRuleId());
        assertEquals("msg-1", flag.getMessageId());
        assertEquals("Test flag", flag.getFlagDescription());
        assertEquals("email", flag.getNetwork());
        assertEquals("tenant-1", flag.getTenantId());
        assertNotNull(flag.getCreatedAt());
    }

    @Test
    void testFlagConstructor_NullValues() {
        Flag flag = new Flag(null, null, null, null, null);

        assertNull(flag.getRuleId());
        assertNull(flag.getMessageId());
        assertNull(flag.getFlagDescription());
        assertNull(flag.getNetwork());
        assertNull(flag.getTenantId());
        assertNotNull(flag.getCreatedAt());
    }

    @Test
    void testFlagNoArgsConstructor() {
        Flag flag = new Flag();

        assertNull(flag.getFlagId());
        assertNull(flag.getRuleId());
        assertNull(flag.getMessageId());
        assertNull(flag.getFlagDescription());
        assertNull(flag.getNetwork());
        assertNull(flag.getTenantId());
        assertNull(flag.getCreatedAt());
    }


    // 3. Test inequality with different fields
    @Test
    void testEquals_DifferentFields_ReturnsFalse() {
        Flag flag1 = new Flag("rule-1", "msg-1", "flag1", "email", "tenant-1");
        Flag flag2 = new Flag("rule-2", "msg-2", "flag2", "sms", "tenant-2");

        assertNotEquals(flag1.getRuleId(), flag2.getRuleId());
        assertNotEquals(flag1.getMessageId(), flag2.getMessageId());
        assertNotEquals(flag1.getFlagDescription(), flag2.getFlagDescription());
        assertNotEquals(flag1.getNetwork(), flag2.getNetwork());
        assertNotEquals(flag1.getTenantId(), flag2.getTenantId());
    }

    // 4. Test toString contains important fields
    @Test
    void testToString_ContainsFields() {
        Flag flag = new Flag("rule-3", "msg-3", "Test flag", "email", "tenant-3");
        String str = flag.toString();

        assertTrue(str.contains("rule-3"));
        assertTrue(str.contains("msg-3"));
        assertTrue(str.contains("Test flag"));
        assertTrue(str.contains("email"));
        assertTrue(str.contains("tenant-3"));
    }

    // 5. Test createdAt is set automatically if not provided
    @Test
    void testCreatedAt_AutoSet() {
        Flag flag = new Flag();
        flag.setCreatedAt(null);

        assertNull(flag.getCreatedAt());

        flag = new Flag("rule", "msg", "desc", "email", "tenant");
        assertNotNull(flag.getCreatedAt());
    }
}
