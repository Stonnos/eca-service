package com.ecaservice.server.service.push.dictionary;


import com.ecaservice.server.model.entity.AbstractEvaluationEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Evaluation push properties.
 *
 * @author Roman Batygin
 */
@Getter
@RequiredArgsConstructor
public enum EvaluationPushProperty {

    /**
     * Evaluation id property
     */
    ID("id") {
        @Override
        public String visit(EvaluationPushPropertyVisitor visitor, AbstractEvaluationEntity evaluationEntity) {
            return visitor.visitId(evaluationEntity);
        }
    },

    /**
     * Evaluation request id property
     */
    REQUEST_ID("requestId") {
        @Override
        public String visit(EvaluationPushPropertyVisitor visitor, AbstractEvaluationEntity evaluationEntity) {
            return visitor.visitRequestId(evaluationEntity);
        }
    },

    /**
     * Evaluation request status property
     */
    REQUEST_STATUS("requestStatus") {
        @Override
        public String visit(EvaluationPushPropertyVisitor visitor, AbstractEvaluationEntity evaluationEntity) {
            return visitor.visitRequestStatus(evaluationEntity);
        }
    };

    /**
     * Property name
     */
    private final String propertyName;

    /**
     * Invokes visitor.
     *
     * @param visitor          - visitor interface
     * @param evaluationEntity - evaluation entity
     * @return property value
     */
    public abstract String visit(EvaluationPushPropertyVisitor visitor, AbstractEvaluationEntity evaluationEntity);
}
