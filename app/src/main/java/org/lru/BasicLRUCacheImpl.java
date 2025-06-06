package org.lru;

import java.util.HashMap;
import java.util.Map;

public class BasicLRUCacheImpl implements BasicLRUCache {
    
    private final Integer capacity;
    private final Node head;
    private final Node tail;
    private final Map<Integer, Node> storage;

    public BasicLRUCacheImpl(Integer capacity) {
        this.capacity = capacity;
        this.head = new Node();
        this.tail = new Node();
        this.storage = new HashMap<>();

        this.head.next = this.tail;
        this.tail.prev = this.head;
    }
    
    @Override
    public Integer get(Integer key) {
        if(storage.containsKey(key)) {
            //move to tail
            Node node = storage.get(key);
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
    public void put(Integer key, Integer value) {

        if(storage.containsKey(key)) {
            Node node = storage.get(key);

            node.prev.next = node.next;
            node.next.prev = node.prev;           
        } else {
            if(this.storage.size() == this.capacity) {
                Node del = head.next;
                head.next = del.next;
                del.next.prev = head;

                storage.remove(del.key);
            }
        }

        Node newNode = new Node(key, value);
        newNode.next = tail;
        newNode.prev = tail.prev;
        tail.prev.next = newNode;
        tail.prev = newNode;

        storage.put(key, newNode);
    }

    private class Node {
        Integer key;
        Integer value;

        Node prev;
        Node next;

        Node(Integer key, Integer value) {
            this.key = key;
            this.value = value;
            this.prev = null;
            this.next = null;
        }

        public Node() {}
    }
}
