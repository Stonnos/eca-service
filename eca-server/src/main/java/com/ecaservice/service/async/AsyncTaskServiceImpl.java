package com.ecaservice.service.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import static com.ecaservice.config.EcaServiceParam.ECA_POOL_EXECUTOR;

/**
 * Implements service for executing asynchronous tasks.
 *
 * @author Roman Batygin
 */
@Component
public class AsyncTaskServiceImpl implements AsyncTaskService {

    @Async(ECA_POOL_EXECUTOR)
    @Override
    public void perform(Runnable runnable) {
        runnable.run();
    }
}
