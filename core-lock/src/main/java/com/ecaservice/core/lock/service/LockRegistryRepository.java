package com.ecaservice.core.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.Map;

/**
 * Lock registry repository.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class LockRegistryRepository {

    /**
     * Lock registries map
     */
    private final Map<String, LockRegistry> lockRegistries;

    /**
     * Gets lock registry by key.
     *
     * @param registryKey - registry key
     * @return lock registry
     */
    public LockRegistry getRegistry(String registryKey) {
        log.debug("Gets lock registry by key [{}]", registryKey);
        var lockRegistry = lockRegistries.get(registryKey);
        if (lockRegistry == null) {
            throw new IllegalStateException(String.format("Lock registry with key [%s] not found!", registryKey));
        }
        log.debug("Lock registry [{}] has been fetched", registryKey);
        return lockRegistry;
    }
}
