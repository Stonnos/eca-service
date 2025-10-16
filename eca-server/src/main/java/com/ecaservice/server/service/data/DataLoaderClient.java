package com.ecaservice.server.service.data;

import com.ecaservice.data.loader.dto.InstancesMetaInfoDto;
import com.ecaservice.feign.oauth.config.FeignClientOauth2Configuration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Data loader feign client interface.
 *
 * @author Roman Batygin
 */
@FeignClient(value = "eca-data-loader", path = "/api/internal/instances",
        configuration = FeignClientOauth2Configuration.class)
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
