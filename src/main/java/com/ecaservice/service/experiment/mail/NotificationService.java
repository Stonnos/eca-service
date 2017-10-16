package com.ecaservice.service.experiment.mail;

import com.ecaservice.config.MailConfig;
import com.ecaservice.model.Mail;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.experiment.visitors.EmailTemplateVisitor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

/**
 * Notification service.
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

    /**
     * Constructor with dependency spring injection.
     *
     * @param templateEngine        {@link TemplateEngine} bean
     * @param mailSenderService     {@link MailSenderService} bean
     * @param experimentRepository  {@link ExperimentRepository} bean
     * @param mailConfig            {@link MailConfig} bean
     * @param statusTemplateVisitor {@link EmailTemplateVisitor} bean
     */
    @Autowired
    public NotificationService(TemplateEngine templateEngine,
                               MailSenderService mailSenderService,
                               ExperimentRepository experimentRepository,
                               MailConfig mailConfig,
                               EmailTemplateVisitor statusTemplateVisitor) {
        this.templateEngine = templateEngine;
        this.mailSenderService = mailSenderService;
        this.mailConfig = mailConfig;
        this.experimentRepository = experimentRepository;
        this.statusTemplateVisitor = statusTemplateVisitor;
    }

    /**
     * Sends experiment download reference to email.
     * @param experiment {@link Experiment} object
     */
    public void notifyByEmail(Experiment experiment) {
        try {
            String template = mailConfig.getMessageTemplatesMap().get(experiment.getExperimentStatus());
            Context context = experiment.getExperimentStatus().handle(statusTemplateVisitor, experiment);
            String message = templateEngine.process(template, context);
            Mail mail = new Mail(mailConfig.getFrom(), experiment.getEmail(), mailConfig.getSubject(),
                    message, true);
            mailSenderService.sendEmail(mail);
            experiment.setSentDate(LocalDateTime.now());
        } catch (Exception ex) {
            log.warn(ex.getMessage());
            populateErrorSent(experiment, ex.getMessage());
        } finally {
            experimentRepository.save(experiment);
        }
    }

    private void populateErrorSent(Experiment experiment, String errorMessage) {
        if (experiment.getFailedAttemptsToSent() >= mailConfig.getMaxFailedAttemptsToSent()) {
            experiment.setExperimentStatus(ExperimentStatus.EXCEEDED);
            experiment.setErrorMessage("Number of sent retries exceeded maximum.");
        } else {
            experiment.setErrorMessage(errorMessage);
            experiment.setFailedAttemptsToSent(experiment.getFailedAttemptsToSent() + 1);
        }
    }
}
