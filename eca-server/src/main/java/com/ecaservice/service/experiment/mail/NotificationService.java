package com.ecaservice.service.experiment.mail;

import com.ecaservice.config.MailConfig;
import com.ecaservice.dto.mail.EmailRequest;
import com.ecaservice.dto.mail.EmailResponse;
import com.ecaservice.dto.mail.ResponseStatus;
import com.ecaservice.exception.notification.NotificationServiceException;
import com.ecaservice.model.entity.EmailRequestEntity;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.repository.EmailRequestRepository;
import com.ecaservice.service.experiment.visitor.EmailTemplateVisitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

/**
 * Notification service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final TemplateEngine templateEngine;
    private final MailConfig mailConfig;
    private final EmailTemplateVisitor statusTemplateVisitor;
    private final WebServiceTemplate notificationWebServiceTemplate;
    private final EmailRequestRepository emailRequestRepository;
    private final NotificationResponseErrorHandler errorHandler;

    /**
     * Sends email message based on experiment status.
     *
     * @param experiment - experiment object
     */
    public void notifyByEmail(Experiment experiment) {
        log.info("Starting to send email request for experiment [{}] with status [{}].", experiment.getUuid(),
                experiment.getExperimentStatus());
        if (!Boolean.TRUE.equals(mailConfig.getEnabled())) {
            log.warn("Notifications sending is disabled.");
        } else {
            EmailRequest emailRequest = createEmailRequest(experiment);
            EmailRequestEntity emailRequestEntity = new EmailRequestEntity();
            emailRequestEntity.setExperiment(experiment);
            emailRequestEntity.setRequestDate(LocalDateTime.now());
            try {
                EmailResponse emailResponse =
                        (EmailResponse) notificationWebServiceTemplate.marshalSendAndReceive(mailConfig.getServiceUrl(),
                                emailRequest);
                log.trace("Received response [{}] from '{}'.", emailResponse, mailConfig.getServiceUrl());
                emailRequestEntity.setRequestId(emailResponse.getRequestId());
                emailRequestEntity.setResponseStatus(emailResponse.getStatus());
                if (errorHandler.hasError(emailResponse)) {
                    errorHandler.handleError(emailResponse);
                } else {
                    log.info("Email request has been successfully sent for experiment [{}], experiment status [{}].",
                            experiment.getUuid(), experiment.getExperimentStatus());
                }
            }
            catch (NotificationServiceException ex) {
                emailRequestEntity.setErrorMessage(ex.getMessage());
                throw ex;
            }
            catch (Exception ex) {
                emailRequestEntity.setResponseStatus(ResponseStatus.ERROR);
                emailRequestEntity.setErrorMessage(ex.getMessage());
                throw ex;
            } finally {
                emailRequestRepository.save(emailRequestEntity);
            }
        }
    }

    private String buildEmailMessage(Experiment experiment) {
        String template = mailConfig.getMessageTemplatesMap().get(experiment.getExperimentStatus());
        Context context = experiment.getExperimentStatus().handle(statusTemplateVisitor, experiment);
        return templateEngine.process(template, context);
    }

    private EmailRequest createEmailRequest(Experiment experiment) {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setSender(mailConfig.getFrom());
        emailRequest.setReceiver(experiment.getEmail());
        emailRequest.setSubject(mailConfig.getSubject());
        emailRequest.setMessage(buildEmailMessage(experiment));
        emailRequest.setHtml(true);
        return emailRequest;
    }
}
