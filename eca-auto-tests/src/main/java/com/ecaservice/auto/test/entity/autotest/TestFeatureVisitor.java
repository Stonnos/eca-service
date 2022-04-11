package com.ecaservice.auto.test.entity.autotest;

/**
 * Test feature visitor interface.
 *
 * @param <T> - result generic type
 * @author Roman Batygin
 */
public interface TestFeatureVisitor<T> {

    /**
     * Visit experiment emails feature.
     *
     * @return result
     */
    default T visitExperimentEmailsFeature() {
        throw new UnsupportedOperationException("Unsupported experiment emails feature");
    }

    /**
     * Visit evaluation results feature.
     *
     * @return result
     */
    default T visitEvaluationResults() {
        throw new UnsupportedOperationException("Unsupported evaluation results feature");
    }
}
