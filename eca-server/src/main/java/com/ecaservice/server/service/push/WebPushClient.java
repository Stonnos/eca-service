package com.ecaservice.server.service.push;

import com.ecaservice.web.dto.model.ExperimentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Web push feign client interface.
 *
 * @author Roman Batygin
 */
@FeignClient(value = "eca-web-push", path = "/push")
public interface WebPushClient {

    /**
     * Sends web push with experiment changes.
     *
     * @param experimentDto - experiment dto
     */
    @PostMapping(value = "/experiment")
    void push(ExperimentDto experimentDto);
}
