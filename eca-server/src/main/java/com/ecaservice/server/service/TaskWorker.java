package com.ecaservice.server.service;

import com.ecaservice.server.model.Cancelable;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
     * Perform task in new thread and wit for task result.
     *
     * @param callable - callable interface (task)
     * @param timeout  - task timeout
     * @param timeUnit - task unit
     * @return task result
     * @throws ExecutionException   in case of execution error
     * @throws InterruptedException in case if thread is interrupted
     * @throws TimeoutException     in case of task timeout
     */
    public T performTask(Callable<T> callable, long timeout, TimeUnit timeUnit)
            throws ExecutionException, InterruptedException, TimeoutException {
        future = executorService.submit(callable);
        return future.get(timeout, timeUnit);
    }

    @Override
    public boolean isCancelled() {
        return future != null && future.isCancelled();
    }

    @Override
    public void cancel() {
        if (future == null) {
            throw new IllegalStateException("Future is not initialized");
        } else {
            future.cancel(true);
        }
    }
}
