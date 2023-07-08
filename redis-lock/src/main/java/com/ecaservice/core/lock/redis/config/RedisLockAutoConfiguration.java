package com.ecaservice.core.lock.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
     * Redis lock registry bean
     */
    public static final String REDIS_LOCK_REGISTRY = "redisLockRegistry";

    /**
     * Creates redis lock registry.
     *
     * @param redisConnectionFactory - redis connection factory
     * @param redisLockProperties    - redis lock properties
     * @return redis lock registry
     */
    @Bean(REDIS_LOCK_REGISTRY)
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedisConnectionFactory.class)
    public RedisLockRegistry redisLockRegistry(final RedisConnectionFactory redisConnectionFactory,
                                               final RedisLockProperties redisLockProperties) {
        var redisLockRegistry = new RedisLockRegistry(redisConnectionFactory, redisLockProperties.getRegistryKey(),
                redisLockProperties.getExpireAfter());
        log.info("Redis lock registry [{}] has been initialized", redisLockProperties.getRegistryKey());
        return redisLockRegistry;
    }
}
