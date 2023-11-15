package com.ecaservice.server.config.lock;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.DefaultLockRegistry;

/**
 * Abstract lock configuration.
 *
 * @author Roman Batygin
 */
public abstract class AbstractLockConfiguration {

    /**
     * Creates default lock registry bean.
     *
     * @return default lock registry bean
     */
    public DefaultLockRegistry lockRegistry() {
        return new DefaultLockRegistry();
    }

    /**
     * Creates redis lock registry.
     *
     * @param redisConnectionFactory - redis connection factory
     * @param lockProperties         - lock properties
     * @return redis lock registry
     */
    public RedisLockRegistry redisLockRegistry(final RedisConnectionFactory redisConnectionFactory,
                                               final LockProperties lockProperties) {
        return new RedisLockRegistry(redisConnectionFactory, lockProperties.getRegistryKey(),
                lockProperties.getExpireAfter());
    }
}
