package com.ecaservice.auto.test.service;

import com.ecaservice.auto.test.entity.ExperimentRequestEntity;
import com.ecaservice.auto.test.model.EmailMessage;
import com.ecaservice.auto.test.model.EmailTypeVisitor;
import com.ecaservice.auto.test.repository.ExperimentRequestRepository;
import com.ecaservice.common.web.exception.EntityNotFoundException;
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

            }

            @Override
            public void visitInProgressExperiment() {

            }

            @Override
            public void visitFinishedExperiment() {

            }

            @Override
            public void visitErrorExperiment() {

            }

            @Override
            public void visitTimeoutExperiment() {

            }
        });
        log.info("Email message has been processed for experiment [{}]", emailMessage.getRequestId());
    }
}