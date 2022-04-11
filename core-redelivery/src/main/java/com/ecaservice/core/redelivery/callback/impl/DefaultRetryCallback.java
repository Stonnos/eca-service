package com.ecaservice.core.redelivery.callback.impl;

import com.ecaservice.core.redelivery.callback.RetryCallback;
import com.ecaservice.core.redelivery.model.RetryContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Retry callback default implementation.
 *
 * @author Roman Batygin
 */
@Slf4j
public class DefaultRetryCallback implements RetryCallback {

    @Override
    public void onSuccess(RetryContext retryContext) {
        log.info("Success retry for request [{}]", retryContext.getRequestId());
    }

    @Override
    public void onRetryExhausted(RetryContext retryContext) {
        log.info("Retries exhausted for request [{}]. Retries [{}] of [{}]", retryContext.getRequestId(),
                retryContext.getCurrentRetries(), retryContext.getMaxRetries());
    }

    @Override
    public void onError(RetryContext retryContext, Exception ex) {
       if (retryContext.getMaxRetries() > 0) {
           log.info("Retry has been failed for request [{}]. Retries [{}] of [{}]", retryContext.getRequestId(),
                   retryContext.getCurrentRetries(), retryContext.getMaxRetries());
       } else {
           log.info("Retry has been failed for request [{}]. Current retries [{}]", retryContext.getRequestId(),
                   retryContext.getCurrentRetries());
       }
    }
}
