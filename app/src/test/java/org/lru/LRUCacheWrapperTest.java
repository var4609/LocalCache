package org.lru;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LRUCacheWrapperTest {

    // Dummy classes for testing, nested for convenience
    static class TestRequest {
        final Integer id;
        final String data;
        TestRequest(Integer id, String data) { this.id = id; this.data = data; }
    }

    static class TestResponse {
        final String result;
        TestResponse(String result) { this.result = result; }
        // Override equals for easy comparison in assertions
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestResponse that = (TestResponse) obj;
            return result.equals(that.result);
        }
    }

    private LRUCacheWrapper<TestRequest, TestResponse, Integer> cacheWrapper;
    private AtomicInteger loaderExecutionCount;
    private Function<TestRequest, TestResponse> valueLoader;

    @BeforeEach
    void setUp() {
        // This counter will act as a "spy" to see if our expensive loader was called.
        loaderExecutionCount = new AtomicInteger(0);

        // This is our "expensive operation" (e.g., an API call).
        valueLoader = (request) -> {
            loaderExecutionCount.incrementAndGet();
            return new TestResponse("Response for " + request.id + " with data '" + request.data + "'");
        };
    }

    @Test
    @DisplayName("Should call valueLoader on cache miss")
    void testCacheMissAndValueLoading() {
        cacheWrapper = new LRUCacheWrapper<>(10, (req) -> req.id);
        TestRequest request = new TestRequest(1, "data");

        TestResponse response = cacheWrapper.call(request, valueLoader);

        assertEquals(new TestResponse("Response for 1 with data 'data'"), response);
        assertEquals(1, loaderExecutionCount.get(), "Value loader should be called once on a cache miss.");
    }

    @Test
    @DisplayName("Should return cached value and not call loader on cache hit")
    void testCacheHitDoesNotCallLoader() {
        cacheWrapper = new LRUCacheWrapper<>(10, (req) -> req.id);
        TestRequest request = new TestRequest(1, "first call");

        // First call - miss
        cacheWrapper.call(request, valueLoader);
        assertEquals(1, loaderExecutionCount.get());

        // Second call - hit
        TestResponse response = cacheWrapper.call(request, valueLoader);

        assertEquals(new TestResponse("Response for 1 with data 'first call'"), response);
        assertEquals(1, loaderExecutionCount.get(), "Value loader should NOT be called on a cache hit.");
    }

    @Test
    @DisplayName("Should use keyBuilder for equality, not object identity")
    void testKeyBuilderIsUsedForEquality() {
        cacheWrapper = new LRUCacheWrapper<>(10, (req) -> req.id);
        TestRequest requestA = new TestRequest(1, "dataA");
        TestRequest requestB = new TestRequest(1, "dataB"); // Same ID, different object and data

        // First call with requestA
        TestResponse responseA = cacheWrapper.call(requestA, valueLoader);
        assertEquals(1, loaderExecutionCount.get());
        assertEquals("Response for 1 with data 'dataA'", responseA.result);

        // Second call with requestB, which has the same key (id=1)
        TestResponse responseB = cacheWrapper.call(requestB, valueLoader);
        assertEquals(1, loaderExecutionCount.get(), "Loader should not be called for an equivalent key.");
        
        // IMPORTANT: The returned response should be the one from the FIRST call, as it was cached.
        assertEquals("Response for 1 with data 'dataA'", responseB.result, "Should return the originally cached value.");
    }
    
    @Test
    @DisplayName("Should evict the least recently used entry when capacity is exceeded")
    void testLruEvictionPolicy() {
        cacheWrapper = new LRUCacheWrapper<>(2, (req) -> req.id); // Capacity of 2

        // 1. Fill the cache
        cacheWrapper.call(new TestRequest(1, "data1"), valueLoader); // Cache: [1]
        cacheWrapper.call(new TestRequest(2, "data2"), valueLoader); // Cache: [1, 2]
        assertEquals(2, loaderExecutionCount.get());
        
        // 2. Add a new entry, which should evict the LRU item (ID 1)
        cacheWrapper.call(new TestRequest(3, "data3"), valueLoader); // Cache: [2, 3]
        assertEquals(3, loaderExecutionCount.get(), "Loader should be called for the new item.");
        
        // 3. Request the evicted item (ID 1) again, it should be a miss
        cacheWrapper.call(new TestRequest(1, "data1-again"), valueLoader);
        assertEquals(4, loaderExecutionCount.get(), "Loader should be called for the evicted item.");
    }
    
    @Test
    @DisplayName("Should refresh an entry's recency on access")
    void testAccessOrderRefreshesLru() {
        cacheWrapper = new LRUCacheWrapper<>(2, (req) -> req.id); // Capacity of 2

        // 1. Fill the cache
        cacheWrapper.call(new TestRequest(1, "data1"), valueLoader); // Cache state (LRU -> MRU): [1]
        cacheWrapper.call(new TestRequest(2, "data2"), valueLoader); // Cache state: [1, 2]
        
        // 2. Access item 1 again, making it the most recently used
        cacheWrapper.call(new TestRequest(1, "data1"), valueLoader); // Cache state: [2, 1]
        assertEquals(2, loaderExecutionCount.get(), "Loader should not be called for the access.");
        
        // 3. Add a new entry (ID 3). This should evict the LRU item, which is now ID 2.
        cacheWrapper.call(new TestRequest(3, "data3"), valueLoader); // Cache state: [1, 3]
        assertEquals(3, loaderExecutionCount.get());
        
        // 4. Verify that ID 2 was evicted (miss) and ID 1 is still present (hit)
        loaderExecutionCount.set(0); // Reset counter for clarity
        
        cacheWrapper.call(new TestRequest(1, "data1"), valueLoader); // Should be a HIT
        assertEquals(0, loaderExecutionCount.get(), "Item 1 should still be in the cache.");
        
        cacheWrapper.call(new TestRequest(2, "data2"), valueLoader); // Should be a MISS
        assertEquals(1, loaderExecutionCount.get(), "Item 2 should have been evicted.");
    }
}
