package com.ecaservice.auto.test.messaging.handler;

import com.ecaservice.auto.test.entity.autotest.EmailTestStepEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.entity.autotest.RequestStageType;
import com.ecaservice.auto.test.model.EmailMessage;
import com.ecaservice.auto.test.model.EmailTypeVisitor;
import com.ecaservice.auto.test.repository.autotest.EmailTestStepRepository;
import com.ecaservice.auto.test.repository.autotest.ExperimentRequestRepository;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import com.ecaservice.test.common.service.TestResultsMatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

import static com.ecaservice.auto.test.config.mail.Channels.MAIL_HANDLE_CHANNEL;
import static com.ecaservice.test.common.util.Utils.calculateTestResult;

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
            emailStepEntity.setMessageReceived(true);
            try {
                compareAndMatchResults(experimentRequestEntity, emailMessage, emailStepEntity);
                emailStepEntity.setExecutionStatus(ExecutionStatus.FINISHED);
                log.info("Email message [{}] has been processed for experiment [{}] with test result: [{}]",
                        emailMessage.getEmailType(), emailMessage.getRequestId(), emailStepEntity.getTestResult());
            } catch (Exception ex) {
                log.error("Error while test step [{}] email [{}] handling for experiment request [{}]: {}",
                        emailStepEntity.getId(), emailStepEntity.getEmailType(), experimentRequestEntity.getRequestId(),
                        ex.getMessage());
                emailStepEntity.setDetails(ex.getMessage());
                emailStepEntity.setTestResult(TestResult.ERROR);
                emailStepEntity.setExecutionStatus(ExecutionStatus.ERROR);
            } finally {
                emailStepEntity.setFinished(LocalDateTime.now());
                emailTestStepRepository.save(emailStepEntity);
            }
        }
    }

    private void compareAndMatchResults(ExperimentRequestEntity experimentRequestEntity,
                                        EmailMessage emailMessage,
                                        EmailTestStepEntity emailStepEntity) {
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
        emailStepEntity.setTestResult(calculateTestResult(matcher));
        emailStepEntity.setTotalMatched(matcher.getTotalMatched());
        emailStepEntity.setTotalNotMatched(matcher.getTotalNotMatched());
        emailStepEntity.setTotalNotFound(matcher.getTotalNotFound());
    }
}
