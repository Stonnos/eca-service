package com.ecaservice.server.service.push.dictionary;


import com.ecaservice.server.model.entity.AbstractEvaluationEntity;

/**
 * Evaluation push property visitor.
 *
 * @author Roman Batygin
 */
public interface EvaluationPushPropertyVisitor {

    /**
     * Visits evaluation id.
     *
     * @param evaluationEntity - evaluation entity
     * @return evaluation id value
     */
    String visitId(AbstractEvaluationEntity evaluationEntity);

    /**
     * Visits evaluation request id.
     *
     * @param evaluationEntity - evaluation entity
     * @return evaluation request id value
     */
    String visitRequestId(AbstractEvaluationEntity evaluationEntity);

    /**
     * Visits evaluation request status.
     *
     * @param evaluationEntity - evaluation entity
     * @return evaluation request status value
     */
    String visitRequestStatus(AbstractEvaluationEntity evaluationEntity);
}
