package com.ecaservice.core.redelivery.error;

import feign.FeignException;
import feign.RetryableException;

import java.util.stream.Stream;

/**
 * Feign exception strategy implementation.
 *
 * @author Roman Batygin
 */
public class FeignExceptionStrategy implements ExceptionStrategy {

    private final Class<?>[] notFatalExceptions = new Class<?>[] {
            FeignException.ServiceUnavailable.class,
            RetryableException.class
    };

    @Override
    public boolean notFatal(Exception ex) {
        return Stream.of(notFatalExceptions)
                .anyMatch(notFatalException -> notFatalException.isAssignableFrom(ex.getClass()));
    }
}
