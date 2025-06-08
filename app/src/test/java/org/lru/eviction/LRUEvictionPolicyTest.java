package org.lru.eviction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lru.dto.Node;

import static org.junit.jupiter.api.Assertions.*;

class LRUEvictionPolicyTest {

    private LRUEvictionPolicy<Integer, String> evictionPolicy;

    @BeforeEach
    void setup() {
        evictionPolicy = new LRUEvictionPolicy<>();
    }

    @Test
    void testAdd() {
        Node<Integer, String> node = new Node<>(1, "A");
        evictionPolicy.add(node);

        // Head's next should be this node, tail's prev should also be this node
        assertEquals(node, evictionPolicy.head.next);
        assertEquals(node, evictionPolicy.tail.prev);
        assertEquals(evictionPolicy.tail, node.next);
        assertEquals(evictionPolicy.head, node.prev);
    }

    @Test
    void testAccessedMovesToTail() {
        Node<Integer, String> node1 = new Node<>(1, "A");
        Node<Integer, String> node2 = new Node<>(2, "B");
        evictionPolicy.add(node1);
        evictionPolicy.add(node2);

        // node1 should be before node2 now
        assertEquals(node1, node2.prev);
        assertEquals(evictionPolicy.head.next, node1);

        evictionPolicy.accessed(node1);

        // node1 should now be the most recently used (before tail)
        assertEquals(evictionPolicy.tail.prev, node1);
        assertEquals(evictionPolicy.head.next, node2); // node2 is now least recently used
    }

    @Test
    void testEvictLeastRecentlyUsed() {
        Node<Integer, String> node1 = new Node<>(1, "A");
        Node<Integer, String> node2 = new Node<>(2, "B");
        evictionPolicy.add(node1);
        evictionPolicy.add(node2);

        Integer evictedKey = evictionPolicy.evict(null);
        assertEquals(1, evictedKey);  // node1 should be evicted (LRU)
        assertEquals(evictionPolicy.head.next, node2); // node2 should be new head.next
    }

    @Test
    void testEvictSpecificNode() {
        Node<Integer, String> node1 = new Node<>(1, "A");
        Node<Integer, String> node2 = new Node<>(2, "B");
        evictionPolicy.add(node1);
        evictionPolicy.add(node2);

        Integer evictedKey = evictionPolicy.evict(node2);
        assertEquals(2, evictedKey);
        assertEquals(evictionPolicy.tail.prev, node1);
        assertEquals(evictionPolicy.head.next, node1);
    }

    @Test
    void testAccessedNodeFromMiddle() {
        Node<Integer, String> node1 = new Node<>(1, "A");
        Node<Integer, String> node2 = new Node<>(2, "B");
        Node<Integer, String> node3 = new Node<>(3, "C");
        evictionPolicy.add(node1);
        evictionPolicy.add(node2);
        evictionPolicy.add(node3);

        evictionPolicy.accessed(node2);

        // Expected order now: node1 -> node3 -> node2
        assertEquals(node1, evictionPolicy.head.next);
        assertEquals(node3, node1.next);
        assertEquals(node2, node3.next);
        assertEquals(evictionPolicy.tail.prev, node2);
    }
}
