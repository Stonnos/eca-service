package com.ecaservice.external.api.test.exception;

/**
 * Evaluation error exception.
 *
 * @author Roman Batygin
 */
public class ProcessEvaluationException extends RuntimeException {

    /**
     * Creates exception object.
     *
     * @param testId - auto test id
     */
    public ProcessEvaluationException(Long testId) {
        super(String.format("Evaluation error for test [%d]", testId));
    }
}
