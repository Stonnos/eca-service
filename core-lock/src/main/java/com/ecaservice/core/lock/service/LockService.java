package com.ecaservice.core.lock.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
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
            log.error("There was an error while release lock with key [{}]: {}", lockKey, ex.getMessage());
        }
    }

    /**
     * Performs action using lock.
     *
     * @param lockKey              - lock key
     * @param actionCallback       - action callback
     * @param lockAcquiredCallback - callback in case if lock is already acquired for specified key
     */
    public void doInLock(String lockKey, Callback actionCallback, Callback lockAcquiredCallback) {
        Lock lock = lockRegistry.obtain(lockKey);
        try {
            if (!lock.tryLock()) {
                lockAcquiredCallback.apply();
            } else {
                actionCallback.apply();
                unlock(lockKey);
            }
        } catch (CannotAcquireLockException ex) {
            log.error("Acquire lock error: {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("There was an error: {}", ex.getMessage());
            unlock(lockKey);
        }
    }
}
