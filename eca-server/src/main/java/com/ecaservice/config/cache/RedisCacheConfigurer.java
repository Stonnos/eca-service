package com.ecaservice.config.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.time.Duration;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;
import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

/**
 * Redis cache configuration class.
 *
 * @author Roman Batygin
 */
@Configuration
@EnableConfigurationProperties(CacheConfig.class)
@ConditionalOnProperty(value = "spring.cache.type", havingValue = "redis")
@RequiredArgsConstructor
public class RedisCacheConfigurer {

    private final CacheConfig cacheConfig;

    /**
     * Creates redis cache manager builder customizer.
     *
     * @return redis cache manager builder customizer bean
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> {
            Map<String, RedisCacheConfiguration> configurationMap = newHashMap();
            cacheConfig.getSpecs().forEach(
                    (key, value) -> configurationMap.put(key, RedisCacheConfiguration.defaultCacheConfig()
                            .entryTtl(Duration.ofSeconds(value.getExpireAfterWriteSeconds()))
                            .serializeValuesWith(fromSerializer(new GenericJackson2JsonRedisSerializer()))));
            builder.withInitialCacheConfigurations(configurationMap);
        };
    }
}