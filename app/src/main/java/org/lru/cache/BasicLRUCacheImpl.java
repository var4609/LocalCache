package org.lru.cache;

import java.util.HashMap;
import java.util.Map;

import org.cache.LocalCache;
import org.lru.dto.Node;
import org.lru.eviction.EvictionPolicy;
import org.lru.eviction.LRUEvictionPolicy;

public class BasicLRUCacheImpl<Key, Value> implements LocalCache<Key, Value> {
    
    private final Integer capacity;
    private final Map<Key, Node<Key, Value>> storage;
    private final EvictionPolicy<Key, Value> evictionPolicy;

    public BasicLRUCacheImpl(Integer capacity) {
        this.capacity = capacity;
        this.storage = new HashMap<>();
        this.evictionPolicy = new LRUEvictionPolicy<>();
    }
    
    @Override
    public Value get(Key key) {
        if(storage.containsKey(key)) {
            //move to tail
            Node<Key, Value> node = storage.get(key);
            evictionPolicy.accessed(node);
            return node.value;
        } else {
            return null;
        }
    }

    @Override
    public void put(Key key, Value value) {

        if(storage.containsKey(key)) {
            Node<Key, Value> node = storage.get(key);
            evictionPolicy.evict(node);          
        } else {
            if(this.storage.size() == this.capacity) {
                Key delKey = evictionPolicy.evict(null);
                storage.remove(delKey);
            }
        }

        Node<Key, Value> newNode = new Node<>(key, value);
        evictionPolicy.add(newNode);
        storage.put(key, newNode);
    }
}
