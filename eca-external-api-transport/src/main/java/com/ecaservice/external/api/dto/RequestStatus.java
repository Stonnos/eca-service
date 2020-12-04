package com.ecaservice.external.api.dto;

import lombok.RequiredArgsConstructor;

/**
 * Request status enum.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public enum RequestStatus {

    /**
     * Success request
     */
    SUCCESS(null),

    /**
     * Validation error
     */
    VALIDATION_ERROR("Validation errors"),

    /**
     * Train data not found for specified url
     */
    DATA_NOT_FOUND("Train data not found for specified url"),

    /**
     * Unknown error
     */
    ERROR("Unknown error"),

    /**
     * Request timeout
     */
    TIMEOUT("Request timeout"),

    /**
     * Internal system unavailable
     */
    SERVICE_UNAVAILABLE("Internal service unavailable");

    private final String description;

    /**
     * ERS report status description.
     *
     * @return ERS report status status description
     */
    public String getDescription() {
        return description;
    }
}
