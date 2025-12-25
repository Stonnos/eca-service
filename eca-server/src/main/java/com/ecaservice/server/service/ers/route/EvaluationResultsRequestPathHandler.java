package com.ecaservice.server.service.ers.route;

import com.ecaservice.server.model.entity.EvaluationResultsRequestEntity;
import org.springframework.stereotype.Component;

import static com.ecaservice.server.util.RoutePaths.EVALUATION_RESULTS_DETAILS_PATH;

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
        return String.format(EVALUATION_RESULTS_DETAILS_PATH, ersRequestEntity.getEvaluationLog().getId());
    }
}
