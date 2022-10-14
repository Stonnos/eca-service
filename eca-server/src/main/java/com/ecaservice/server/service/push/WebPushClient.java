package com.ecaservice.server.service.push;

import com.ecaservice.web.push.dto.AbstractPushRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Web push feign client interface.
 *
 * @author Roman Batygin
 */
@FeignClient(value = "eca-web-push", path = "/api/push")
public interface WebPushClient {

    /**
     * Sends web push notification.
     *
     * @param pushRequest - push request
     */
    @PostMapping(value = "/send")
    void sendPush(AbstractPushRequest pushRequest);
}
