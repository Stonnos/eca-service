package com.ecaservice.service.experiment.mail;

import com.ecaservice.config.MailConfig;
import com.ecaservice.model.entity.Email;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.repository.EmailRepository;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.experiment.visitor.EmailTemplateVisitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
    private final MailSenderService mailSenderService;
    private final MailConfig mailConfig;
    private final EmailTemplateVisitor statusTemplateVisitor;
    private final ExperimentRepository experimentRepository;
    private final EmailRepository emailRepository;

    /**
     * Constructor with dependency spring injection.
     *
     * @param templateEngine        - template engine bean
     * @param mailSenderService     - mail sender service bean
     * @param experimentRepository  - experiment repository bean
     * @param mailConfig            - mail config bean
     * @param statusTemplateVisitor - email template visitor bean
     * @param emailRepository       - email repository bean
     */
    @Inject
    public NotificationService(TemplateEngine templateEngine,
                               MailSenderService mailSenderService,
                               ExperimentRepository experimentRepository,
                               MailConfig mailConfig,
                               EmailTemplateVisitor statusTemplateVisitor,
                               EmailRepository emailRepository) {
        this.templateEngine = templateEngine;
        this.mailSenderService = mailSenderService;
        this.mailConfig = mailConfig;
        this.experimentRepository = experimentRepository;
        this.statusTemplateVisitor = statusTemplateVisitor;
        this.emailRepository = emailRepository;
    }

    /**
     * Sends experiment to specified email. If email message hasn't been send to specified email
     * then number of failed attempts to sent is increased by one. If number of failed
     * attempts to sent is greater or equals to N then EXCEEDED status is assigned to experiment.
     *
     * @param experiment experiment to sent {@link Experiment}
     */
    public void sendExperimentResults(Experiment experiment) {
        try {
            notifyByEmail(experiment);
            experiment.setSentDate(LocalDateTime.now());
        } catch (Exception ex) {
            log.error("There was an error: {}", ex.getMessage());
            handleErrorSent(experiment);
        } finally {
            experimentRepository.save(experiment);
        }
    }

    /**
     * Sends email message based on experiment status.
     *
     * @param experiment - experiment object
     */
    public void notifyByEmail(Experiment experiment) {
        Email email = createEmail(experiment);
        emailRepository.save(email);
        mailSenderService.sendEmail(email);
    }

    private String buildEmailMessage(Experiment experiment) {
        String template = mailConfig.getMessageTemplatesMap().get(experiment.getExperimentStatus());
        Context context = experiment.getExperimentStatus().handle(statusTemplateVisitor, experiment);
        return templateEngine.process(template, context);
    }

    private Email createEmail(Experiment experiment) {
        Email email = new Email();
        email.setSender(mailConfig.getFrom());
        email.setReceiver(experiment.getEmail());
        email.setSaveDate(LocalDateTime.now());
        email.setExperiment(experiment);
        email.setSubject(mailConfig.getSubject());
        email.setMessage(buildEmailMessage(experiment));
        return email;
    }

    private void handleErrorSent(Experiment experiment) {
        if (experiment.getFailedAttemptsToSent() >= mailConfig.getMaxFailedAttemptsToSent()) {
            experiment.setExperimentStatus(ExperimentStatus.EXCEEDED);
        } else {
            experiment.setFailedAttemptsToSent(experiment.getFailedAttemptsToSent() + 1);
        }
    }
}
