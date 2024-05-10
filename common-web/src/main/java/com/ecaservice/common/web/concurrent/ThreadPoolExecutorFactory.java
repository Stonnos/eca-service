package com.ecaservice.common.web.concurrent;

import io.micrometer.context.ContextSnapshotFactory;
import lombok.experimental.UtilityClass;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

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
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        ContextSnapshotFactory contextSnapshotFactory = ContextSnapshotFactory.builder().build();
        executor.setThreadFactory(runnable -> new Thread(contextSnapshotFactory.captureAll().wrap(runnable)));
        return executor;
    }
}
