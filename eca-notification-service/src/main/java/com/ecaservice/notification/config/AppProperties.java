package com.ecaservice.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

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
     * Encrypt properties
     */
    private EncryptProperties encrypt = new EncryptProperties();

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
