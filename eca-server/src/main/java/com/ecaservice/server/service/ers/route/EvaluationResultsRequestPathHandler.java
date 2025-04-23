package com.ecaservice.server.service.ers.route;

import com.ecaservice.server.model.entity.EvaluationResultsRequestEntity;
import org.springframework.stereotype.Component;

/**
 * Evaluation results request path handler.
 *
 * @author Roman Batygin
 */
@Component
public class EvaluationResultsRequestPathHandler
        extends AbstractEvaluationResultsRequestPathHandler<EvaluationResultsRequestEntity> {

    public EvaluationResultsRequestPathHandler() {
        super(EvaluationResultsRequestEntity.class);
    }

    @Override
    public String handlePath(EvaluationResultsRequestEntity ersRequestEntity) {
        return String.format("/dashboard/classifiers/evaluation-results/%d", ersRequestEntity.getEvaluationLog().getId());
    }
}
