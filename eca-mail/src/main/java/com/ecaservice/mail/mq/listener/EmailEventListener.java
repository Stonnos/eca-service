package com.ecaservice.mail.mq.listener;

import com.ecaservice.mail.service.EmailService;
import com.ecaservice.notification.dto.EmailRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;

/**
 * Email message listener.
 *
 * @author Roman Batygin
 */
@Slf4j
@Validated
@Component
@ConditionalOnProperty(value = "mail-config.rabbit.enabled", havingValue = "true")
@RequiredArgsConstructor
public class EmailEventListener {

    private final EmailService emailService;


    /**
     * Handles email request message.
     *
     * @param emailRequest - email request
     */
    @RabbitListener(queues = "${mail-config.rabbit.queueName}")
    public void handleEmailEvent(@Valid @RequestBody EmailRequest emailRequest) {
        putMdc(TX_ID, emailRequest.getCorrelationId());
        log.info("Received email request message [{}]", emailRequest.getRequestId());
        emailService.saveEmail(emailRequest);
    }
}
