package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.entity.ExperimentRequestStageType;
import com.ecaservice.auto.test.model.EmailMessage;
import com.ecaservice.auto.test.model.EmailTypeVisitor;
import com.ecaservice.auto.test.repository.ExperimentRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    private final ExperimentRequestService experimentRequestService;
    private final ExperimentRequestRepository experimentRequestRepository;

    /**
     * Processed email message.
     *
     * @param emailMessage - email message
     */
    public void processMessage(EmailMessage emailMessage) {
        log.info("Starting to process email message for experiment [{}]", emailMessage.getRequestId());
        var experimentRequestEntity = experimentRequestService.getByRequestId(emailMessage.getRequestId());
        emailMessage.getEmailType().handle(new EmailTypeVisitor() {
            @Override
            public void visitNewExperiment() {
                experimentRequestEntity.setNewStatusEmailReceived(true);
                experimentRequestRepository.save(experimentRequestEntity);
            }

            @Override
            public void visitInProgressExperiment() {
                experimentRequestEntity.setInProgressStatusEmailReceived(true);
                experimentRequestRepository.save(experimentRequestEntity);
            }

            @Override
            public void visitFinishedExperiment() {
                experimentRequestEntity.setFinishedStatusEmailReceived(true);
                experimentRequestEntity.setDownloadUrl(emailMessage.getDownloadUrl());
                experimentRequestEntity.setStageType(ExperimentRequestStageType.REQUEST_FINISHED);
                experimentRequestRepository.save(experimentRequestEntity);
            }

            @Override
            public void visitErrorExperiment() {
                experimentRequestEntity.setErrorStatusEmailReceived(true);
                experimentRequestService.finishWithError(experimentRequestEntity, EXPERIMENT_FINISHED_WITH_ERROR);
            }

            @Override
            public void visitTimeoutExperiment() {
                experimentRequestEntity.setTimeoutStatusEmailReceived(true);
                experimentRequestService.finishWithError(experimentRequestEntity, EXPERIMENT_FINISHED_WITH_TIMEOUT);
            }
        });
        log.info("Email message has been processed for experiment [{}]", emailMessage.getRequestId());
    }
}
