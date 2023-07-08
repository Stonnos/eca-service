package com.ecaservice.core.lock.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.locks.DefaultLockRegistry;

import static com.ecaservice.core.lock.config.CoreLockAutoConfiguration.LOCK_REGISTRY;

/**
 * In memory lock configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = "lock.registry-type", havingValue = "IN_MEMORY", matchIfMissing = true)
@RequiredArgsConstructor
public class ImMemoryLockConfiguration {

    private final LockProperties lockProperties;

    /**
     * Creates default lock registry bean.
     *
     * @return default lock registry bean
     */
    @Bean(LOCK_REGISTRY)
    public DefaultLockRegistry lockRegistry() {
        var lockRegistry = new DefaultLockRegistry();
        log.info("Default in memory lock registry has been initialized");
        return lockRegistry;
    }
}
