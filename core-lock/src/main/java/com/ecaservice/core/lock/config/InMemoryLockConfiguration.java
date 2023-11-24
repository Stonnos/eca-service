package com.ecaservice.core.lock.config;

import com.ecaservice.core.lock.service.LockRegistryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.locks.DefaultLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;

/**
 * In memory lock configuration.
 *
 * @author Roman Batygin
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = "lock.registry-type", havingValue = "IN_MEMORY", matchIfMissing = true)
@RequiredArgsConstructor
public class InMemoryLockConfiguration extends AbstractLockConfiguration {

    /**
     * Creates lock registry repository bean.
     *
     * @param lockProperties - lock properties
     * @return lock registry repository
     */
    @Bean
    @Override
    public LockRegistryRepository lockRegistryRepository(LockProperties lockProperties) {
        var lockRegistryRepository = super.lockRegistryRepository(lockProperties);
        log.info("In memory lock registry repository has been initialized");
        return lockRegistryRepository;
    }

    @Override
    public LockRegistry createLockRegistry(String registryKey,
                                           LockProperties.LockRegistryProperties lockRegistryProperties) {
        var lockRegistry = new DefaultLockRegistry();
        log.info("In memory lock [{}] registry has been initialized", registryKey);
        return lockRegistry;
    }
}
