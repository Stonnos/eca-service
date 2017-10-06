package com.ecaservice.service.impl;

import com.ecaservice.service.CalculationExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Roman Batygin
 */
@Slf4j
@Service
public class CalculationExecutorServiceImpl implements CalculationExecutorService {

    private final ExecutorService executorService;

    /**
     * Constructor with spring dependency injection.
     *
     * @param executorService {@link ExecutorService} bean
     */
    @Autowired
    public CalculationExecutorServiceImpl(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public <V> V execute(Callable<V> task, long timeout, TimeUnit timeUnit)
            throws InterruptedException, ExecutionException, TimeoutException {
        Future<V> future = executorService.submit(task);
        log.trace("execute: calculation started");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        V result = timeout <= 0 ? future.get() : future.get(timeout, timeUnit);
        stopWatch.stop();
        log.trace("execute: calculation finished in {} s.", stopWatch.getTotalTimeSeconds());
        return result;
    }
}
