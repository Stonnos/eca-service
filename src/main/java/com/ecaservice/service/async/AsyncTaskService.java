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
     * @param asyncTaskCallback - asynchronous task callback
     */
    void perform(AsyncTaskCallback asyncTaskCallback);
}
