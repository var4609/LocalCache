package org.cache;

public interface LocalCache<Key, Value> {

    Value get(Key key);

    void put(Key key, Value value);
}
