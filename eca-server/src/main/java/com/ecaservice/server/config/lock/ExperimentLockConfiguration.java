package com.ecaservice.server.config.lock;

import com.ecaservice.server.config.ExperimentConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.DefaultLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

/**
 * Experiment lock configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = "lock.enabled", havingValue = "true")
public class ExperimentLockConfiguration {

    public static final String EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN = "experimentRedisLockRegistry";

    /**
     * Creates redis lock registry for experiments processing.
     *
     * @param redisConnectionFactory - redis connection factory
     * @return experiment redis lock registry
     */
    @Bean(EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN)
    @ConditionalOnProperty(value = "lock.registry-type", havingValue = "REDIS")
    public RedisLockRegistry lockRegistry(RedisConnectionFactory redisConnectionFactory,
                                          ExperimentConfig experimentConfig) {
        ExperimentConfig.LockProperties lockProperties = experimentConfig.getLock();
        var lockRegistry = new RedisLockRegistry(redisConnectionFactory, lockProperties.getRegistryKey(),
                lockProperties.getExpireAfter());
        log.info("Redis lock registry [{}] has been initialized for experiment processing. Lock expiration [{}] ms.",
                experimentConfig.getLock().getRegistryKey(), experimentConfig.getLock().getExpireAfter());
        return lockRegistry;
    }

    /**
     * Creates default lock registry for experiments processing.
     *
     * @return experiment redis lock registry
     */
    @Bean(EXPERIMENT_REDIS_LOCK_REGISTRY_BEAN)
    @ConditionalOnProperty(value = "lock.registry-type", havingValue = "IN_MEMORY")
    public LockRegistry lockRegistry() {
        var lockRegistry = new DefaultLockRegistry();
        log.info("Default lock registry has been initialized for experiment processing");
        return lockRegistry;
    }
}
