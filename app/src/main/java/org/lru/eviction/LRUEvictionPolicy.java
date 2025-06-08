package org.lru.eviction;

import org.lru.dto.Node;

public class LRUEvictionPolicy<Key, Value> implements EvictionPolicy<Key, Value> {

    public final Node<Key, Value> head;
    public final Node<Key, Value> tail;

    public LRUEvictionPolicy() {
        this.head = new Node<>(null, null);
        this.tail = new Node<>(null, null);
        this.head.next = this.tail;
        this.tail.prev = this.head;
    }
    
    @Override
    public void accessed(Node<Key, Value> node) {
        evict(node);
        addToTail(node);
    }

    @Override
    public Key evict(Node<Key, Value> node) {
        if(node == null) {
            Key key = head.next.key;
            head.next = head.next.next;
            head.next.prev = head;
            return key;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            return node.key;
        }
        
    }

    @Override
    public void add(Node<Key, Value> node) {
        addToTail(node);
    }

    private void addToTail(Node<Key, Value> node) {
        tail.prev.next = node;
        node.prev = tail.prev;
        tail.prev = node;
        node.next = tail;
    }
}
