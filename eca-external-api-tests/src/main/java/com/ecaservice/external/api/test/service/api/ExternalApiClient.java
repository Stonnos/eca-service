package com.ecaservice.external.api.test.service.api;

import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.EvaluationResultsResponseDto;
import com.ecaservice.external.api.dto.InstancesDto;
import com.ecaservice.external.api.dto.ResponseDto;
import com.ecaservice.external.api.dto.SimpleEvaluationResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * External API client.
 *
 * @author Roman Batygin
 */
@FeignClient(name = "external-api-client", url = "${external-api-tests.url}/external-api")
public interface ExternalApiClient {

    /**
     * Uploads train data file to server.
     *
     * @param trainingData - train data file
     * @return instances dto
     */
    @PostMapping(value = "/uploads-train-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseDto<InstancesDto> uploadInstances(@RequestPart("trainingData") MultipartFile trainingData);

    /**
     * Creates evaluation request.
     *
     * @param evaluationRequestDto - evaluation request dto
     * @return evaluation response dto
     */
    @PostMapping(value = "/evaluation-request")
    ResponseDto<SimpleEvaluationResponseDto> evaluateRequest(@RequestBody EvaluationRequestDto evaluationRequestDto);

    /**
     * Gets evaluation results.
     *
     * @param requestId - request id
     * @return evaluation response dto
     */
    @GetMapping(value = "/evaluation-results/{requestId}")
    ResponseDto<EvaluationResultsResponseDto> getEvaluationResults(@PathVariable String requestId);
}
