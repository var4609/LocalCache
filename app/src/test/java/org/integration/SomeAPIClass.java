package org.integration;

import org.lru.cache.BasicLRUCache;
import org.lru.cache.BasicLRUCacheImpl;

public class SomeAPIClass {

    private BasicLRUCache<SomeApiRequest, SomeApiReponse> lruCache;

    public SomeAPIClass() {
        this.lruCache = new BasicLRUCacheImpl<>(10);
    }

    private SomeApiReponse sampleAPICall(SomeApiRequest request) {
        SomeApiReponse someApiReponse = lruCache.get(request);
        if(someApiReponse != null) {
            return someApiReponse;
        } else {
            SomeApiReponse heavyResponse = heavyTransaction(request);
            lruCache.put(request, heavyResponse);
            return heavyResponse;
        }
    }

    private SomeApiReponse heavyTransaction(SomeApiRequest request) {
        return new SomeApiReponse();
    }

    private class SomeApiRequest {}
    private class SomeApiReponse {}
}
