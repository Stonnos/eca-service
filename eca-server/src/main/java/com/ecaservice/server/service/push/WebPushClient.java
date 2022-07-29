package com.ecaservice.server.service.push;

import com.ecaservice.web.dto.model.push.PushRequestDto;
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
     * Sends web push.
     *
     * @param pushRequestDto - push request dto
     */
    @PostMapping(value = "/send")
    void sendPush(PushRequestDto pushRequestDto);
}
