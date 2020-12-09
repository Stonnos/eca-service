package com.ecaservice.report.model;

/**
 * Interface for visitor pattern.
 *
 * @author Roman Batygin
 */
public interface ReportTypeVisitor {

    /**
     * Method executed in case if experiment status is EXPERIMENTS.
     */
    void caseExperiments();

    /**
     * Method executed in case if experiment status is EVALUATION_LOGS.
     */
    void caseEvaluationLogs();

    /**
     * Method executed in case if experiment status is CLASSIFIERS_OPTIONS_REQUESTS.
     */
    void caseClassifierOptionsRequests();

    /**
     * Method executed in case if experiment status is CLASSIFIERS_CONFIGURATION.
     */
    void caseClassifiersConfiguration();
}
