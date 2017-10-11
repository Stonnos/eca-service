package com.ecaservice.service.experiment;

import com.ecaservice.config.MailConfig;
import com.ecaservice.model.Mail;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.model.experiment.ExperimentStatus;
import com.ecaservice.repository.ExperimentRepository;
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
    private final ExperimentStatusTemplateVisitor statusTemplateVisitor;
    private final ExperimentRepository experimentRepository;

    @Autowired
    public NotificationService(TemplateEngine templateEngine,
                               MailSenderService mailSenderService,
                               ExperimentRepository experimentRepository,
                               MailConfig mailConfig,
                               ExperimentStatusTemplateVisitor statusTemplateVisitor) {
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
            Mail mail = new Mail();
            mail.setFrom(mailConfig.getFrom());
            mail.setTo(experiment.getEmail());
            mail.setSubject(mailConfig.getSubject());
            mail.setMessage(message);
            mail.setHtml(true);
            mailSenderService.sendEmail(mail);
            experiment.setSentDate(LocalDateTime.now());
        } catch (Exception ex) {
            log.warn(ex.getMessage());
            populateFailedSent(experiment, ex.getMessage());
        } finally {
            experimentRepository.save(experiment);
        }
    }

    private void populateFailedSent(Experiment experiment, String errorMessage) {
        if (experiment.getRetriesToSent() >= mailConfig.getMaxRetriesToSent()) {
            experiment.setExperimentStatus(ExperimentStatus.EXCEEDED);
            experiment.setErrorMessage("Number of sent retries exceeded maximum");
        } else {
            experiment.setExperimentStatus(ExperimentStatus.FAILED);
            experiment.setRetriesToSent(experiment.getRetriesToSent() + 1);
        }
    }
}
