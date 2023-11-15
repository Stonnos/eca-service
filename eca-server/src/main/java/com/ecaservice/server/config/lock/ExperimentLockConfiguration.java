package com.ecaservice.server.config.lock;

import com.ecaservice.server.config.ExperimentConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.DefaultLockRegistry;

/**
 * Experiment lock configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
public class ExperimentLockConfiguration extends AbstractLockConfiguration {

    public static final String EXPERIMENT_LOCK_REGISTRY = "experimentLockRegistry";

    /**
     * Creates default lock registry bean.
     *
     * @return default lock registry bean
     */
    @Override
    @ConditionalOnProperty(value = "lock.registry-type", havingValue = "IN_MEMORY", matchIfMissing = true)
    @Bean(EXPERIMENT_LOCK_REGISTRY)
    public DefaultLockRegistry lockRegistry() {
        var lockRegistry = super.lockRegistry();
        log.info("Experiment in memory lock registry has been initialized");
        return lockRegistry;
    }

    /**
     * Creates redis lock registry.
     *
     * @param redisConnectionFactory - redis connection factory
     * @param experimentConfig       - experiment config
     * @return redis lock registry
     */
    @ConditionalOnProperty(value = "lock.registry-type", havingValue = "REDIS")
    @Bean(EXPERIMENT_LOCK_REGISTRY)
    public RedisLockRegistry redisLockRegistry(final RedisConnectionFactory redisConnectionFactory,
                                               final ExperimentConfig experimentConfig) {
        var lockProperties = experimentConfig.getLock();
        var redisLockRegistry = redisLockRegistry(redisConnectionFactory, lockProperties);
        log.info("Experiment lock registry [{}] has been initialized. Lock expiration [{}] ms",
                lockProperties.getRegistryKey(), lockProperties.getExpireAfter());
        return redisLockRegistry;
    }
}
