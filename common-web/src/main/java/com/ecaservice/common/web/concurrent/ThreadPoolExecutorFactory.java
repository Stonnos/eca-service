package com.ecaservice.common.web.concurrent;

import io.micrometer.context.ContextExecutorService;
import io.micrometer.context.ContextSnapshotFactory;
import lombok.experimental.UtilityClass;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread pool executor factory.
 *
 * @author Roman Batygin
 */
@UtilityClass
public class ThreadPoolExecutorFactory {

    /**
     * Creates thread pool task executor.
     *
     * @param poolSize - thread pool size
     * @return thread pool task executor
     */
    public static Executor createThreadPoolTaskExecutor(int poolSize) {
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);
        return ContextExecutorService.wrap(executor, ContextSnapshotFactory.builder().build()::captureAll);
    }
}
