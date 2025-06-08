package org.lru.eviction;

import org.lru.dto.Node;

public interface EvictionPolicy<Key, Value> {

    void accessed(Node<Key, Value> node);

    Key evict(Node<Key, Value> node);

    void add(Node<Key, Value> node);
}
