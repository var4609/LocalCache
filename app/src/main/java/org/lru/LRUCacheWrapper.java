package org.lru;

import java.util.function.Function;

import org.cache.LocalCacheWrapper;
import org.lru.cache.BasicLRUCacheImpl;

public class LRUCacheWrapper<RequestKey, Value, CacheKey> extends LocalCacheWrapper<RequestKey, Value, CacheKey> {
    
    public LRUCacheWrapper(Integer capacity, Function<RequestKey, CacheKey> keyBuilder) {
        super(new BasicLRUCacheImpl<>(capacity), keyBuilder);
    }
}
