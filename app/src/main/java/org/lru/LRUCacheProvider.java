package org.lru;

import org.cache.LocalCache;
import org.lru.cache.BasicLRUCacheImpl;

public class LRUCacheProvider<Key, Value> {

    public LocalCache<Key, Value> provide(Integer capacity) {
        return new BasicLRUCacheImpl<>(capacity);
    }
}