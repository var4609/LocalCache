package org.lru;

import java.util.function.Function;

import org.lru.cache.BasicLRUCache;
import org.lru.cache.BasicLRUCacheImpl;

public class LRUCacheWrapper<RequestKey, Value, CacheKey> {
    
    private BasicLRUCache<CacheKey, Value> lruCache;
    private Function<RequestKey, CacheKey> keyBuilder;

    public LRUCacheWrapper(Integer capacity, Function<RequestKey, CacheKey> keyBuilder) {
        this.lruCache = new BasicLRUCacheImpl<>(capacity);
        this.keyBuilder = keyBuilder;
    }

    public Value call(RequestKey key, Function<RequestKey, Value> valueLoaderFunction) {
        CacheKey cacheKey = keyBuilder.apply(key);
        
        Value value = lruCache.get(cacheKey);

        if(value == null) {
            value = valueLoaderFunction.apply(key);
            lruCache.put(cacheKey, value);
        }

        return value;
    }
}
