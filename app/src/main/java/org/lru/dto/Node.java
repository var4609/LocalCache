package org.lru.dto;

public class Node<Key, Value> {
    public Key key;
    public Value value;

    public Node<Key, Value> prev;
    public Node<Key, Value> next;

    public Node(Key key, Value value) {
        this.key = key;
        this.value = value;
        this.prev = null;
        this.next = null;
    }

    public Node() {}
}
