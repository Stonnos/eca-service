package com.ecaservice.service.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Implements service for executing asynchronous tasks.
 *
 * @author Roman Batygin
 */
@Component
public class AsyncTaskServiceImpl implements AsyncTaskService {

    @Async("ecaThreadPoolTaskExecutor")
    @Override
    public void perform(Runnable runnable) {
        runnable.run();
    }
}
