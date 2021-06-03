package com.ecaservice.core.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.locks.LockRegistry;

import java.util.concurrent.locks.Lock;

/**
 * Service for locks management.
 *
 * @author Roman Batygin
 */
@Slf4j
@RequiredArgsConstructor
public class LockService {

    private final LockRegistry lockRegistry;

    /**
     * Obtain lock for key.
     *
     * @param lockKey - lock key
     */
    public void lock(String lockKey) {
        log.debug("Obtain lock [{}]", lockKey);
        Lock lock = lockRegistry.obtain(lockKey);
        lock.lock();
        log.debug("Lock [{}] has been obtained", lockKey);
    }

    /**
     * Release lock with key.
     *
     * @param lockKey - lock key
     */
    public void unlock(String lockKey) {
        log.debug("Release lock [{}]", lockKey);
        Lock lock = lockRegistry.obtain(lockKey);
        lock.unlock();
        log.debug("Lock [{}] has been unlocked", lockKey);
    }
}
