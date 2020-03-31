package com.ecaservice.exception.experiment;

import com.ecaservice.exception.EcaServiceException;

/**
 * Experiment exception class.
 *
 * @author Roman Batygin
 */
public class ExperimentException extends EcaServiceException {

    /**
     * Creates experiment exception.
     *
     * @param message error message
     */
    public ExperimentException(String message) {
        super(message);
    }
}
