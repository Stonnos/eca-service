package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.server.model.entity.Experiment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Experiment email message template variables.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum ExperimentEmailTemplateVariable {

    /**
     * Experiment type
     */
    EXPERIMENT_TYPE("experimentType") {
        @Override
        public String visit(ExperimentEmailTemplateVariableVisitor visitor, Experiment experiment) {
            return visitor.visitExperimentType(experiment);
        }
    },

    /**
     * Experiment download url
     */
    DOWNLOAD_URL("downloadUrl") {
        @Override
        public String visit(ExperimentEmailTemplateVariableVisitor visitor, Experiment experiment) {
            return visitor.visitDownloadUrl(experiment);
        }
    },

    /**
     * Timeout value
     */
    TIMEOUT_VALUE("timeout") {
        @Override
        public String visit(ExperimentEmailTemplateVariableVisitor visitor, Experiment experiment) {
            return visitor.visitTimeout(experiment);
        }
    },

    /**
     * Request id
     */
    REQUEST_ID("requestId") {
        @Override
        public String visit(ExperimentEmailTemplateVariableVisitor visitor, Experiment experiment) {
            return visitor.visitRequestId(experiment);
        }
    };

    /**
     * Variable name
     */
    private final String variableName;

    /**
     * Invokes visitor.
     *
     * @param visitor    - visitor interface
     * @param experiment - experiment entity
     * @return variable value
     */
    public abstract String visit(ExperimentEmailTemplateVariableVisitor visitor, Experiment experiment);
}
