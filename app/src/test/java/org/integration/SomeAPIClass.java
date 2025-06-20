package org.integration;

import org.lru.LRUCacheWrapper;

public class SomeAPIClass {

    private LRUCacheWrapper<SomeApiRequest, SomeApiReponse, Integer> wrapper = new LRUCacheWrapper<>(10, (SomeApiRequest request) -> request.id);

    private SomeApiReponse sampleAPICall(SomeApiRequest request) {
        return wrapper.call(request, this::heavyTransaction);
    }

    private SomeApiReponse heavyTransaction(SomeApiRequest request) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new SomeApiReponse();
    }

    private class SomeApiRequest {
        public Integer id;
    }
    private class SomeApiReponse {}
}
