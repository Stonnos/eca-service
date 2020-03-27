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
     * @param requestId - experiment request id
     */
    public void lock(String requestId) {
        lockStore.putIfAbsent(requestId, new Object());
    }

    /**
     * Unlocks specified experiment.
     *
     * @param requestId - experiment request id
     */
    public void unlock(String requestId) {
        lockStore.remove(requestId);
    }

    /**
     * Checks experiment for locking
     *
     * @param requestId - experiment request id
     * @return {@code true} if experiment is locked
     */
    public boolean locked(String requestId) {
        return lockStore.containsKey(requestId);
    }
}
