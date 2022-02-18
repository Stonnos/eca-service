package com.ecaservice.core.redelivery.error;

/**
 * Default exception strategy implementation.
 *
 * @author Roman Batygin
 */
public class DefaultExceptionStrategy implements ExceptionStrategy {

    @Override
    public boolean notFatal(Exception ex) {
        return true;
    }
}
