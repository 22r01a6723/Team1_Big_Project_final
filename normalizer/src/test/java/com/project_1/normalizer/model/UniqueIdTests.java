package com.project_1.normalizer.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UniqueIdTest {

    @Test
    void testUniqueIdConstructor() {
        UniqueId uniqueId = new UniqueId("msg-1");
        assertEquals("msg-1", uniqueId.getId());
    }

    @Test
    void testUniqueIdSetters() {
        UniqueId uniqueId = new UniqueId("msg-1");
        uniqueId.setId("msg-2");
        assertEquals("msg-2", uniqueId.getId());
    }

    @Test
    void testUniqueIdNullValue() {
        UniqueId uniqueId = new UniqueId(null);
        assertNull(uniqueId.getId());
        uniqueId.setId(null);
        assertNull(uniqueId.getId());
    }


    @Test
    void testEquals_SameObject() {
        UniqueId uniqueId = new UniqueId("msg-1");
        assertEquals(uniqueId, uniqueId);
    }


    @Test
    void testEquals_DifferentIds() {
        UniqueId id1 = new UniqueId("msg-1");
        UniqueId id2 = new UniqueId("msg-2");
        assertNotEquals(id1, id2);
    }

    @Test
    void testEquals_NullObject() {
        UniqueId id1 = new UniqueId("msg-1");
        assertNotEquals(id1, null);
    }

    @Test
    void testEquals_DifferentClass() {
        UniqueId id1 = new UniqueId("msg-1");
        assertNotEquals(id1, "msg-1");
    }

    @Test
    void testHashCodeConsistency() {
        UniqueId id = new UniqueId("msg-1");
        int hash1 = id.hashCode();
        int hash2 = id.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testEmptyStringId() {
        UniqueId id = new UniqueId("");
        assertEquals("", id.getId());
    }

    @Test
    void testSpecialCharactersId() {
        UniqueId id = new UniqueId("@!#-123");
        assertEquals("@!#-123", id.getId());
    }
}
