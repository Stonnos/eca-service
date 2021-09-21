package com.ecaservice.mail.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Email configuration.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@ConfigurationProperties("mail-config")
public class MailConfig {

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
     * Maximum page size for paging requests
     */
    @NotNull
    private Integer maxPageSize;

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
