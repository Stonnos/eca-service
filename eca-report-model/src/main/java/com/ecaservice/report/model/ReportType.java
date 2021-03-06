package com.ecaservice.report.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Report type enum.
 *
 * @author Roman Batygin
 */
@RequiredArgsConstructor
public enum ReportType {

    /**
     * Experiments list report
     */
    EXPERIMENTS("experiments-report") {
        @Override
        public void handle(ReportTypeVisitor visitor) {
            visitor.caseExperiments();
        }
    },

    /**
     * Evaluation logs list report
     */
    EVALUATION_LOGS("evaluation-logs-report") {
        @Override
        public void handle(ReportTypeVisitor visitor) {
            visitor.caseEvaluationLogs();
        }
    },

    /**
     * Classifier options requests report
     */
    CLASSIFIERS_OPTIONS_REQUESTS("classifier-options-requests") {
        @Override
        public void handle(ReportTypeVisitor visitor) {
            visitor.caseClassifierOptionsRequests();
        }
    },

    /**
     * Classifiers configuration report
     */
    CLASSIFIERS_CONFIGURATION("classifiers-configuration") {
        @Override
        public void handle(ReportTypeVisitor visitor) {
            visitor.caseClassifiersConfiguration();
        }
    },

    /**
     * Audit logs report
     */
    AUDIT_LOGS("audit-logs") {
        @Override
        public void handle(ReportTypeVisitor visitor) {
            visitor.caseAuditLogs();
        }
    };

    /**
     * Report name
     */
    @Getter
    private final String name;

    /**
     * Visitor pattern common method.
     *
     * @param visitor   - visitor interface
     */
    public abstract void handle(ReportTypeVisitor visitor);
}
