package com.ecaservice.service.experiment;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements service for locking experiment history sending in manual mode.
 *
 * @author Roman Batygin
 */
@Service
public class ExperimentResultsLockService {

    private final Map<String, Object> lockStore = new ConcurrentHashMap<>();

    /**
     * Locks specified experiment.
     *
     * @param uuid - experiment uuid
     */
    public void lock(String uuid) {
        lockStore.putIfAbsent(uuid, new Object());
    }

    /**
     * Unlocks specified experiment.
     *
     * @param uuid - experiment uuid
     */
    public void unlock(String uuid) {
        lockStore.remove(uuid);
    }

    /**
     * Checks experiment for locking
     *
     * @param uuid - experiment uuid
     * @return {@code true} if experiment is locked
     */
    public boolean locked(String uuid) {
        return lockStore.containsKey(uuid);
    }
}
