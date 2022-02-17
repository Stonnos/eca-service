package com.ecaservice.server.config.redelivery;

import com.ecaservice.core.lock.redis.config.RedisLockProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

import static com.ecaservice.core.redelivery.config.RedeliveryCoreAutoConfiguration.REDELIVERY_LOCK_REGISTRY;

/**
 * Redelivery configuration class.
 *
 * @author Roman Batygin
 */
@Slf4j
@ConditionalOnProperty(value = "redelivery.enabled", havingValue = "true")
@Configuration
public class EcaServerRedeliveryConfiguration {

    private static final String REGISTRY_PREFIX = "%s-redelivery";

    /**
     * Creates redis lock registry for redelivery lib.
     *
     * @param redisConnectionFactory - redis connection factory
     * @return redelivery redis lock registry
     */
    @Bean(REDELIVERY_LOCK_REGISTRY)
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory,
                                               RedisLockProperties redisLockProperties) {
        String registryKey = String.format(REGISTRY_PREFIX, redisLockProperties.getRegistryKey());
        var redisLockRegistry =
                new RedisLockRegistry(redisConnectionFactory, registryKey, redisLockProperties.getExpireAfter());
        log.info("Redelivery redis lock registry [{}] has been initialized", registryKey);
        return redisLockRegistry;
    }
}
