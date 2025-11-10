package com.ecaservice.auto.test.service.api;

import com.ecaservice.ers.dto.GetEvaluationResultsRequest;
import com.ecaservice.ers.dto.GetEvaluationResultsResponse;
import com.ecaservice.feign.oauth.config.FeignClientOauth2Configuration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Evaluation results service feign client interface.
 *
 * @author Roman Batygin
 */
@FeignClient(name = "eca-ers-api-client", url = "${auto-tests.ecaErsBaseUrl}/api",
        configuration = FeignClientOauth2Configuration.class)
public interface ErsClient {

    /**
     * Gets evaluation results report.
     *
     * @param request - get evaluation results request
     * @return evaluation results report response
     */
    @PostMapping(value = "/results")
    GetEvaluationResultsResponse getEvaluationResults(GetEvaluationResultsRequest request);
}
