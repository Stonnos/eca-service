package com.ecaservice.server.config.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Caffeine cache configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(CacheConfig.class)
@ConditionalOnProperty(value = "spring.cache.type", havingValue = "caffeine")
@RequiredArgsConstructor
public class CaffeineCacheConfiguration {

    private final CacheConfig cacheConfig;

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
     * @return cache manager bean
     */
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(buildCaches());
        log.info("Caffeine cache manager has been initialized");
        return simpleCacheManager;
    }

    private List<CaffeineCache> buildCaches() {
        return cacheConfig.getSpecs().entrySet()
                .stream()
                .map(entry -> buildCache(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private CaffeineCache buildCache(String cacheName, CacheSpec cacheSpec) {
        Cache<Object, Object> cache = Caffeine.newBuilder()
                .expireAfterWrite(cacheSpec.getExpireAfterWriteSeconds(), TimeUnit.SECONDS)
                .maximumSize(cacheSpec.getMaxSize())
                .ticker(ticker())
                .build();
        return new CaffeineCache(cacheName, cache);
    }
}
