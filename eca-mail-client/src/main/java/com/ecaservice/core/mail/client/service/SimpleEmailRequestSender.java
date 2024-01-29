package com.ecaservice.core.mail.client.service;

import com.ecaservice.core.redelivery.annotation.Retry;
import com.ecaservice.core.redelivery.annotation.Retryable;
import com.ecaservice.notification.dto.EmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.ecaservice.core.redelivery.config.RedeliveryCoreAutoConfiguration.FEIGN_EXCEPTION_STRATEGY;

/**
 * Simple email request sender.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@Retryable
@RequiredArgsConstructor
public class SimpleEmailRequestSender implements EmailRequestSender {

    private final EmailClient emailClient;

    @Override
    @Retry(value = "emailRequest", messageConverter = "emailRequestConverter",
            exceptionStrategy = FEIGN_EXCEPTION_STRATEGY, requestIdKey = "#emailRequest.requestId")
    public void sendEmail(EmailRequest emailRequest) {
        log.info("Starting to sent email request with code [{}]", emailRequest.getTemplateCode());
        var emailResponse = emailClient.sendEmail(emailRequest);
        log.info("Email [{}] has been sent with request id [{}]", emailRequest.getTemplateCode(),
                emailResponse.getRequestId());
    }
}
