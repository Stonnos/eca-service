package com.ecaservice.auto.test.messaging.handler;

import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.event.model.EmailTestStepEvent;
import com.ecaservice.auto.test.model.EmailMessage;
import com.ecaservice.auto.test.repository.autotest.EmailTestStepRepository;
import com.ecaservice.auto.test.repository.autotest.ExperimentRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;

import static com.ecaservice.auto.test.config.mail.Channels.MAIL_HANDLE_CHANNEL;

/**
 * Email message handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@ConditionalOnProperty(value = "mail.enabled", havingValue = "true")
@RequiredArgsConstructor
public class EmailMessageHandler {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ExperimentRequestRepository experimentRequestRepository;
    private final EmailTestStepRepository emailTestStepRepository;

    /**
     * Processed email message.
     *
     * @param emailMessage - email message
     */
    @ServiceActivator(inputChannel = MAIL_HANDLE_CHANNEL)
    public void handleMessage(EmailMessage emailMessage) {
        log.info("Starting to process email message [{}] for experiment [{}]",
                emailMessage.getEmailType(), emailMessage.getRequestId());
        var experimentRequestEntity = experimentRequestRepository.findByRequestId(emailMessage.getRequestId());
        if (experimentRequestEntity == null) {
            log.warn("Can't find experiment request entity with request id [{}] for email message processing",
                    emailMessage.getRequestId());
        } else if (RequestStageType.EXCEEDED.equals(experimentRequestEntity.getStageType())) {
            log.warn("Can't handle email message. Got exceeded request entity with request id [{}]",
                    experimentRequestEntity.getRequestId());
        } else {
            internalHandleMessage(emailMessage, experimentRequestEntity);
        }
    }

    private void internalHandleMessage(EmailMessage emailMessage, ExperimentRequestEntity experimentRequestEntity) {

        var emailStepEntity = emailTestStepRepository.findByEvaluationRequestEntityAndEmailType(experimentRequestEntity,
                emailMessage.getEmailType());
        if (emailStepEntity == null) {
            log.warn("Email step entity not found for experiment with request id [{}], email type [{}]",
                    experimentRequestEntity.getRequestId(), emailMessage.getEmailType());
        } else {
            applicationEventPublisher.publishEvent(
                    new EmailTestStepEvent(this, emailMessage, emailStepEntity, experimentRequestEntity));
        }
    }
}
