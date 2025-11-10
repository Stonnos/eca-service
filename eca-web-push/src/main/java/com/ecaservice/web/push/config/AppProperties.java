package com.ecaservice.web.push.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

/**
 * Application properties.
 *
 * @author Roman Batygin
 */
@Data
@Validated
@ConfigurationProperties("app")
public class AppProperties {

    /**
     * User notification life time in days
     */
    @NotNull
    private Integer notificationLifeTimeDays;

    /**
     * Push token validity time in minutes
     */
    @NotNull
    private Integer pushTokenValidityMinutes;

    /**
     * Encrypt properties
     */
    private EncryptProperties encrypt;

    /**
     * Rabbit properties
     */
    private RabbitProperties rabbit = new RabbitProperties();

    /**
     * RabbitMQ properties.
     */
    @Data
    public static class RabbitProperties {

        /**
         * Rabbit enabled?
         */
        private boolean enabled;

        /**
         * Queue name to receive audit events
         */
        private String queueName;
    }

    /**
     * Encrypt properties.
     */
    @Data
    public static class EncryptProperties {

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
