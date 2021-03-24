package com.ecaservice.ers.dto;

/**
 * ERS response status.
 *
 * @author Roman Batygin
 */
public enum ResponseStatus {

    /**
     * Success status
     */
    SUCCESS,

    /**
     * Duplicate request id
     */
    DUPLICATE_REQUEST_ID,

    /**
     * Training data not found
     */
    DATA_NOT_FOUND,

    /**
     * Results not found
     */
    RESULTS_NOT_FOUND
}
