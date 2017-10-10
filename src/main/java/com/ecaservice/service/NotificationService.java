package com.ecaservice.service;

import com.ecaservice.config.ExperimentConfig;
import com.ecaservice.config.MailConfig;
import com.ecaservice.model.EvaluationStatus;
import com.ecaservice.model.Mail;
import com.ecaservice.model.entity.Experiment;
import com.ecaservice.repository.ExperimentRepository;
import com.ecaservice.service.experiment.MailSenderService;
import com.ecaservice.service.experiment.dictionary.TemplateVariablesDictionary;
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
    private final ExperimentConfig experimentConfig;
    private final ExperimentRepository experimentRepository;

    @Autowired
    public NotificationService(TemplateEngine templateEngine,
                               MailSenderService mailSenderService,
                               ExperimentRepository experimentRepository,
                               MailConfig mailConfig, ExperimentConfig experimentConfig) {
        this.templateEngine = templateEngine;
        this.mailSenderService = mailSenderService;
        this.mailConfig = mailConfig;
        this.experimentRepository = experimentRepository;
        this.experimentConfig = experimentConfig;
    }

    /**
     * Sends experiment download reference to email.
     * @param experiment {@link Experiment} object
     */
    public void notifyByEmail(Experiment experiment) {
        try {
            Context context = new Context();
            context.setVariable(TemplateVariablesDictionary.FIRST_NAME_KEY, experiment.getFirstName());
            context.setVariable(TemplateVariablesDictionary.DOWNLOAD_URL_KEY,
                    String.format(experimentConfig.getDownloadUrl(), experiment.getUuid()));
            String message = templateEngine.process(mailConfig.getMessageTemplatePath(), context);
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
            experiment.setEvaluationStatus(EvaluationStatus.EXCEEDED);
            experiment.setErrorMessage("Number of sent retries exceeded maximum");
        } else {
            experiment.setEvaluationStatus(EvaluationStatus.FAILED);
            experiment.setErrorMessage(errorMessage);
            experiment.setRetriesToSent(experiment.getRetriesToSent() + 1);
        }
    }
}
