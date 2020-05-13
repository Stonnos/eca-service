package com.ecaservice.service.experiment.mail;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "eca-mail", path = "/notifications")
public interface EmailClient {

    @PostMapping(value = "/email-request")
    EmailResponse sendEmail(@RequestBody EmailRequest emailRequest);
}
