package com.ecaservice.report.model;

/**
 * Report type enum.
 *
 * @author Roman Batygin
 */
public enum ReportType {

    /**
     * Experiments list report
     */
    EXPERIMENTS {
        @Override
        public void handle(ReportTypeVisitor visitor) {
            visitor.caseExperiments();
        }
    },

    /**
     * Evaluation logs list report
     */
    EVALUATION_LOGS {
        @Override
        public void handle(ReportTypeVisitor visitor) {
            visitor.caseEvaluationLogs();
        }
    },

    /**
     * Classifier options requests report
     */
    CLASSIFIERS_OPTIONS_REQUESTS {
        @Override
        public void handle(ReportTypeVisitor visitor) {
            visitor.caseClassifierOptionsRequests();
        }
    };

    /**
     * Visitor pattern common method.
     *
     * @param visitor   - visitor interface
     */
    public abstract void handle(ReportTypeVisitor visitor);
}
