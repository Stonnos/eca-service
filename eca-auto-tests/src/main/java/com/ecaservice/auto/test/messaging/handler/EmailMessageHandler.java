package com.ecaservice.auto.test.messaging.handler;

import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestStageType;
import com.ecaservice.auto.test.model.EmailMessage;
import com.ecaservice.auto.test.model.EmailTypeVisitor;
import com.ecaservice.auto.test.repository.autotest.ExperimentRequestRepository;
import com.ecaservice.auto.test.service.ExperimentRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

    private final ExperimentRequestService experimentRequestService;
    private final ExperimentRequestRepository experimentRequestRepository;

    /**
     * Processed email message.
     *
     * @param emailMessage - email message
     */
    @ServiceActivator(inputChannel = MAIL_HANDLE_CHANNEL)
    public void handleMessage(EmailMessage emailMessage) {
        log.info("Starting to process email message [{}] for experiment [{}]",
                emailMessage.getEmailType(), emailMessage.getRequestId());
        var experimentRequestEntity = experimentRequestService.getByRequestId(emailMessage.getRequestId());
        if (ExperimentRequestStageType.EXCEEDED.equals(experimentRequestEntity.getStageType())) {
            log.warn("Can't handle email message. Got exceeded request entity with request id [{}]",
                    experimentRequestEntity.getRequestId());
        } else {
            internalHandleMessage(emailMessage, experimentRequestEntity);
        }
    }

    private void internalHandleMessage(EmailMessage emailMessage, ExperimentRequestEntity experimentRequestEntity) {
        emailMessage.getEmailType().handle(new EmailTypeVisitor() {
            @Override
            public void visitNewExperiment() {
                experimentRequestEntity.setNewStatusEmailReceived(true);
            }

            @Override
            public void visitInProgressExperiment() {
                experimentRequestEntity.setInProgressStatusEmailReceived(true);
            }

            @Override
            public void visitFinishedExperiment() {
                experimentRequestEntity.setFinishedStatusEmailReceived(true);
            }

            @Override
            public void visitErrorExperiment() {
                experimentRequestEntity.setErrorStatusEmailReceived(true);
            }

            @Override
            public void visitTimeoutExperiment() {
                experimentRequestEntity.setTimeoutStatusEmailReceived(true);
            }
        });
        experimentRequestRepository.save(experimentRequestEntity);
        log.info("Email message [{}] has been processed for experiment [{}]", emailMessage.getEmailType(),
                emailMessage.getRequestId());
    }
}
