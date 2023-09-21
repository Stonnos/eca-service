package com.ecaservice.external.api.test.service.api;

import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.EvaluationResultsResponseDto;
import com.ecaservice.external.api.dto.ExperimentRequestDto;
import com.ecaservice.external.api.dto.ExperimentResultsResponseDto;
import com.ecaservice.external.api.dto.SimpleEvaluationResponseDto;
import com.ecaservice.feign.oauth.config.FeignClientOauth2Configuration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * External API client.
 *
 * @author Roman Batygin
 */
@FeignClient(name = "external-api-client", url = "${external-api-tests.url}/external-api",
        configuration = FeignClientOauth2Configuration.class)
public interface ExternalApiClient {

    /**
     * Creates evaluation request.
     *
     * @param evaluationRequestDto - evaluation request dto
     * @return evaluation response dto
     */
    @PostMapping(value = "/evaluation-request")
    SimpleEvaluationResponseDto evaluateRequest(@RequestBody EvaluationRequestDto evaluationRequestDto);

    /**
     * Creates experiment request.
     *
     * @param experimentRequestDto - experiment request dto
     * @return evaluation response dto
     */
    @PostMapping(value = "/experiment-request")
    SimpleEvaluationResponseDto experimentRequest(@RequestBody ExperimentRequestDto experimentRequestDto);

    /**
     * Gets evaluation results.
     *
     * @param requestId - request id
     * @return evaluation results response dto
     */
    @GetMapping(value = "/evaluation-results/{requestId}")
    EvaluationResultsResponseDto getEvaluationResults(@PathVariable String requestId);

    /**
     * Gets experiment results.
     *
     * @param requestId - request id
     * @return experiment results response dto
     */
    @GetMapping(value = "/experiment-results/{requestId}")
    ExperimentResultsResponseDto getExperimentResults(@PathVariable String requestId);
}
