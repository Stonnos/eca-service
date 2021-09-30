package com.ecaservice.server.exception.experiment;

import com.ecaservice.server.exception.EcaServiceException;

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
