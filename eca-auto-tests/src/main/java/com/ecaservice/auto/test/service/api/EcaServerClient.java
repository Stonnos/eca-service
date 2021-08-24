package com.ecaservice.auto.test.service.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Eca server API client.
 *
 * @author Roman Batygin
 */
@FeignClient(name = "eca-server-api-client", url = "${auto-tests.ecaServerBaseUrl}/eca-api")
public interface EcaServerClient {

    /**
     * Downloads experiment history.
     *
     * @param token - token
     * @return resource object
     */
    @GetMapping(value = "/experiment/download/{token}")
    Resource downloadModel(@PathVariable String token);
}
