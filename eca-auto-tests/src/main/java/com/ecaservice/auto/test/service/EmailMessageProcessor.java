package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.entity.ExperimentRequestEntity;
import com.ecaservice.auto.test.model.EmailMessage;
import com.ecaservice.auto.test.model.EmailTypeVisitor;
import com.ecaservice.auto.test.repository.ExperimentRequestRepository;
import com.ecaservice.common.web.exception.EntityNotFoundException;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Email message processor service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailMessageProcessor {

    private static final String EXPERIMENT_FINISHED_WITH_TIMEOUT = "Experiment finished with timeout";
    private static final String EXPERIMENT_FINISHED_WITH_ERROR = "Experiment finished with error";

    private final ExperimentRequestRepository experimentRequestRepository;

    /**
     * Processed email message.
     *
     * @param emailMessage - email message
     */
    public void processMessage(EmailMessage emailMessage) {
        log.info("Starting to process email message for experiment [{}]", emailMessage.getRequestId());
        var experimentRequestEntity = experimentRequestRepository.findByRequestId(emailMessage.getRequestId())
                .orElseThrow(
                        () -> new EntityNotFoundException(ExperimentRequestEntity.class, emailMessage.getRequestId()));
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
                experimentRequestEntity.setDownloadUrl(emailMessage.getDownloadUrl());
            }

            @Override
            public void visitErrorExperiment() {
                experimentRequestEntity.setErrorStatusEmailReceived(true);
                experimentRequestEntity.setDetails(EXPERIMENT_FINISHED_WITH_ERROR);
                finishWithError();
            }

            @Override
            public void visitTimeoutExperiment() {
                experimentRequestEntity.setTimeoutStatusEmailReceived(true);
                experimentRequestEntity.setDetails(EXPERIMENT_FINISHED_WITH_TIMEOUT);
                finishWithError();
            }

            void finishWithError() {
                experimentRequestEntity.setExecutionStatus(ExecutionStatus.ERROR);
                experimentRequestEntity.setTestResult(TestResult.ERROR);
                experimentRequestEntity.setFinished(LocalDateTime.now());
            }
        });
        experimentRequestRepository.save(experimentRequestEntity);
        log.info("Email message has been processed for experiment [{}]", emailMessage.getRequestId());
    }
}
