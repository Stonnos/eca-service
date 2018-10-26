package com.ecaservice.service.async;

import com.ecaservice.config.EcaServiceParam;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Implements service for executing asynchronous tasks.
 *
 * @author Roman Batygin
 */
@Component
public class AsyncTaskCallbackServiceImpl implements AsyncTaskService {

    @Async(EcaServiceParam.SIMPLE_POOL_EXECUTOR)
    @Override
    public void perform(AsyncTaskCallback asyncTaskCallback) {
        asyncTaskCallback.run();
    }
}
