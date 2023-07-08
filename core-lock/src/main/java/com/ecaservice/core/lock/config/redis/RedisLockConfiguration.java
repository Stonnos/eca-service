package com.ecaservice.core.lock.config.redis;

import com.ecaservice.core.lock.config.LockProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

import static com.ecaservice.core.lock.config.CoreLockAutoConfiguration.LOCK_REGISTRY;

/**
 * Redis locks configuration class.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@ConditionalOnClass(RedisLockRegistry.class)
@ConditionalOnProperty(value = "lock.registry-type", havingValue = "REDIS")
public class RedisLockConfiguration {

    /**
     * Creates redis lock registry.
     *
     * @param redisConnectionFactory - redis connection factory
     * @param lockProperties         - redis lock properties
     * @return redis lock registry
     */
    @Bean(LOCK_REGISTRY)
    @ConditionalOnMissingBean
    @ConditionalOnBean(RedisConnectionFactory.class)
    public RedisLockRegistry redisLockRegistry(final RedisConnectionFactory redisConnectionFactory,
                                               final LockProperties lockProperties) {
        var redisLockProperties = lockProperties.getRedis();
        var redisLockRegistry = new RedisLockRegistry(redisConnectionFactory, redisLockProperties.getRegistryKey(),
                redisLockProperties.getExpireAfter());
        log.info("Redis lock registry [{}] has been initialized", redisLockProperties.getRegistryKey());
        return redisLockRegistry;
    }
}
