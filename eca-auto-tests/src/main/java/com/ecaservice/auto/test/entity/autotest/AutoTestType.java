package com.ecaservice.auto.test.entity.autotest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

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
    EXPERIMENT_REQUEST_PROCESS(List.of(TestFeature.EVALUATION_RESULTS, TestFeature.EXPERIMENT_EMAILS)),

    /**
     * Evaluation request process test
     */
    EVALUATION_REQUEST_PROCESS(Collections.singletonList(TestFeature.EVALUATION_RESULTS));

    /**
     * Supported features list
     */
    private final List<TestFeature> supportedFeatures;
}
