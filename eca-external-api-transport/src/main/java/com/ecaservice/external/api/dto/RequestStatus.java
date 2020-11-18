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
     * Invalid train data url
     */
    INVALID_URL,

    /**
     * Train data not found for specified url
     */
    DATA_NOT_FOUND,

    /**
     * Unknown error
     */
    ERROR,

    /**
     * Request timeout
     */
    TIMEOUT,

    /**
     * Internal system unavailable
     */
    SERVICE_UNAVAILABLE
}
