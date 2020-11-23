package com.ecaservice.external.api.test.service.api;

import com.ecaservice.external.api.dto.InstancesDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * External API client.
 *
 * @author Roman Batygin
 */
@FeignClient(name = "external-api-client", url = "http://localhost:8080/external-api")
public interface ExternalApiClient {

    /**
     * Uploads train data file to server.
     *
     * @param trainingData - train data file
     * @return instances dto
     */
    @PostMapping(value = "/uploads-train-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    InstancesDto uploadInstances(@RequestPart("trainingData") MultipartFile trainingData);
}
