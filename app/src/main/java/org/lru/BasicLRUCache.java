package org.lru;

public interface BasicLRUCache {

    Integer get(Integer key);

    void put(Integer key, Integer value);
}
