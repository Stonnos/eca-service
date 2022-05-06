package com.ecaservice.core.lock.service;

import com.ecaservice.core.lock.exception.CannotUnlockException;
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
        try {
            Lock lock = lockRegistry.obtain(lockKey);
            lock.unlock();
            log.debug("Lock [{}] has been unlocked", lockKey);
        } catch (Exception ex) {
            throw new CannotUnlockException(ex.getMessage());
        }
    }

    /**
     * Tries to acquire lock.
     *
     * @param lockKey - lock key
     * @return {@code true} if lock has been acquired, {@code false} otherwise
     */
    public boolean tryLock(String lockKey) {
        Lock lock = lockRegistry.obtain(lockKey);
        return lock.tryLock();
    }
}
