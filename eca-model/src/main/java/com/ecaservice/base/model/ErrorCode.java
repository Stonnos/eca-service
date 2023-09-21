package com.ecaservice.base.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Error code.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    /**
     * Internal server error
     */
    INTERNAL_SERVER_ERROR("Internal server error"),

    /**
     * Service unavailable error
     */
    SERVICE_UNAVAILABLE("Service unavailable"),

    /**
     * Classifier options not found for specified request (used for evaluation optimizer requests)
     */
    CLASSIFIER_OPTIONS_NOT_FOUND("Classifier options not found for specified request"),

    /**
     * Training data not found in central data storage
     */
    TRAINING_DATA_NOT_FOUND("Training data not found for specified request"),

    /**
     * Invalid field value
     */
    INVALID_FIELD_VALUE("Invalid field value");

    /**
     * Error message
     */
    private final String errorMessage;
}
