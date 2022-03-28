package com.ecaservice.auto.test.messaging.handler;

import com.ecaservice.auto.test.entity.autotest.EmailTestStepEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.model.EmailMessage;
import com.ecaservice.auto.test.model.EmailTypeVisitor;
import com.ecaservice.auto.test.repository.autotest.EmailTestStepRepository;
import com.ecaservice.auto.test.service.EvaluationRequestService;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.service.TestResultsMatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

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

    private final EvaluationRequestService evaluationRequestService;
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
        var experimentRequestEntity = evaluationRequestService.getByRequestId(emailMessage.getRequestId());
        if (RequestStageType.EXCEEDED.equals(experimentRequestEntity.getStageType())) {
            log.warn("Can't handle email message. Got exceeded request entity with request id [{}]",
                    experimentRequestEntity.getRequestId());
        } else {
            internalHandleMessage(emailMessage, experimentRequestEntity);
        }
    }

    private void internalHandleMessage(EmailMessage emailMessage, ExperimentRequestEntity experimentRequestEntity) {

        var emailStepEntity = emailTestStepRepository.findByEvaluationRequestEntityAndEmailType(experimentRequestEntity,
                emailMessage.getEmailType())
                .orElseThrow(() -> new EntityNotFoundException(EmailTestStepEntity.class,
                        String.format("Request id [%d], email type [%s]", experimentRequestEntity.getId(),
                                emailMessage.getEmailType())));
        emailStepEntity.setMessageReceived(true);
        var matcher = new TestResultsMatcher();
        emailMessage.getEmailType().handle(new EmailTypeVisitor() {
            @Override
            public void visitFinishedExperiment() {
                Assert.notNull(experimentRequestEntity.getDownloadUrl(),
                        String.format("Expected not null download url for experiment [%s]",
                                experimentRequestEntity.getRequestId()));
                emailStepEntity.setExpectedDownloadUrl(experimentRequestEntity.getDownloadUrl());
                emailStepEntity.setActualDownloadUrl(emailMessage.getDownloadUrl());
                var downloadUrlMatchResult = matcher.compareAndMatch(experimentRequestEntity.getDownloadUrl(),
                        emailMessage.getDownloadUrl());
                emailStepEntity.setDownloadUrlMatchResult(downloadUrlMatchResult);
            }
        });
        emailStepEntity.setExecutionStatus(ExecutionStatus.FINISHED);
        emailStepEntity.setFinished(LocalDateTime.now());
        log.info("Email message [{}] has been processed for experiment [{}]", emailMessage.getEmailType(),
                emailMessage.getRequestId());
    }
}
