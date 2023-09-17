package com.ecaservice.server.service.push.dictionary;


import com.ecaservice.server.model.entity.Experiment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Experiment push properties.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum ExperimentPushProperty {

    /**
     * Experiment id property
     */
    ID("experimentId") {
        @Override
        public String visit(ExperimentPushPropertyVisitor visitor, Experiment experiment) {
            return visitor.visitId(experiment);
        }
    },

    /**
     * Experiment request id property
     */
    REQUEST_ID("experimentRequestId"){
        @Override
        public String visit(ExperimentPushPropertyVisitor visitor, Experiment experiment) {
            return visitor.visitRequestId(experiment);
        }
    },

    /**
     * Experiment request status property
     */
    REQUEST_STATUS("experimentRequestStatus"){
        @Override
        public String visit(ExperimentPushPropertyVisitor visitor, Experiment experiment) {
            return visitor.visitRequestStatus(experiment);
        }
    };

    /**
     * Property name
     */
    private final String propertyName;

    /**
     * Invokes visitor.
     *
     * @param visitor    - visitor interface
     * @param experiment - experiment entity
     * @return property value
     */
    public abstract String visit(ExperimentPushPropertyVisitor visitor, Experiment experiment);
}
