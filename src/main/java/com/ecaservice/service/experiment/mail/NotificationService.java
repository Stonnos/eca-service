package com.ecaservice.service.experiment.mail;

import com.ecaservice.config.MailConfig;
import com.ecaservice.dto.mail.EmailRequest;
import com.ecaservice.dto.mail.EmailResponse;
import com.ecaservice.dto.mail.ResponseStatus;
import com.ecaservice.model.entity.EmailRequestEntity;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.repository.EmailRequestRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.experiment.visitor.EmailTemplateVisitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.inject.Inject;
import java.time.LocalDateTime;

/**
 * Notification service.
 *
 * @author Roman Batygin
 */
@Slf4j
@Service
public class NotificationService {

    private final TemplateEngine templateEngine;
    private final MailConfig mailConfig;
    private final EmailTemplateVisitor statusTemplateVisitor;
    private final WebServiceTemplate webServiceTemplate;
    private final EmailRequestRepository emailRequestRepository;
    private final ExperimentRepository experimentRepository;

    /**
     * Constructor with dependency spring injection.
     *
     * @param templateEngine         - template engine bean
     * @param mailConfig             - mail config bean
     * @param statusTemplateVisitor  - email template visitor bean
     * @param webServiceTemplate     - web service template bean
     * @param emailRequestRepository - email request repository bean
     * @param experimentRepository   - experiment repository bean
     */
    @Inject
    public NotificationService(TemplateEngine templateEngine,
                               MailConfig mailConfig,
                               EmailTemplateVisitor statusTemplateVisitor,
                               WebServiceTemplate webServiceTemplate,
                               EmailRequestRepository emailRequestRepository, ExperimentRepository experimentRepository) {
        this.templateEngine = templateEngine;
        this.mailConfig = mailConfig;
        this.statusTemplateVisitor = statusTemplateVisitor;
        this.webServiceTemplate = webServiceTemplate;
        this.emailRequestRepository = emailRequestRepository;
        this.experimentRepository = experimentRepository;
    }

    /**
     * Sends email message based on experiment status.
     *
     * @param experiment - experiment object
     */
    public void notifyByEmail(Experiment experiment) {
        EmailRequest emailRequest = createEmailRequest(experiment);
        EmailRequestEntity emailRequestEntity = new EmailRequestEntity();
        try {
            EmailResponse emailResponse = (EmailResponse) webServiceTemplate.marshalSendAndReceive(mailConfig.getServiceUrl(), emailRequest);
            emailRequestEntity.setRequestId(emailResponse.getRequestId());
            emailRequestEntity.setStatus(emailResponse.getStatus());
            experiment.setSentDate(LocalDateTime.now());
            experimentRepository.save(experiment);
        } catch (Exception ex) {
            log.error("There was an error while sending email request for experiment with id [{}]: {}", experiment.getId(), ex.getMessage());
            emailRequestEntity.setStatus(ResponseStatus.ERROR);
            emailRequestEntity.setErrorMessage(ex.getMessage());
        } finally {
            emailRequestEntity.setCreationDate(LocalDateTime.now());
            emailRequestRepository.save(emailRequestEntity);
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
