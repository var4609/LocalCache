package org.lru;

import java.util.HashMap;
import java.util.Map;

import org.lru.dto.Node;

public class BasicLRUCacheImpl<Key, Value> implements BasicLRUCache<Key, Value> {
    
    private final Integer capacity;
    private final Node<Key, Value> head;
    private final Node<Key, Value> tail;
    private final Map<Key, Node<Key, Value>> storage;

    public BasicLRUCacheImpl(Integer capacity) {
        this.capacity = capacity;
        this.head = new Node<Key, Value>();
        this.tail = new Node<Key, Value>();
        this.storage = new HashMap<>();

        this.head.next = this.tail;
        this.tail.prev = this.head;
    }
    
    @Override
    public Value get(Key key) {
        if(storage.containsKey(key)) {
            //move to tail
            Node<Key, Value> node = storage.get(key);
            //a. detach
            node.prev.next = node.next;
            node.next.prev = node.prev;
            //b. attach before tail
            node.next = tail;
            node.prev = tail.prev;
            tail.prev.next = node;
            tail.prev = node;

            return node.value;
        } else {
            return null;
        }
    }

    @Override
    public void put(Key key, Value value) {

        if(storage.containsKey(key)) {
            Node<Key, Value> node = storage.get(key);

            node.prev.next = node.next;
            node.next.prev = node.prev;           
        } else {
            if(this.storage.size() == this.capacity) {
                Node<Key, Value> del = head.next;
                head.next = del.next;
                del.next.prev = head;

                storage.remove(del.key);
            }
        }

        Node<Key, Value> newNode = new Node<>(key, value);
        newNode.next = tail;
        newNode.prev = tail.prev;
        tail.prev.next = newNode;
        tail.prev = newNode;

        storage.put(key, newNode);
    }
}
