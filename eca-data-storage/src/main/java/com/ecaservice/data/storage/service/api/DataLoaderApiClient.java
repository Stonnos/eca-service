package com.ecaservice.data.storage.service.api;

import com.ecaservice.data.loader.dto.UploadInstancesResponseDto;
import com.ecaservice.feign.oauth.config.FeignClientOauth2Configuration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * Central data loader API client.
 *
 * @author Roman Batygin
 */
@FeignClient(name = "eca-data-loader", path = "/api/external", configuration = FeignClientOauth2Configuration.class)
public interface DataLoaderApiClient {

    /**
     * Uploads train data file to server.
     *
     * @param instancesFile - train data file
     * @return upload instances response dto
     */
    @PostMapping(value = "/upload-train-data", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    UploadInstancesResponseDto uploadInstances(@RequestPart("instancesFile") MultipartFile instancesFile);
}
