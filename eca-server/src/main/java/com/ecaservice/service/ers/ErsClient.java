package com.ecaservice.service.ers;

import com.ecaservice.ers.dto.ClassifierOptionsRequest;
import com.ecaservice.ers.dto.ClassifierOptionsResponse;
import com.ecaservice.ers.dto.EvaluationResultsRequest;
import com.ecaservice.ers.dto.EvaluationResultsResponse;
import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Evaluation results service feign client interface.
 *
 * @author Roman Batygin
 */
@FeignClient(value = "eca-ers", path = "/api")
public interface ErsClient {

    /**
     * Saves evaluation results report.
     *
     * @param evaluationResultsRequest - evaluation results request
     * @return evaluation results response
     */
    @PostMapping(value = "/save")
    EvaluationResultsResponse save(EvaluationResultsRequest evaluationResultsRequest);

    /**
     * Gets evaluation results report.
     *
     * @param request - get evaluation results request
     * @return evaluation results report response
     */
    @PostMapping(value = "/results")
    GetEvaluationResultsResponse getEvaluationResults(GetEvaluationResultsRequest request);

    /**
     * Gets optimal classifiers options for specified request.
     *
     * @param classifierOptionsRequest - classifier options request
     * @return classifier options response
     */
    @PostMapping(value = "/optimal-classifier-options")
    ClassifierOptionsResponse getClassifierOptions(ClassifierOptionsRequest classifierOptionsRequest);
}
