package com.Project1.IngestionAndValidation.uniqueId;


import com.Project1.IngestionAndValidation.Models.UniqueId;
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
    void testUniqueIdEmptyString() {
        UniqueId uniqueId = new UniqueId("");
        assertEquals("", uniqueId.getId());
    }

    @Test
    void testUniqueIdSpecialCharacters() {
        UniqueId uniqueId = new UniqueId("@#$%^&*()");
        assertEquals("@#$%^&*()", uniqueId.getId());
    }

    @Test
    void testUniqueIdLongString() {
        String longId = "a".repeat(1000);
        UniqueId uniqueId = new UniqueId(longId);
        assertEquals(longId, uniqueId.getId());
    }

    @Test
    void testUniqueIdEqualitySameObject() {
        UniqueId uniqueId = new UniqueId("msg-1");
        assertEquals(uniqueId, uniqueId);
    }

    @Test
    void testUniqueIdEqualityDifferentObjectsSameId() {
        UniqueId id1 = new UniqueId("msg-1");
        UniqueId id2 = new UniqueId("msg-1");
        assertNotEquals(id1, id2);
    }

    @Test
    void testUniqueIdInequalityDifferentIds() {
        UniqueId id1 = new UniqueId("msg-1");
        UniqueId id2 = new UniqueId("msg-2");
        assertNotEquals(id1, id2);
    }

    @Test
    void testUniqueIdInequalityNull() {
        UniqueId id1 = new UniqueId("msg-1");
        assertNotEquals(id1, null);
    }

    @Test
    void testUniqueIdInequalityDifferentClass() {
        UniqueId id1 = new UniqueId("msg-1");
        String id2 = "msg-1";
        assertNotEquals(id1, id2);
    }

    @Test
    void testUniqueIdHashCodeConsistency() {
        UniqueId id1 = new UniqueId("msg-1");
        int hash1 = id1.hashCode();
        int hash2 = id1.hashCode();
        assertEquals(hash1, hash2);
    }


    @Test
    void testUniqueIdHashCodeInequality() {
        UniqueId id1 = new UniqueId("msg-1");
        UniqueId id2 = new UniqueId("msg-2");
        assertNotEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void testUniqueIdToString() {
        UniqueId id1 = new UniqueId("msg-1");
        String result = id1.toString();
        assertTrue(!result.contains("msg-1"));
    }
}