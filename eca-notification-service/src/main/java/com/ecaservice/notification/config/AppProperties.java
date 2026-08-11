package com.ecaservice.notification.config;

import com.ecaservice.notification.entity.NotificationEventType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

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
     * Rabbit properties
     */
    private RabbitProperties rabbit = new RabbitProperties();

    /**
     * Encrypt properties
     */
    private EncryptProperties encrypt = new EncryptProperties();

    /**
     * Email codes to event types mappings
     */
    private List<NotificationCodesMappingProperties> emailCodesMapping = new ArrayList<>();

    /**
     * Web push codes to event types mappings
     */
    private List<NotificationCodesMappingProperties> webPushCodesMapping = new ArrayList<>();

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

    /**
     * Notification codes mappings.
     */
    @Data
    public static class NotificationCodesMappingProperties {

        /**
         * Message code
         */
        private String messageCode;

        /**
         * Notification event type
         */
        private NotificationEventType eventType;
    }

    /**
     * RabbitMQ properties.
     */
    @Data
    public static class RabbitProperties {

        /**
         * Rabbit enabled?
         */
        private boolean enabled;
    }
}
