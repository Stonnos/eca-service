package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.core.mail.client.event.model.EmailEvent;
import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.service.experiment.visitor.EmailTemplateVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Notification service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailTemplateVisitor statusTemplateVisitor;
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * Sends email message based on experiment status.
     *
     * @param experiment - experiment object
     */
    public void notifyByEmail(Experiment experiment) {
        log.info("Starting to send email request for experiment [{}] with status [{}].", experiment.getRequestId(),
                experiment.getRequestStatus());
        Assert.notNull(experiment.getEmail(),
                String.format("Experiment [%s] email must be not empty", experiment.getEmail()));
        EmailRequest emailRequest = experiment.getRequestStatus().handle(statusTemplateVisitor, experiment);
        EmailEvent emailEvent = new EmailEvent(this, emailRequest);
        applicationEventPublisher.publishEvent(emailEvent);
        log.info("Email request has been sent for experiment [{}], experiment status [{}].", experiment.getRequestId(),
                experiment.getRequestStatus());
    }
}
