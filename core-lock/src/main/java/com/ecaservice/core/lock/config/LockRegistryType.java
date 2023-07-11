package com.ecaservice.core.lock.config;

/**
 * Lock registry type.
 *
 * @author Roman Batygin
 */
public enum LockRegistryType {

    /**
     * In memory reentrant lock
     */
    IN_MEMORY,

    /**
     * Redis lock
     */
    REDIS
}
