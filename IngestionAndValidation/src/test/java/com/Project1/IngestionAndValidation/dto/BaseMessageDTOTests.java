package com.Project1.IngestionAndValidation.dto;


import com.Project1.IngestionAndValidation.Models.BaseMessageDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BaseMessageDTOTest {

    @Test
    void testBaseMessageDTOFields() {
        BaseMessageDTO dto = new BaseMessageDTO() {};
        dto.setTenantId("tenant-1");
        dto.setStableMessageId("stable-123");

        assertEquals("tenant-1", dto.getTenantId());
        assertEquals("stable-123", dto.getStableMessageId());
    }

    @Test
    void testBaseMessageDTOToString() {
        BaseMessageDTO dto = new BaseMessageDTO() {};
        dto.setTenantId("tenant-1");
        dto.setStableMessageId("stable-123");

        String result = dto.toString();
        assertTrue(result.contains("tenant-1"));
        assertTrue(result.contains("stable-123"));
    }

    @Test
    void testBaseMessageDTONullFields() {
        BaseMessageDTO dto = new BaseMessageDTO() {};

        assertNull(dto.getTenantId());
        assertNull(dto.getStableMessageId());

        String result = dto.toString();
        assertTrue(result.contains("null"));
    }

    @Test
    void testEqualsSameObject() {
        BaseMessageDTO dto = new BaseMessageDTO() {};
        dto.setTenantId("tenant-1");
        dto.setStableMessageId("stable-123");

        assertEquals(dto, dto); // reflexivity
    }


    @Test
    void testEqualsDifferentTenantId() {
        BaseMessageDTO dto1 = new BaseMessageDTO() {};
        dto1.setTenantId("tenant-1");
        dto1.setStableMessageId("stable-123");

        BaseMessageDTO dto2 = new BaseMessageDTO() {};
        dto2.setTenantId("tenant-2");
        dto2.setStableMessageId("stable-123");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testEqualsDifferentStableMessageId() {
        BaseMessageDTO dto1 = new BaseMessageDTO() {};
        dto1.setTenantId("tenant-1");
        dto1.setStableMessageId("stable-123");

        BaseMessageDTO dto2 = new BaseMessageDTO() {};
        dto2.setTenantId("tenant-1");
        dto2.setStableMessageId("stable-456");

        assertNotEquals(dto1, dto2);
    }

    @Test
    void testNotEqualsNull() {
        BaseMessageDTO dto = new BaseMessageDTO() {};
        dto.setTenantId("tenant-1");
        dto.setStableMessageId("stable-123");

        assertNotEquals(dto, null);
    }

    @Test
    void testNotEqualsDifferentClass() {
        BaseMessageDTO dto = new BaseMessageDTO() {};
        dto.setTenantId("tenant-1");
        dto.setStableMessageId("stable-123");

        assertNotEquals(dto, "Some String");
    }

    @Test
    void testHashCodeConsistency() {
        BaseMessageDTO dto = new BaseMessageDTO() {};
        dto.setTenantId("tenant-1");
        dto.setStableMessageId("stable-123");

        int hash1 = dto.hashCode();
        int hash2 = dto.hashCode();

        assertEquals(hash1, hash2);
    }

    @Test
    void testToStringIncludesClassName() {
        BaseMessageDTO dto = new BaseMessageDTO() {};
        dto.setTenantId("tenant-1");
        dto.setStableMessageId("stable-123");

        String result = dto.toString();
        assertTrue(result.contains("BaseMessageDTO"));
    }

    @Test
    void testDefaultConstructorCreatesEmptyObject() {
        BaseMessageDTO dto = new BaseMessageDTO() {};

        assertNull(dto.getTenantId());
        assertNull(dto.getStableMessageId());
    }
}