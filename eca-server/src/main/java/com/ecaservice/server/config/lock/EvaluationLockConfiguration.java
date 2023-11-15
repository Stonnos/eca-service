package com.ecaservice.server.config.lock;

import com.ecaservice.server.config.ClassifiersProperties;
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
public class EvaluationLockConfiguration extends AbstractLockConfiguration {

    public static final String EVALUATION_LOCK_REGISTRY = "evaluationLockRegistry";

    /**
     * Creates default lock registry bean.
     *
     * @return default lock registry bean
     */
    @Override
    @ConditionalOnProperty(value = "lock.registry-type", havingValue = "IN_MEMORY", matchIfMissing = true)
    @Bean(EVALUATION_LOCK_REGISTRY)
    public DefaultLockRegistry lockRegistry() {
        var lockRegistry = super.lockRegistry();
        log.info("Evaluation in memory lock registry has been initialized");
        return lockRegistry;
    }

    /**
     * Creates redis lock registry.
     *
     * @param redisConnectionFactory - redis connection factory
     * @param classifiersProperties  - classifiers properties
     * @return redis lock registry
     */
    @ConditionalOnProperty(value = "lock.registry-type", havingValue = "REDIS")
    @Bean(EVALUATION_LOCK_REGISTRY)
    public RedisLockRegistry redisLockRegistry(final RedisConnectionFactory redisConnectionFactory,
                                               final ClassifiersProperties classifiersProperties) {
        var lockProperties = classifiersProperties.getLock();
        var redisLockRegistry = redisLockRegistry(redisConnectionFactory, lockProperties);
        log.info("Evaluation lock registry [{}] has been initialized. Lock expiration [{}] ms",
                lockProperties.getRegistryKey(), lockProperties.getExpireAfter());
        return redisLockRegistry;
    }
}
