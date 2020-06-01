package com.ecaservice.mail.config;

import com.ecaservice.notification.dto.EmailTemplateType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Email configuration.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("mail-config")
public class MailConfig {

    /**
     * Number of failed attempts to send experiment results
     */
    private Integer maxFailedAttemptsToSent;

    /**
     * Page size for emails sending
     */
    private Integer pageSize;

    /**
     * Delay in seconds for sent email job
     */
    private Integer delaySeconds;

    /**
     * Message templates map
     */
    private Map<EmailTemplateType, String> messageTemplatesMap;

    /**
     * Sender email
     */
    private String sender;

    /**
     * Subject string
     */
    private String subject;
}
