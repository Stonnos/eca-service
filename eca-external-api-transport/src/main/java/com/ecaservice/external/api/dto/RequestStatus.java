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
     * Invalid train data extension
     */
    INVALID_TRAIN_DATA_EXTENSION,

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
