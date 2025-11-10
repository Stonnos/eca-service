package com.ecaservice.server.service.ds;

import com.ecaservice.data.storage.dto.ExportInstancesResponseDto;
import com.ecaservice.feign.oauth.config.FeignClientOauth2Configuration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Data storage feign client interface.
 *
 * @author Roman Batygin
 */
@FeignClient(value = "eca-data-storage", path = "/api/internal/instances",
        configuration = FeignClientOauth2Configuration.class)
public interface DataStorageClient {

    /**
     * Export valid instances in central data storage.
     *
     * @param uuid - instances uuid
     * @return export instances response dto
     */
    @PostMapping(value = "/export-valid-instances")
    ExportInstancesResponseDto exportValidInstances(@RequestParam String uuid);
}