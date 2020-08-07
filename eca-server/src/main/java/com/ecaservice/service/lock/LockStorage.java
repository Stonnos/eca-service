package com.ecaservice.service.lock;

/**
 * Lock storage interface.
 */
public interface LockStorage {

    /**
     * Performs lock operation.
     *
     * @param name       - lock name
     * @param key        - lock key
     * @param expiration - lock expiration in millis
     * @return {@code true} if lock is acquired
     */
    boolean lock(String name, String key, long expiration);

    /**
     * Performs unlock operation
     *
     * @param name - lock name
     * @param key  - lock key
     */
    void unlock(String name, String key);

    /**
     * Reset lock if expired.
     *
     * @param name - lock name
     * @param key  - lock key
     */
    void resetLockIfExpired(String name, String key);
}
