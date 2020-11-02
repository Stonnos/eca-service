package com.ecaservice.external.api.dto;

/**
 * Request status enum.
 *
 * @author Roman Batygin
 */
public enum RequestStatus {

    /**
     * Success request
     */
    SUCCESS,

    /**
     * Train data not found for specified url
     */
    DATA_NOT_FOUND,

    /**
     * Unknown error
     */
    ERROR,

    /**
     * Internal system unavailable
     */
    SERVICE_UNAVAILABLE
}
