package com.ecaservice.core.lock.config.redis;

import com.ecaservice.core.lock.config.AbstractLockConfiguration;
import com.ecaservice.core.lock.config.LockProperties;
import com.ecaservice.core.lock.service.LockRegistryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

/**
 * Redis locks configuration class.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@ConditionalOnClass(RedisLockRegistry.class)
@ConditionalOnProperty(value = "lock.registry-type", havingValue = "REDIS")
@RequiredArgsConstructor
public class RedisLockConfiguration extends AbstractLockConfiguration {

    private final RedisConnectionFactory redisConnectionFactory;

    /**
     * Creates redis lock registry repository.
     *
     * @param lockProperties - lock properties
     * @return redis lock registry repository
     */
    @Bean
    public LockRegistryRepository lockRegistryRepository(LockProperties lockProperties) {
        var lockRegistryRepository = super.lockRegistryRepository(lockProperties);
        log.info("Redis lock registry repository has been initialized");
        return lockRegistryRepository;
    }

    @Override
    public LockRegistry createLockRegistry(String registryKey,
                                           LockProperties.LockRegistryProperties lockRegistryProperties) {
        var redisLockRegistry =
                new RedisLockRegistry(redisConnectionFactory, registryKey, lockRegistryProperties.getExpireAfter());
        log.info("Redis lock registry [{}] has been initialized. Lock expiration [{}] ms",
                registryKey, lockRegistryProperties.getExpireAfter());
        return redisLockRegistry;
    }
}
