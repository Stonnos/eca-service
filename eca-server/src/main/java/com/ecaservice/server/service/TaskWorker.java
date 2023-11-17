package com.ecaservice.server.service;

import com.ecaservice.server.model.Cancelable;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Async task worker.
 *
 * @param <T> - task result generic type
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public class TaskWorker<T> implements Cancelable {

    private final ExecutorService executorService;

    private Future<T> future;

    /**
     * Gets or create future object.
     *
     * @param callable - callable interface
     * @return future object
     */
    public synchronized Future<T> getOrCreateFuture(Callable<T> callable) {
        if (future == null) {
            future = executorService.submit(callable);
        }
        return future;
    }

    @Override
    public synchronized boolean isCancelled() {
        return future != null && future.isCancelled();
    }

    /**
     * Cancels task.
     */
    public synchronized void cancel() {
        if (future == null) {
            throw new IllegalStateException("Future is not initialized");
        } else {
            future.cancel(true);
        }
    }
}
