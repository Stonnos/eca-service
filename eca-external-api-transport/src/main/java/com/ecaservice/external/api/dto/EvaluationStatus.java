package com.ecaservice.external.api.dto;

import lombok.RequiredArgsConstructor;

/**
 * Evaluation status enum.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public enum EvaluationStatus {


    /**
     * Evaluation in progress
     */
    IN_PROGRESS("Evaluation in progress"),

    /**
     * Evaluation finished
     */
    FINISHED("Evaluation finished"),

    /**
     * Unknown error
     */
    ERROR("Unknown error");

    private final String description;

    /**
     * Evaluation status description.
     *
     * @return evaluation status description
     */
    public String getDescription() {
        return description;
    }
}
