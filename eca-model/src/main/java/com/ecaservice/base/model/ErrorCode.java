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
     * Classifier options not found for specified request
     */
    CLASSIFIER_OPTIONS_NOT_FOUND("Classifier options not found for specified request"),

    /**
     * Training data not found (used for evaluation optimizer requests)
     */
    TRAINING_DATA_NOT_FOUND("Training data not found for specified request");

    /**
     * Error message
     */
    private final String errorMessage;
}
