package com.ecaservice.service.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Implements service for executing asynchronous tasks.
 *
 * @author Roman Batygin
 */
@Component
public class AsyncTaskCallbackServiceImpl implements AsyncTaskService {

    @Async
    @Override
    public void perform(AsyncTaskCallback asyncTaskCallback) {
        asyncTaskCallback.run();
    }
}
