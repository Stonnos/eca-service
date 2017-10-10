package com.ecaservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

/**
 * Experiment email configuration class.
 * @author Roman Batygin
 */
@Data
public class MailConfig {

    @Value("${experiment.mail.messageTemplatePath}")
    private String messageTemplatePath;

    @Value("${experiment.mail.from}")
    private String from;

    @Value("${experiment.mail.subject}")
    private String subject;

    @Value("${experiment.mail.maxRetriesToSent}")
    private Integer maxRetriesToSent;
}
