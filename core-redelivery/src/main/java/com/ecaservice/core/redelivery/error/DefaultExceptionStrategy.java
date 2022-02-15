package com.ecaservice.core.redelivery.error;

import org.springframework.stereotype.Component;

/**
 * Default exception strategy implementation.
 *
 * @author Roman Batygin
 */
@Component
public class DefaultExceptionStrategy implements ExceptionStrategy {

    @Override
    public boolean notFatal(Exception ex) {
        return true;
    }
}
