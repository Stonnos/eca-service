package com.ecaservice.service.lock;

import com.ecaservice.exception.LockTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {

    private final LockStorage lockStorage;

    /**
     * Waits for lock with specified options.
     *
     * @param name       - lock name
     * @param key        - lock key
     * @param expiration - lock expiration in millis
     * @param timeout    - wait lock timeout in millis
     * @param retry      - interval in millis for next attempt locking
     */
    public void waitForLock(String name, String key, long expiration, long timeout, long retry) {
        boolean locked = false;
        LocalDateTime timeToGiveUp = LocalDateTime.now().plus(timeout, ChronoField.MILLI_OF_DAY.getBaseUnit());
        while (!locked && LocalDateTime.now().isBefore(timeToGiveUp)) {
            locked = lockStorage.lock(name, key, expiration);
            if (!locked) {
                log.info("Waiting for lock with name [{}], key [{}]....", name, key);
                try {
                    Thread.sleep(retry);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (!locked) {
            throw new LockTimeoutException(String.format("Can't wait lock with name [%s], key [%s]", name, key));
        }
    }

    /**
     * Perform unlock operation.
     *
     * @param name - lock name
     * @param key  - lock key
     */
    public void unlock(String name, String key) {
        lockStorage.unlock(name, key);
    }

    /**
     * Reset lock if expired.
     *
     * @param name - lock name
     * @param key  - lock key
     */
    public void resetLockIfExpired(String name, String key) {
        lockStorage.resetLockIfExpired(name, key);
    }
}
