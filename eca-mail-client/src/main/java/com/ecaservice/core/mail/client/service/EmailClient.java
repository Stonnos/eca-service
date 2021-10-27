package com.ecaservice.core.mail.client.service;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Email feign client interface.
 */
@FeignClient(value = "eca-mail", path = "/emails")
public interface EmailClient {

    /**
     * Sends email request to email service.
     *
     * @param emailRequest - email request
     * @return email response
     */
    @PostMapping(value = "/email-request")
    EmailResponse sendEmail(@RequestBody EmailRequest emailRequest);
}
