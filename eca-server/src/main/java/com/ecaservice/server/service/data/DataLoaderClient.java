package com.ecaservice.server.service.data;

import com.ecaservice.data.loader.dto.InstancesMetaInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Data loader feign client interface.
 *
 * @author Roman Batygin
 */
@FeignClient(value = "eca-data-loader", path = "/api/internal/instances")
public interface DataLoaderClient {

    /**
     * Gets instances meta data info.
     *
     * @param uuid - instances uuid
     * @return instances meta data info
     */
    @GetMapping(value = "/meta-info/{uuid}")
    InstancesMetaInfoDto getInstancesMetaInfo(@PathVariable String uuid);
}
