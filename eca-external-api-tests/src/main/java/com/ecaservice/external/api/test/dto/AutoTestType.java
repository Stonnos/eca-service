package com.ecaservice.external.api.test.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Auto test type.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum AutoTestType {

    /**
     * Experiment request process test
     */
    EXPERIMENT_REQUEST_PROCESS,

    /**
     * Evaluation request process test
     */
    EVALUATION_REQUEST_PROCESS
}
