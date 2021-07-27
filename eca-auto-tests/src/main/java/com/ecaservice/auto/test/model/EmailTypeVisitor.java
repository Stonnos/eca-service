package com.ecaservice.auto.test.model;

/**
 * Email type visitor interface.
 *
 * @author Roman Batygin
 */
public interface EmailTypeVisitor {

    /**
     * Method executed in case if email type is NEW_EXPERIMENT.
     */
    default void visitNewExperiment() {
    }

    /**
     * Method executed in case if email type is IN_PROGRESS_EXPERIMENT.
     */
    default void visitInProgressExperiment() {
    }

    /**
     * Method executed in case if email type is FINISHED_EXPERIMENT.
     */
    default void visitFinishedExperiment() {
    }

    /**
     * Method executed in case if email type is ERROR_EXPERIMENT.
     */
    default void visitErrorExperiment() {
    }

    /**
     * Method executed in case if email type is TIMEOUT_EXPERIMENT.
     */
    default void visitTimeoutExperiment() {
    }
}
