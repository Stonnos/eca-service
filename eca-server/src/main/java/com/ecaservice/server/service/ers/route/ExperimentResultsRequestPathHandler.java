package com.ecaservice.server.service.ers.route;

import com.ecaservice.server.model.entity.ExperimentResultsRequest;
import org.springframework.stereotype.Component;

/**
 * Experiment results request path handler.
 *
 * @author Roman Batygin
 */
@Component
public class ExperimentResultsRequestPathHandler
        extends AbstractEvaluationResultsRequestPathHandler<ExperimentResultsRequest> {

    public ExperimentResultsRequestPathHandler() {
        super(ExperimentResultsRequest.class);
    }

    @Override
    public String handlePath(ExperimentResultsRequest experimentResultsRequest) {
        return String.format("/dashboard/experiments/results/details/%d", experimentResultsRequest.getExperimentResults().getId());
    }
}
