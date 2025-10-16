package com.ecaservice.core.mail.client.service;

import com.ecaservice.core.mail.client.config.EcaMailClientProperties;
import com.ecaservice.core.redelivery.annotation.Retry;
import com.ecaservice.core.redelivery.annotation.Retryable;
import com.ecaservice.notification.dto.EmailRequest;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import static com.ecaservice.common.web.util.LogHelper.TX_ID;
import static com.ecaservice.common.web.util.LogHelper.putMdc;
import static com.ecaservice.core.mail.client.config.rabbit.MailClientRabbitConfiguration.MAIL_CLIENT_RABBIT_TEMPLATE;

/**
 * Simple email request sender.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@Retryable
public class SimpleEmailRequestSender implements EmailRequestSender {

    private final EcaMailClientProperties ecaMailClientProperties;
    private final RabbitTemplate rabbitTemplate;

    /**
     * Constructor with parameters.
     *
     * @param ecaMailClientProperties - eca mail client properties
     * @param rabbitTemplate          - rabbit template
     */
    public SimpleEmailRequestSender(EcaMailClientProperties ecaMailClientProperties,
                                    @Qualifier(MAIL_CLIENT_RABBIT_TEMPLATE) RabbitTemplate rabbitTemplate) {
        this.ecaMailClientProperties = ecaMailClientProperties;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @NewSpan
    @Retry(value = "emailRequest", messageConverter = "emailRequestConverter", requestIdKey = "#emailRequest.requestId")
    public void sendEmail(EmailRequest emailRequest) {
        putMdc(TX_ID, emailRequest.getCorrelationId());
        String queueName = ecaMailClientProperties.getRabbit().getQueueName();
        log.info("Starting to sent email request [{}], correlation id [{}], code [{}] to queue [{}]",
                emailRequest.getRequestId(), emailRequest.getCorrelationId(), emailRequest.getTemplateCode(),
                queueName);
        rabbitTemplate.convertAndSend(queueName, emailRequest);
        log.info("Email request [{}], correlation id [{}], code [{}] has been sent to queue [{}]",
                emailRequest.getRequestId(), emailRequest.getCorrelationId(), emailRequest.getTemplateCode(),
                queueName);
    }
}
