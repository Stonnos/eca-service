package com.ecaservice.core.mail.client.service;

import com.ecaservice.core.redelivery.annotation.Retry;
import com.ecaservice.core.redelivery.annotation.Retryable;
import com.ecaservice.notification.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Roman Batygin
 */
@Slf4j
@Service
@Retryable
@RequiredArgsConstructor
public class EmailRequestSender {

    private final EmailClient emailClient;

    /**
     * Sends email request to eca-mail service.
     *
     * @param emailRequest - email request
     */
    @Retry(value = "emailRequest", messageConverter = "emailRequestConverter",
            exceptionStrategy = "feignExceptionStrategy")
    public void sendEmail(EmailRequest emailRequest) {
        log.info("Starting to sent email request with code [{}]", emailRequest.getTemplateCode());
        var emailResponse = emailClient.sendEmail(emailRequest);
        log.info("Email [{}] has been sent with request id [{}]", emailRequest.getTemplateCode(),
                emailResponse.getRequestId());
    }
}
