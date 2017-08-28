package com.ecaservice.service;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Service for executing asynchronous calculation.
 *
 * @author Roman Batygin
 */
public interface CalculationExecutorService {

    /**
     * Executes asynchronous task with the specified timeout.
     * When timeout is reached service interrupted.
     *
     * @param task     task object
     * @param <V>      result type to return
     * @param timeout  timeout value
     * @param timeUnit {@link TimeUnit} enum value
     * @return task result
     */
    <V> V execute(Callable<V> task, long timeout, TimeUnit timeUnit) throws InterruptedException, ExecutionException,
            TimeoutException;
}
