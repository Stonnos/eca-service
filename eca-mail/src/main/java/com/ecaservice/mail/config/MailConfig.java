package com.ecaservice.mail.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
     * Sender email
     */
    private String sender;

    /**
     * Encrypt properties
     */
    private EncryptConfig encrypt;

    /**
     * Encrypt config.
     */
    @Data
    public static class EncryptConfig {

        /**
         * Password for PBKDF2WithHmacSHA1 algorithm
         */
        private String password;

        /**
         * Salt for PBKDF2WithHmacSHA1 algorithm
         */
        private String salt;
    }
}
