package com.ecaservice.exception;

/**
 * Experiment exception class.
 *
 * @author Roman Batygin
 */

public class ExperimentException extends EcaServiceException {

    /**
     * Creates <tt>ExperimentException</tt> object
     *
     * @param message error message
     */
    public ExperimentException(String message) {
        super(message);
    }
}
