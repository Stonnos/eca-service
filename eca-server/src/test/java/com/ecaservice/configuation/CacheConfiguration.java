package com.ecaservice.configuation;

import com.ecaservice.config.cache.CacheNames;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Cache configuration for tests.
 *
 * @author Roman Batygin
 */
@TestConfiguration
@EnableCaching
public class CacheConfiguration {

    private static final int CACHE_DURATION = 2;
    private static final int CACHE_MAXIMUM_SIZE = 1000;

    /**
     * Creates ticker bean.
     *
     * @return ticker bean
     */
    @Bean
    public Ticker ticker() {
        return Ticker.systemTicker();
    }

    /**
     * Creates cache manager bean.
     *
     * @param ticker - ticker bean
     * @return cache manager bean
     */
    @Bean
    public CacheManager cacheManager(Ticker ticker) {
        SimpleCacheManager manager = new SimpleCacheManager();
        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                .expireAfterWrite(CACHE_DURATION, TimeUnit.MINUTES)
                .maximumSize(CACHE_MAXIMUM_SIZE)
                .ticker(ticker);
        manager.setCaches(Collections.singletonList(
                new CaffeineCache(CacheNames.EVALUATION_RESULTS_CACHE_NAME, caffeineBuilder.build())));
        return manager;
    }
}
