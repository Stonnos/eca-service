package com.ecaservice.server.service.push.dictionary;


import com.ecaservice.server.model.entity.AbstractEvaluationEntity;
import org.springframework.stereotype.Component;

/**
 * Evaluation push property visitor.
 *
 * @author Roman Batygin
 */
@Component
public class EvaluationPushPropertyVisitorImpl implements EvaluationPushPropertyVisitor {

    @Override
    public String visitId(AbstractEvaluationEntity evaluationEntity) {
        return String.valueOf(evaluationEntity.getId());
    }

    @Override
    public String visitRequestId(AbstractEvaluationEntity evaluationEntity) {
        return evaluationEntity.getRequestId();
    }

    @Override
    public String visitRequestStatus(AbstractEvaluationEntity evaluationEntity) {
        return evaluationEntity.getRequestStatus().name();
    }
}
