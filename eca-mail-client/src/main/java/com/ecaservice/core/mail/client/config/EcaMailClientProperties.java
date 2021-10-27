package com.ecaservice.core.mail.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Eca mail client properties.
 *
 * @author Roman Batygin
 */
@Data
@ConfigurationProperties("mail.client")
public class EcaMailClientProperties {

    private static final int DEFAULT_THREAD_POOL_SIZE = 1;
    private static final int DEFAULT_PAGE_SIZE = 25;
    private static final long DEFAULT_REDELIVERY_INTERVAL_MILLIS = 60000L;
    private static final String DEFAULT_SALT = "s@lt#";
    private static final String DEFAULT_PASSWORD = "passw0rd@!";

    /**
     * Is email sending enabled?
     */
    private Boolean enabled;

    /**
     * Use async sending?
     */
    private Boolean async;

    /**
     * Thread pool size
     */
    private Integer threadPoolSize = DEFAULT_THREAD_POOL_SIZE;

    /**
     * Page size (used for pagination)
     */
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    /**
     * Enabled redelivery?
     */
    private Boolean redelivery;

    /**
     * Redelivery interval in millis
     */
    private Long redeliveryIntervalMillis = DEFAULT_REDELIVERY_INTERVAL_MILLIS;

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
         * Encrypt enabled?
         */
        private Boolean enabled;

        /**
         * Password for PBKDF2WithHmacSHA1 algorithm
         */
        private String password = DEFAULT_PASSWORD;

        /**
         * Salt for PBKDF2WithHmacSHA1 algorithm
         */
        private String salt = DEFAULT_SALT;
    }
}
