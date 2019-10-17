package com.ecaservice.exception.experiment;

/**
 * Experiment results not found exception.
 *
 * @author Roman Batygin
 */
public class ResultsNotFoundException extends ExperimentException {

    /**
     * Creates experiment results not found exception.
     *
     * @param message error message
     */
    public ResultsNotFoundException(String message) {
        super(message);
    }
}
