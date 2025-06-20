package org.cache;

import java.util.function.Function;

public abstract class LocalCacheWrapper<RequestKey, ResponseValue, CacheKey> {
    
    private final LocalCache<CacheKey, ResponseValue> cache;
    private final Function<RequestKey, CacheKey> keyLoader;

    public LocalCacheWrapper(LocalCache<CacheKey, ResponseValue> cache, Function<RequestKey, CacheKey> keyLoader) {
        this.cache = cache;
        this.keyLoader = keyLoader;
    }

    public ResponseValue call(RequestKey requestKey, Function<RequestKey, ResponseValue> valueLoader) {
        final CacheKey cacheKey = keyLoader.apply(requestKey);
        ResponseValue responseValue = cache.get(cacheKey);

        if(responseValue == null) {
            responseValue = valueLoader.apply(requestKey);
            cache.put(cacheKey, responseValue);
        }

        return responseValue;
    };
}
