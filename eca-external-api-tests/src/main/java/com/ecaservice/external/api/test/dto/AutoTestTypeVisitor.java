package com.ecaservice.external.api.test.dto;

/**
 * Auto test type visitor.
 *
 * @param <R> - result generic type
 * @author Roman Batygin
 */
public interface AutoTestTypeVisitor<R> {

    /**
     * Visit evaluation request process.
     *
     * @return any result
     */
    R visitEvaluationRequestProcess();

    /**
     * Visit experiment request process.
     *
     * @return any result
     */
    R visitExperimentRequestProcess();
}
