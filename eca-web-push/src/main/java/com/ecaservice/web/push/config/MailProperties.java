package com.ecaservice.web.push.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Email configuration.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@ConfigurationProperties("mail")
public class MailProperties {

    /**
     * Number of failed attempts to send experiment results
     */
    private Integer maxFailedAttemptsToSent;

    /**
     * Page size for emails sending
     */
    @NotNull
    private Integer pageSize;

    /**
     * Delay in seconds for sent email job
     */
    @NotNull
    private Integer delaySeconds;

    /**
     * Sender email
     */
    @NotEmpty
    private String sender;

    /**
     * Rabbit properties
     */
    private RabbitProperties rabbit = new RabbitProperties();
}
