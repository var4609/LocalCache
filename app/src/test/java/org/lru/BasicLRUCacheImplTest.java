package org.lru;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BasicLRUCacheImplTest {

    private BasicLRUCacheImpl<Integer, Integer> cache;

    @BeforeEach
    void setup() {
        cache = new BasicLRUCacheImpl<>(2);
    }

    @Test
    void testPutAndGet() {
        cache.put(1, 100);
        cache.put(2, 200);

        assertEquals(100, cache.get(1));
        assertEquals(200, cache.get(2));
    }

    @Test
    void testEvictionWhenCapacityExceeded() {
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(3, 300); // should evict key 1

        assertNull(cache.get(1));
        assertEquals(200, cache.get(2));
        assertEquals(300, cache.get(3));
    }

    @Test
    void testUpdateValue() {
        cache.put(1, 100);
        cache.put(1, 101); // should update value, not evict

        assertEquals(101, cache.get(1));
    }

    @Test
    void testLRUOrderUpdateOnAccess() {
        cache.put(1, 100);
        cache.put(2, 200);

        // Access key 1 to make it recently used
        cache.get(1);

        // Insert new key to trigger eviction
        cache.put(3, 300);

        // Key 2 should be evicted (as it was least recently used)
        assertNull(cache.get(2));
        assertEquals(100, cache.get(1));
        assertEquals(300, cache.get(3));
    }

    @Test
    void testGetNonExistentKey() {
        cache.put(1, 100);

        assertNull(cache.get(999));
    }

    @Test
    void testOverwriteDoesNotEvictOthers() {
        cache.put(1, 100);
        cache.put(2, 200);
        cache.put(1, 111); // update existing key

        // Should not cause eviction
        assertEquals(111, cache.get(1));
        assertEquals(200, cache.get(2));
    }
}
