package com.ecaservice.server.service.ds;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Data storage feign client interface.
 *
 * @author Roman Batygin
 */
@FeignClient(value = "eca-data-storage", path = "/api/internal/instances")
public interface DataStorageClient {

    /**
     * Downloads valid instances report.
     *
     * @param uuid - instances uuid
     * @return resource object
     */
    Resource downloadValidInstancesReport(@RequestParam String uuid);
}