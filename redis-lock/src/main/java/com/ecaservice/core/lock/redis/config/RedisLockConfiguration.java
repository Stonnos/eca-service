package com.ecaservice.core.lock.redis.config;

import com.ecaservice.core.lock.annotation.EnableLocks;
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
@EnableLocks
@Configuration
@EnableConfigurationProperties(RedisLockProperties.class)
public class RedisLockConfiguration {

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
        return new RedisLockRegistry(redisConnectionFactory, redisLockProperties.getRegistryKey(),
                redisLockProperties.getExpireAfter());
    }
}
