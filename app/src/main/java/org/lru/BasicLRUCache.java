package org.lru;

public interface BasicLRUCache<Key, Value> {

    Value get(Key key);

    void put(Key key, Value value);
}
