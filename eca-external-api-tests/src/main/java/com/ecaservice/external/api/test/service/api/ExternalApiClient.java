package com.ecaservice.external.api.test.service.api;

import com.ecaservice.external.api.dto.EvaluationRequestDto;
import com.ecaservice.external.api.dto.EvaluationResponseDto;
import com.ecaservice.external.api.dto.InstancesDto;
import com.ecaservice.external.api.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
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
@FeignClient(name = "external-api-client", url = "${external-api-tests.url}")
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
     * Evaluates classifier model.
     *
     * @param evaluationRequestDto - evaluation request dto
     * @return evaluation response dto
     */
    @PostMapping(value = "/evaluate")
    ResponseDto<EvaluationResponseDto> evaluateModel(@RequestBody EvaluationRequestDto evaluationRequestDto);

    /**
     * Downloads model with specified request id
     *
     * @param requestId - request id
     * @return resource object
     */
    @GetMapping(value = "/download-model/{requestId}")
    Resource downloadModel(@PathVariable String requestId);
}
