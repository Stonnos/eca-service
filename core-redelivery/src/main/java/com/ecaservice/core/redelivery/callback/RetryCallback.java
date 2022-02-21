package com.ecaservice.core.redelivery.callback;

import com.ecaservice.core.redelivery.model.RetryContext;

/**
 * Retry callback interface.
 *
 * @author Roman Batygin
 */
public interface RetryCallback {

    /**
     * Callback after success retry attempt.
     *
     * @param retryContext - retry context
     */
    void onSuccess(RetryContext retryContext);

    /**
     * Callback after retries exhausted.
     *
     * @param retryContext - retry context
     */
    void onRetryExhausted(RetryContext retryContext);

    /**
     * Callback after retry with exception.
     *
     * @param retryContext - retry context
     * @param ex           - exception
     */
    void onError(RetryContext retryContext, Exception ex);
}
