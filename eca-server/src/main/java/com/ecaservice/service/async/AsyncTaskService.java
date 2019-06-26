package com.ecaservice.service.async;

/**
 * Service for executing asynchronous tasks.
 *
 * @author Roman Batygin
 */
public interface AsyncTaskService {

    /**
     * Performs asynchronous task.
     *
     * @param runnable - runnable object
     */
    void perform(Runnable runnable);
}
