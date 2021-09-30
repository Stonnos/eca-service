package com.ecaservice.server.service.experiment.mail;

import com.ecaservice.notification.dto.EmailRequest;
import com.ecaservice.notification.dto.EmailResponse;
import com.ecaservice.server.model.entity.Experiment;
import com.ecaservice.server.service.experiment.visitor.EmailTemplateVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final EmailClient emailClient;

    /**
     * Sends email message based on experiment status.
     *
     * @param experiment - experiment object
     */
    public void notifyByEmail(Experiment experiment) {
        log.info("Starting to send email request for experiment [{}] with status [{}].", experiment.getRequestId(),
                experiment.getRequestStatus());
        EmailRequest emailRequest = experiment.getRequestStatus().handle(statusTemplateVisitor, experiment);
        EmailResponse emailResponse = emailClient.sendEmail(emailRequest);
        log.info("Email request [{}] has been successfully sent for experiment [{}], experiment status [{}].",
                emailResponse.getRequestId(), experiment.getRequestId(), experiment.getRequestStatus());
    }
}
