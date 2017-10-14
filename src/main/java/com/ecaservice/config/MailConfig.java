package com.ecaservice.config;

import com.ecaservice.model.experiment.ExperimentStatus;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Experiment email configuration class.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("experiment.mail")
public class MailConfig {

    /**
     * Message templates map
     */
    private Map<ExperimentStatus, String> messageTemplatesMap = new HashMap<>();

    /**
     * Source email
     */
    private String from;

    /**
     * Subject string
     */
    private String subject;

    /**
     * Maximum number of failed attempts to sent
     */
    private Integer maxFailedAttemptsToSent;
}
