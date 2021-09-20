package com.ecaservice.core.lock.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * Redis locks configuration class.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RedisLockProperties.class)
public class RedisLockAutoConfiguration {

    /**
     * Creates redis lock registry.
     *
     * @param redisConnectionFactory - redis connection factory
     * @param redisLockProperties    - redis lock properties
     * @return redis lock registry
     */
    @Bean
    public RedisLockRegistry redisLockRegistry(final RedisConnectionFactory redisConnectionFactory,
                                               final RedisLockProperties redisLockProperties) {
        var redisLockRegistry = new RedisLockRegistry(redisConnectionFactory, redisLockProperties.getRegistryKey(),
                redisLockProperties.getExpireAfter());
        log.info("Redis lock registry [{}] has been initialized", redisLockProperties.getRegistryKey());
        return redisLockRegistry;
    }
}
