package org.lru;

import org.lru.cache.BasicLRUCache;
import org.lru.cache.BasicLRUCacheImpl;

public class LRUCacheProvider<Key, Value> {

    public BasicLRUCache<Key, Value> provide(Integer capacity) {
        return new BasicLRUCacheImpl<>(capacity);
    }
}