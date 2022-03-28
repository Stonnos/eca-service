package com.ecaservice.auto.test.service.step;

import com.ecaservice.auto.test.entity.autotest.EmailTestStepEntity;
import com.ecaservice.auto.test.entity.autotest.ExperimentRequestEntity;
import com.ecaservice.auto.test.event.model.EmailTestStepEvent;
import com.ecaservice.auto.test.model.EmailMessage;
import com.ecaservice.auto.test.model.EmailTypeVisitor;
import com.ecaservice.auto.test.repository.autotest.EmailTestStepRepository;
import com.ecaservice.test.common.model.ExecutionStatus;
import com.ecaservice.test.common.model.TestResult;
import com.ecaservice.test.common.service.TestResultsMatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

import static com.ecaservice.test.common.util.Utils.calculateTestResult;

/**
 * Email test step handler.
 *
 * @author Roman Batygin
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "mail.enabled", havingValue = "true")
public class EmailTestStepHandler extends AbstractTestStepHandler<EmailTestStepEvent> {

    private final EmailTestStepRepository emailTestStepRepository;

    /**
     * Constructor with spring dependency injection.
     *
     * @param emailTestStepRepository - email test step repository
     */
    public EmailTestStepHandler(EmailTestStepRepository emailTestStepRepository) {
        super(EmailTestStepEvent.class);
        this.emailTestStepRepository = emailTestStepRepository;
    }

    @Override
    @EventListener
    public void handle(EmailTestStepEvent event) {
        var emailMessage = event.getEmailMessage();
        var emailStepEntity = event.getEmailTestStepEntity();
        var experimentRequestEntity = event.getExperimentRequestEntity();
        log.info("Starting to handle email step [[{}], {}] for experiment with request id [{}]",
                emailStepEntity.getId(), emailStepEntity.getEmailType(), experimentRequestEntity.getRequestId());
        try {
            emailStepEntity.setMessageReceived(true);
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
