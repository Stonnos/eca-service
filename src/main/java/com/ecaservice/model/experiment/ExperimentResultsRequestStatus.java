package com.ecaservice.model.experiment;

/**
 * Experiment results request status.
 *
 * @author Roman Batygin
 */
public enum ExperimentResultsRequestStatus {

    /**
     * New status
     */
    NEW,

    /**
     * Complete status. Experiment results has been successfully sending to ERS
     */
    COMPLETE,

    /**
     * Error status
     */
    ERROR
}
