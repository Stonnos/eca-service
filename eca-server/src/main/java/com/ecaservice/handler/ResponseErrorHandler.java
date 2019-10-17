package com.ecaservice.handler;

/**
 * Response error handler interface.
 *
 * @param <T> - response generic type
 * @author Roman Batygin
 */
public interface ResponseErrorHandler<T> {

    /**
     * Checks if response has error.
     *
     * @param response - response object
     * @return {@code true} if response has error
     */
    boolean hasError(T response);

    /**
     * Handles error response.
     *
     * @param response - response object
     */
    void handleError(T response);
}
