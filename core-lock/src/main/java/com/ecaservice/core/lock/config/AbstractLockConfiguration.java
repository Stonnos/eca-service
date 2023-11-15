package com.ecaservice.core.lock.config;

import com.ecaservice.core.lock.service.LockRegistryRepository;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.Map;

import static com.ecaservice.core.lock.config.CoreLockAutoConfiguration.DEFAULT_LOCK_REGISTRY_KEY;
import static com.google.common.collect.Maps.newHashMap;

/**
 * Abstract lock configuration.
 *
 * @author Roman Batygin
 */
public abstract class AbstractLockConfiguration {

    /**
     * Creates lock registry repository.
     *
     * @param lockProperties - lock properties
     * @return lock registry repository
     */
    public LockRegistryRepository lockRegistryRepository(LockProperties lockProperties) {
        Map<String, LockRegistry> lockRegistryMap = newHashMap();
        var defaultLockRegistry =
                createLockRegistry(DEFAULT_LOCK_REGISTRY_KEY, new LockProperties.LockRegistryProperties());
        lockRegistryMap.put(DEFAULT_LOCK_REGISTRY_KEY, defaultLockRegistry);
        lockProperties.getRegistries().forEach((registryKey, lockRegistryProperties) -> {
            var lockRegistry = createLockRegistry(registryKey, lockRegistryProperties);
            lockRegistryMap.put(registryKey, lockRegistry);
        });
        return new LockRegistryRepository(lockRegistryMap);
    }

    /**
     * Creates lock registry.
     *
     * @param registryKey            - registry key
     * @param lockRegistryProperties - lock registry properties
     * @return lock registry
     */
    public abstract LockRegistry createLockRegistry(String registryKey,
                                                    LockProperties.LockRegistryProperties lockRegistryProperties);
}
