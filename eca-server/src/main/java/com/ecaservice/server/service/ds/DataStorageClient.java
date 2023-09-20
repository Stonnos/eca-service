package com.ecaservice.server.service.ds;

import com.ecaservice.data.storage.dto.ExportInstancesResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Data storage feign client interface.
 *
 * @author Roman Batygin
 */
@FeignClient(value = "eca-data-storage", path = "/api/internal/instances")
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